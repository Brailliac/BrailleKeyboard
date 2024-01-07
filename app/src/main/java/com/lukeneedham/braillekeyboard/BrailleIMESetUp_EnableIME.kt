package com.lukeneedham.braillekeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


/**
 * Created by Luke on 06/06/2018.
 */
class BrailleIMESetUp_EnableIME : Fragment()
{
    private var settingsPoolingHandler: SettingsPoolingHandler? = null

    private class SettingsPoolingHandler(val context: Context, val callback: () -> Unit) : Handler()
    {
        override fun handleMessage(msg: Message)
        {
            when (msg.what)
            {
                MSG_POLLING_IME_SETTINGS ->
                {
                    if (IMESetupCheckUtil.isThisImeEnabled(context))
                    {
                        callback()
                        return
                    }
                    startPollingImeSettings()
                }
            }
        }

        fun startPollingImeSettings()
        {
            sendMessageDelayed(obtainMessage(MSG_POLLING_IME_SETTINGS), IME_SETTINGS_POLLING_INTERVAL)
        }

        companion object
        {
            private val MSG_POLLING_IME_SETTINGS = 0
            private val IME_SETTINGS_POLLING_INTERVAL: Long = 200
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.setup_enableime, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val contex = context
        if (contex != null)
        {
            settingsPoolingHandler = SettingsPoolingHandler(contex, ::onKeyboardSetupInSettings)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val runnable = Runnable {
            startActivityForResult(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS), 0)
            settingsPoolingHandler?.startPollingImeSettings()
        }
        view.findViewById<Button>(R.id.addKeyboardInSettings).setOnClickListener { runnable.run() }

        (activity as HasToolbar).showToolbar()
    }

    fun onKeyboardSetupInSettings()
    {
        val contex = context
        if (contex != null)
        {
            activity?.finish()
            startBrailleIMESettingsActivityIntent(contex)
        }
    }
}