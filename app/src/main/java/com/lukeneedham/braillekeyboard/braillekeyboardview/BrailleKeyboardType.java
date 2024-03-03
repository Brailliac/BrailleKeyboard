package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.lukeneedham.brailledatabase.Braille.BrailleCell;
import com.lukeneedham.brailledatabase.Braille.BrailleCellDatabase;
import com.lukeneedham.braillekeyboard.R;

/**
 * Created by Luke on 17/09/2016.
 */
public abstract class BrailleKeyboardType extends LinearLayout
{
	public BrailleKeyboardType(Context context)
	{
		super(context);
	}

	public BrailleKeyboardType(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public BrailleKeyboardType(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void setOnSwipeListener(OnSwipeTouchListener swipeListener)
	{
		getKeyboard().setOnSwipeListener(swipeListener);
	}

	private BrailleKeyboard getKeyboard()
	{
		return findViewById(R.id.keyboard);
	}


	public BrailleGridView getBrailleGrid()
	{
		return getKeyboard().getBrailleGrid();
	}

	public void setLeftInputButtonListener(OnTouchListener cl)
	{
		getKeyboard().setLeftInputButtonListener(cl);
	}

	public void setRightInputButtonListener(OnClickListener cl)
	{
		getKeyboard().setRightInputButtonListener(cl);
	}

	public void setLongRightInputButtonListener(OnLongClickListener cl)
	{
		getKeyboard().setLongRightInputButtonListener(cl);
	}

	public void setBrailleDotListener(OnClickListener cl)
	{
		getKeyboard().getBrailleGrid().setDotClickListener(cl);
	}

	public void clearBrailleGrid()
	{
		getKeyboard().getBrailleGrid().clear();
	}
}
