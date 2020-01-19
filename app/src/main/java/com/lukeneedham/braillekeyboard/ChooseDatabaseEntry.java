package com.lukeneedham.braillekeyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabaseType;

public class ChooseDatabaseEntry extends LinearLayout
{
	private BrailleDatabaseType e;

	public ChooseDatabaseEntry(Context context)
	{
		this(context, null);
	}

	public ChooseDatabaseEntry(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public ChooseDatabaseEntry(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		inflate(context, R.layout.database_entry, this);
	}

	public void setDatabase(BrailleDatabaseType e)
	{
		this.e = e;
		((TextView) findViewById(R.id.name)).setText(e.getName(getContext()));
		((TextView) findViewById(R.id.desc)).setText(e.getDesc(getContext()));
	}
}