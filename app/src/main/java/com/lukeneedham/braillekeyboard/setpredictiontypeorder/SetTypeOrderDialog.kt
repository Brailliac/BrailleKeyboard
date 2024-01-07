package com.lukeneedham.braillekeyboard.setpredictiontypeorder

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lukeneedham.braillekeyboard.PreferencesRepository
import com.lukeneedham.braillekeyboard.R
import com.lukeneedham.braillekeyboard.braillekeyboardview.getDefaultDictionaryTypes
import kotlinx.android.synthetic.main.set_type_order_dialog.*
import java.util.*

class SetTypeOrderDialog : DialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.set_type_order_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        val prefsRepo = PreferencesRepository(view.context)
        val symbolDatabase = prefsRepo.symbolDatabaseType.database
        val dictTypes = (prefsRepo.dictionaryTypesTranslationPriority ?: getDefaultDictionaryTypes(symbolDatabase, view.context))
                .toMutableList()
        val recyclerAdapter = SetTypeOrderRecyclerAdapter(dictTypes)
        typePriorityRecyclerView.addItemDecoration(SetTypeOrderItemDecoration())
        typePriorityRecyclerView.adapter = recyclerAdapter
        typePriorityRecyclerView.layoutManager =
            LinearLayoutManager(context)

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback()
        {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
            {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                if (fromPosition < toPosition)
                {
                    for (i in fromPosition until toPosition)
                    {
                        Collections.swap(dictTypes, i, i + 1)
                    }
                }
                else
                {
                    for (i in fromPosition downTo toPosition + 1)
                    {
                        Collections.swap(dictTypes, i, i - 1)
                    }
                }

                prefsRepo.dictionaryTypesTranslationPriority = dictTypes

                recyclerAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition);
                return true;
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
            }

            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) =
                    makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END)
        })


        itemTouchHelper.attachToRecyclerView(typePriorityRecyclerView)
    }

}