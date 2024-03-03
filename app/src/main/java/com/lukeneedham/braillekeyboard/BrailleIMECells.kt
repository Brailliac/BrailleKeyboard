package com.lukeneedham.braillekeyboard

import android.text.TextUtils
import android.util.Log
import android.view.View
import com.lukeneedham.brailledatabase.Braille.BrailleCellDatabase
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase.Companion.BLANK_BRAILLE_SYMBOL
import com.lukeneedham.braillekeyboard.braillekeyboardview.BrailleKeyboardCells
import java.sql.DriverManager

/**
 * Created by Luke on 06/06/2018.
 */
class BrailleIMECells(service: BrailleIMEService) : BrailleIME(service)
{
    lateinit var keyboard : BrailleKeyboardCells

    override fun onCreateInputView(): View
    {
        keyboard = BrailleKeyboardCells(service)

        val layout = setupKeyboard(keyboard)

        return layout
    }

    override fun backspaceButtonClicked()
    {
        val composingSymbol = keyboard.brailleGrid.input

        if(composingSymbol == BrailleCellDatabase.CellEmpty)
        {
            if(TextUtils.isEmpty(inputConnection?.getSelectedText(0)))
            {
                deleteTextBeforeCursorInputConnection(1)
            }
            else
            {
                commitTextToInputConnection("")
            }
        }
        else
        {
            clearComposingText()
        }

        keyboard.clearBrailleGrid()
    }

    override fun inputButtonClicked()
    {
        if(keyboard.brailleGrid.input == BrailleCellDatabase.CellEmpty)
        {
            commitTextToInputConnection(BLANK_BRAILLE_SYMBOL)
        }
        commitComposingText()
        keyboard.clearBrailleGrid()
    }

    override fun dotButtonClicked()
    {
        val currentCell = keyboard.brailleGrid.input


        if(currentCell == BrailleCellDatabase.CellEmpty)
        {
            clearComposingText()
        }
        else
        {
            val letter = "" + currentCell.toString()
            updateComposingText(letter)
        }
    }
}