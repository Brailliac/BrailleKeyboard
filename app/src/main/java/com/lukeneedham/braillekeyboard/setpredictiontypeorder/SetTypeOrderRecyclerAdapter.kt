package com.lukeneedham.braillekeyboard.setpredictiontypeorder

import android.app.ActionBar
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.lukeneedham.brailledatabase.Braille.DictionaryType

class SetTypeOrderRecyclerAdapter(private val dictionaryTypes: List<DictionaryType>) : RecyclerView.Adapter<SetTypeOrderRecyclerAdapter.TypeOrderViewHolder>()
{
    override fun getItemCount(): Int {
        return dictionaryTypes.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TypeOrderViewHolder
    {
        val view = SetTypeOrderItemView(parent.context)
        return TypeOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: TypeOrderViewHolder, position: Int)
    {
        val type = dictionaryTypes[position]
        val view = holder.typeItemView
        view.text = type.getName(view.context)
    }

    override fun onViewAttachedToWindow(holder: TypeOrderViewHolder)
    {
        super.onViewAttachedToWindow(holder)
        val view = holder.typeItemView
        val layoutParams = view.layoutParams
        layoutParams.apply {
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        view.layoutParams = layoutParams
    }

    class TypeOrderViewHolder(val typeItemView: SetTypeOrderItemView) : RecyclerView.ViewHolder(typeItemView)
}