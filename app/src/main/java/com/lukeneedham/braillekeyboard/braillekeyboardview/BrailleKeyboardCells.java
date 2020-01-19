package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.util.AttributeSet;

import com.lukeneedham.braillekeyboard.R;

/**
 * Created by Luke on 17/09/2016.
 */
public class BrailleKeyboardCells extends BrailleKeyboardType
{
	public BrailleKeyboardCells(Context context)
	{
		this(context, null);
	}

	public BrailleKeyboardCells(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public BrailleKeyboardCells(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		inflate(context, R.layout.braillekeyboard_cells, this);
	}
}
