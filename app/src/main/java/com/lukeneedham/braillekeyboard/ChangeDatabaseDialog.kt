package com.lukeneedham.braillekeyboard

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.cardview.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabaseType

class ChangeDatabaseDialog : DialogFragment()
{
    var onDatabaseChangeListener : (BrailleDatabaseType) -> Unit = {}
    var previousDatabase : BrailleDatabaseType? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.changedatabase_dialog, container)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        val v = view

        val c = v!!.findViewById<LinearLayout>(R.id.container)
        for (d in BrailleDatabaseType.values())
        {
            val e = ChooseDatabaseEntry(context)
            e.setDatabase(d)
            e.setOnClickListener(ChangeDatabaseListener(d))
            if (previousDatabase == d)
            {
                e.findViewById<CardView>(R.id.card).setBackgroundColor(resources.getColor(R.color.colorAccent))
                (e.findViewById(R.id.name) as TextView).setTextColor(resources.getColor(R.color.white))
                (e.findViewById(R.id.desc) as TextView).setTextColor(resources.getColor(R.color.secondary_white))
            }
            c.addView(e)
        }

        super.onActivityCreated(savedInstanceState)
    }

    inner class ChangeDatabaseListener(internal var db: BrailleDatabaseType) : View.OnClickListener
    {
        override fun onClick(v: View)
        {
            setDatabase(db)
        }
    }

    fun setDatabase(db: BrailleDatabaseType)
    {
        dismiss()
        onDatabaseChangeListener(db)
    }
}
