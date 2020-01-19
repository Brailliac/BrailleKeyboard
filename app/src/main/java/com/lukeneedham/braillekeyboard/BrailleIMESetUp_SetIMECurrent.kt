package com.lukeneedham.braillekeyboard

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.content.IntentFilter


/**
 * Created by Luke on 06/06/2018.
 */
class BrailleIMESetUp_SetIMECurrent : Fragment()
{
    var inputMethodChangeReceiver : InputMethodChangeReceiver? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.setup_setcurrentime, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val contex = context
        if (contex != null)
        {
            val imm = IMESetupCheckUtil.getImm(contex)
            val runnable = Runnable { imm.showInputMethodPicker() }
            view.findViewById<Button>(R.id.setcurrentIMEbutton).setOnClickListener { runnable.run() }
        }

        (activity as HasToolbar).showToolbar()
    }

    override fun onResume()
    {
        super.onResume()

        val activit = activity
        if(activit != null)
        {
            inputMethodChangeReceiver = InputMethodChangeReceiver(activit)
            context?.registerReceiver(inputMethodChangeReceiver, IntentFilter(Intent.ACTION_INPUT_METHOD_CHANGED))
        }
    }

    override fun onPause()
    {
        super.onPause()
        context?.unregisterReceiver(inputMethodChangeReceiver)
        inputMethodChangeReceiver = null
    }

    class InputMethodChangeReceiver(private val activity : Activity) : BroadcastReceiver()
    {
        override fun onReceive(context: Context, intentIn: Intent)
        {
            val action = intentIn.action;
            if (action == Intent.ACTION_INPUT_METHOD_CHANGED)
            {
                if (IMESetupCheckUtil.isThisImeCurrent(context))
                {
                    activity.finish()
                    startBrailleIMESettingsActivityIntent(context)
                }
            }
        }
    }

}