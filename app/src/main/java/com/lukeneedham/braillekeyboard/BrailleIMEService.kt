package com.lukeneedham.braillekeyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import com.lukeneedham.braillekeyboard.Util.INPUT_MODE_BRAILLE_DEFAULT


/**
 * Created by Luke on 06/06/2018.
 */
class BrailleIMEService : InputMethodService()
{
    var onUpdateSelectionListener = { oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int -> }

    override fun onCreateInputView(): View
    {
        val sharedPrefs = Util.getSharedPrefs(this)
        return if (sharedPrefs.getBoolean(Util.INPUT_MODE_BRAILLE, INPUT_MODE_BRAILLE_DEFAULT)) BrailleIMECells(this).onCreateInputView() else BrailleIMELatin(this).onCreateInputView()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean)
    {
        super.onStartInputView(info, restarting)
        setInputView(onCreateInputView())
    }

    fun restart()
    {
        if (currentInputConnection != null)
        {
            currentInputConnection.setComposingText("", 1)
            setInputView(onCreateInputView())
        }
    }

    override fun onUpdateSelection(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int)
    {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        onUpdateSelectionListener(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
    }
}