package com.lukeneedham.braillekeyboard.setpredictiontypeorder

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.lukeneedham.braillekeyboard.R

class SetTypeOrderItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    {
        super.getItemOffsets(outRect, view, parent, state)
        val resources = view.context.resources
        outRect.apply {
            left = resources.getDimensionPixelSize(R.dimen.set_type_order_item_side_margin)
            right = resources.getDimensionPixelSize(R.dimen.set_type_order_item_side_margin)
            top = resources.getDimensionPixelSize(R.dimen.set_type_order_item_vertical_margin)
            bottom = resources.getDimensionPixelSize(R.dimen.set_type_order_item_vertical_margin)
        }
    }
}