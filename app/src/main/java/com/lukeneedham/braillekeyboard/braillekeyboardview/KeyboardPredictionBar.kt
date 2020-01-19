package com.lukeneedham.braillekeyboard.braillekeyboardview

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.lukeneedham.brailledatabase.Braille.BrailleSymbolTranslation
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase
import com.lukeneedham.braillekeyboard.R
import com.lukeneedham.braillekeyboard.Util

/**
 * Created by Luke on 17/09/2016.
 */
class KeyboardPredictionBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle)
{
    private var predictionItemClickedListener : (BrailleSymbolTranslation) -> Unit = {}

    private val PREDICTION_VERT_MARGIN = Math.max(Util.convertDpToPixel(15), 5)
    private val BASE_PREDICTION_SIDE_MARGIN = Util.convertDpToPixel(8)
    private val MIN_PRED_WIDTH = Util.convertDpToPixel(40)

    init
    {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL or Gravity.START
    }

    private fun addPredictionView(translation: BrailleSymbolTranslation, symbolDatabase : BrailleDatabase)
    {
        val tv = TextView(context)
        tv.text = translation.getLatinTranslation(symbolDatabase)
        tv.textSize = 20f
        tv.setOnClickListener(PredictionTextClickListener(translation))
        addView(tv)

        tv.measure(0, 0)
        val tvWidth = tv.measuredWidth
        // Log.v("tv width", ""+tvWidth)

        val sideMargin = BASE_PREDICTION_SIDE_MARGIN + if(tvWidth < MIN_PRED_WIDTH)
        {
            val toMakeUp = MIN_PRED_WIDTH - tvWidth
            (toMakeUp / 2)
        }
        else
        {
            0
        }

        val tvLp = tv.layoutParams as LinearLayout.LayoutParams
        tvLp.leftMargin = sideMargin
        tvLp.rightMargin = sideMargin
        tv.layoutParams = tvLp


        val bar = View(context)
        bar.setBackgroundColor(resources.getColor(R.color.predictionBarSeparator))
        addView(bar)

        val lp = bar.layoutParams as LinearLayout.LayoutParams
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        lp.width = 2
        lp.topMargin = PREDICTION_VERT_MARGIN
        lp.bottomMargin = PREDICTION_VERT_MARGIN
        bar.layoutParams = lp
    }

    private fun clear()
    {
        removeAllViews()
    }

    fun setPredictions(predsIn: List<BrailleSymbolTranslation>, symbolDatabase : BrailleDatabase)
    {
        clear()
        for (pred in predsIn)
        {
            val latinTrans = pred.getLatinTranslation(symbolDatabase)
            if(latinTrans != null && latinTrans != "")
            {
                addPredictionView(pred, symbolDatabase)
            }
        }
    }

    private inner class PredictionTextClickListener internal constructor(private val prediction: BrailleSymbolTranslation) : View.OnClickListener
    {
        override fun onClick(view: View)
        {
            predictionItemClickedListener(prediction)
        }
    }

    fun setPredictionItemClickedListener(predictionItemClickedListener : (BrailleSymbolTranslation) -> Unit)
    {
        this.predictionItemClickedListener = predictionItemClickedListener
    }
}
