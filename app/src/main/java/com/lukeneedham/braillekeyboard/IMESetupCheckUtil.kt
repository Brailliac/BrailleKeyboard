package com.lukeneedham.braillekeyboard

/**
 * Created by Luke on 02/08/2018.
 */

import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager

object IMESetupCheckUtil
{
    fun isThisImeEnabled(context: Context) : Boolean
    {
        val imm = getImm(context)
        for (imi in imm.enabledInputMethodList)
        {
            if (context.packageName == imi.packageName)
            {
                return true
            }
        }
        return false
    }

    fun isThisImeCurrent(context: Context): Boolean
    {
        val imm = getImm(context)
        val imi = getInputMethodInfoOf(context.packageName, imm)
        val currentImeId = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        return imi != null && imi.id == currentImeId
    }

    private fun getInputMethodInfoOf(packageName: String, imm: InputMethodManager) : InputMethodInfo?
    {
        for (imi in imm.inputMethodList)
        {
            if (packageName == imi.packageName)
            {
                return imi
            }
        }
        return null
    }

    fun getImm(context : Context) : InputMethodManager
    {
        return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
}