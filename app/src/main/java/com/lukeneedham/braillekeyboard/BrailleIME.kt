package com.lukeneedham.braillekeyboard

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import com.lukeneedham.braillekeyboard.braillekeyboardview.BrailleKeyboardLatin
import com.lukeneedham.braillekeyboard.braillekeyboardview.BrailleKeyboardType
import com.lukeneedham.braillekeyboard.braillekeyboardview.OnSwipeTouchListener
import kotlinx.android.synthetic.main.braillekeyboard_latin.view.*
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import com.lukeneedham.braillekeyboard.Util.*
import android.view.Display
import android.view.WindowManager
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import kotlinx.android.synthetic.main.settingsactivity_sizepreview.view.*


/**
 * Created by Luke on 06/06/2018.
 */

abstract class BrailleIME(val service: BrailleIMEService)
{
    abstract fun onCreateInputView(): View

    protected var composingText = ""
        private set

    protected val sharedPrefs = Util.getSharedPrefs(service)
    protected val sharedPrefsRepository = PreferencesRepository(service)

    protected val inputConnection: InputConnection? = service.currentInputConnection

    init
    {
        service.onUpdateSelectionListener = { i: Int, _: Int, i2: Int, i3: Int, i4: Int, i5: Int -> }
    }

    fun setupKeyboard(keyboard: BrailleKeyboardType): View
    {
        keyboard.setLeftInputButtonListener(RepeatListener(500, 120, View.OnClickListener
        {
            Util.vibrate(15, service)
            backspaceButtonClicked()
        }))

        keyboard.setRightInputButtonListener { inputButtonClicked() }
        keyboard.setLongRightInputButtonListener { inputConnection?.performEditorAction(service.currentInputEditorInfo.imeOptions and EditorInfo.IME_MASK_ACTION) ?: false }

        keyboard.setBrailleDotListener { dotButtonClicked() }

        keyboard.setOnSwipeListener(object : OnSwipeTouchListener(keyboard.context)
        {
            override fun onSwipeUp()
            {
                super.onSwipeUp()
                Util.vibrate(15, service)
                openSettings()
            }

            override fun onSwipeDown()
            {
                super.onSwipeDown()
                Util.vibrate(15, service)
                changeKeyboardMode()
            }

            override fun onSwipeRight()
            {
                super.onSwipeRight()
                Util.vibrate(15, service)
                inputButtonClicked()
            }

            override fun onSwipeLeft()
            {
                super.onSwipeLeft()
                Util.vibrate(15, service)
                backspaceButtonClicked()
            }
        })

        /*
        Resize the keyboard to match user preference
         */

        keyboard.setBackgroundColor(service.resources.getColor(R.color.keyboardBackground))

        val rectoVerso = Util.getSharedPrefs(keyboard.context).getBoolean(RECTO_VERSO_MODE, RECTO_VERSO_MODE_DEFAULT)
        keyboard.brailleGrid.setRectoVerso(rectoVerso)

        val container = LinearLayout(service)

        container.addView(keyboard)

        val keyboardLayoutParams = keyboard.layoutParams
        keyboardLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        keyboard.post {
            keyboardLayoutParams.height = calculateKeyboardSize(keyboard, service)
            keyboard.layoutParams = keyboardLayoutParams
        }

        return container
    }

    companion object
    {
        public fun calculateKeyboardSize(keyboard: BrailleKeyboardType, context: Context): Int
        {
            val predBarHeight = if (keyboard is BrailleKeyboardLatin)
            {
                keyboard.predictionBar.height
            }
            else 0

            val keyboardSizePercent = Util.getSharedPrefs(context).getFloat(KEYBOARD_SIZE_PERCENT, KEYBOARD_SIZE_PERCENT_DEFAULT)

            val totalScreenHeight = getWindowSize(context).second
            val keyboardHeight = (keyboardSizePercent * totalScreenHeight).toInt() + predBarHeight
            return keyboardHeight
        }

        /**
         * From https://stackoverflow.com/questions/14341041/how-to-get-real-screen-height-and-width
         */
        private fun getWindowSize(context : Context) : Pair<Int, Int>
        {
            val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            var realWidth: Int
            var realHeight: Int

            if (Build.VERSION.SDK_INT >= 17)
            {
                //new pleasant way to get real metrics
                val realMetrics = DisplayMetrics()
                display.getRealMetrics(realMetrics)
                realWidth = realMetrics.widthPixels
                realHeight = realMetrics.heightPixels
            }
            else
            {
                //reflection for this weird in-between time
                try
                {
                    val mGetRawH = Display::class.java.getMethod("getRawHeight")
                    val mGetRawW = Display::class.java.getMethod("getRawWidth")
                    realWidth = mGetRawW.invoke(display) as Int
                    realHeight = mGetRawH.invoke(display) as Int
                }
                catch (e: Exception)
                {
                    //this may not be 100% accurate, but it's all we've got
                    realWidth = display.getWidth()
                    realHeight = display.getHeight()
                    Log.e("Display Info", "Couldn't use reflection to get the real display metrics.")
                }
            }

            return Pair(realWidth, realHeight)
        }
    }

    protected fun openSettings()
    {
        val i = Intent()

        i.setClass(service, BrailleIMESettingsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        startActivity(service, i, null)
    }

    private fun changeKeyboardMode()
    {
        val oldMode = sharedPrefs.getBoolean(INPUT_MODE_BRAILLE, INPUT_MODE_BRAILLE_DEFAULT)
        val newMode = !oldMode
        sharedPrefs.edit().putBoolean(INPUT_MODE_BRAILLE, newMode).commit()

        service.restart()
    }

    abstract fun backspaceButtonClicked()

    abstract fun inputButtonClicked()

    abstract fun dotButtonClicked()


    open fun updateComposingText(s: String)
    {
        setInputConnectionComposingText(s)
    }

    open fun clearComposingText()
    {
        setInputConnectionComposingText("")
    }

    open fun deleteTextBeforeCursorInputConnection(length: Int)
    {
        inputConnection?.deleteSurroundingText(length, 0)
    }

    open fun commitTextToInputConnection(text: String)
    {
        inputConnection?.commitText(text, 1)
    }

    open fun commitComposingText()
    {
        if (inputConnection != null)
        {
            inputConnection.finishComposingText()
            composingText = ""
        }
    }

    private fun setInputConnectionComposingText(s: String)
    {
        if (inputConnection != null)
        {
            composingText = s
            inputConnection.setComposingText(s, 1)
        }
    }
}