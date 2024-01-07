package com.lukeneedham.braillekeyboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lukeneedham.brailledatabase.Braille.BrailleCellDatabase.CellEmpty
import com.lukeneedham.brailledatabase.Braille.BrailleSymbolTranslation
import com.lukeneedham.braillekeyboard.braillekeyboardview.BrailleKeyboardLatin
import com.lukeneedham.braillekeyboard.braillekeyboardview.BrailleKeyboardType
import com.lukeneedham.braillekeyboard.Util.*

/**
 * Created by Luke on 06/06/2018.
 */
class BrailleIMESettings_ChangeSizeFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.settingsactivity_sizepreview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<SeekBar>(R.id.sizeSeekBar).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                if (seekBar != null)
                {
                    val newFraction = progress / seekBar.max.toFloat()
                    Util.getSharedPrefs(context).edit().putFloat(KEYBOARD_SIZE_PERCENT, newFraction).apply()

                    val newPercentage = Math.round(newFraction * 100)
                    val percentageDescription = newPercentage.toString() + "%"
                    view.findViewById<TextView>(R.id.sizeSeekBarDescription).setText(percentageDescription)

                    val keyboard = view.findViewById<BrailleKeyboardType>(R.id.keyboard)
                    keyboard.setOnTouchListener { _, _ ->  true }
                    val lp = keyboard.layoutParams
                    lp.height = BrailleIME.calculateKeyboardSize(keyboard, context!!)
                    keyboard.layoutParams = lp
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?)
            {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?)
            {
            }
        })
        val percent = Util.getSharedPrefs(context).getFloat(KEYBOARD_SIZE_PERCENT, KEYBOARD_SIZE_PERCENT_DEFAULT)
        val progress = Math.round(percent * 100)
        view.findViewById<SeekBar>(R.id.sizeSeekBar).progress = progress

        view.findViewById<BrailleKeyboardLatin>(R.id.keyboard).setCellsEntered(BrailleSymbolTranslation(), CellEmpty)

        view.findViewById<View>(R.id.backButton).setOnClickListener { (activity as CanGoBack).goBack() }

        (activity as HasToolbar).hideToolbar()
    }
}