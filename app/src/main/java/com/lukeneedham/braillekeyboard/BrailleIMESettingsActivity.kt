package com.lukeneedham.braillekeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log

/**
 * Created by Luke on 06/06/2018.
 */
class BrailleIMESettingsActivity : AppCompatActivity(), CanChangeFragment, CanGoBack, HasToolbar
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settingsactivity)
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        setFragment(
                if (IMESetupCheckUtil.isThisImeEnabled(this))
                {
                    if (IMESetupCheckUtil.isThisImeCurrent(this))
                    {
                        Log.v("setup step", "3")
                        BrailleIMESettings_HomeFragment()
                    }
                    else
                    {
                        Log.v("setup step", "2")
                        BrailleIMESetUp_SetIMECurrent()
                    }
                }
                else
                {
                    Log.v("setup step", "1")
                    BrailleIMESetUp_EnableIME()
                })
    }

    override fun onResume()
    {
        super.onResume()
        if (IMESetupCheckUtil.isThisImeEnabled(this))
        {
            if (!IMESetupCheckUtil.isThisImeCurrent(this))
            {
                clearFragmentBackstack()
                setFragment(BrailleIMESetUp_SetIMECurrent())
            }
        }
        else
        {
            clearFragmentBackstack()
            setFragment(BrailleIMESetUp_EnableIME())
        }
    }

    private fun clearFragmentBackstack()
    {
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount)
        {
            fm.popBackStack()
        }
    }

    override fun setFragment(f: Fragment)
    {
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()

        val tag = f.javaClass.simpleName
        ft.replace(R.id.fragment, f, tag)
        ft.addToBackStack(tag)
        ft.commit()
        fm.executePendingTransactions()
    }

    override fun goBack()
    {
        val fm = supportFragmentManager
        if (fm.backStackEntryCount > 1)
        {
            fm.popBackStack()
            fm.executePendingTransactions()
        }
        else
            finish()
    }

    override fun onBackPressed()
    {
        goBack()
    }

    override fun showToolbar()
    {
        supportActionBar?.show()
    }

    override fun hideToolbar()
    {
        supportActionBar?.hide()
    }
}

fun startBrailleIMESettingsActivityIntent(context: Context)
{
    val intent = Intent()
    intent.setClass(context, BrailleIMESettingsActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(intent)
}