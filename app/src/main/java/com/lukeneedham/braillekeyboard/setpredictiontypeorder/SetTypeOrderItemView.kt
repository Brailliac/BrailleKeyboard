package com.lukeneedham.braillekeyboard.setpredictiontypeorder

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.lukeneedham.braillekeyboard.R

class SetTypeOrderItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr)
{
    init
    {
        background = ContextCompat.getDrawable(context, R.drawable.set_type_order_item_background)
        textSize = 20f
        gravity = Gravity.CENTER
        val paddingSide = context.resources.getDimensionPixelSize(R.dimen.set_type_order_item_side_padding)
        val paddingVertical = context.resources.getDimensionPixelSize(R.dimen.set_type_order_item_vertical_padding)
        setPadding(paddingSide, paddingVertical, paddingSide, paddingVertical)
    }
}