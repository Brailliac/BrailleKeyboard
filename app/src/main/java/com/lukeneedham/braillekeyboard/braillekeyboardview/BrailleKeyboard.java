package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lukeneedham.brailledatabase.Braille.BrailleCell;
import com.lukeneedham.braillekeyboard.R;

/**
 * Created by Luke on 17/09/2016.
 */
public class BrailleKeyboard extends LinearLayout
{
	private BrailleGridView brailleGrid;
	private Side_LeftInputButton leftInputButton;
	private Side_RightInputButton rightInputButton;

	private OnSwipeTouchListener onSwipeListener;

	public BrailleKeyboard(Context context)
	{
		this(context, null);
	}

	public BrailleKeyboard(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public BrailleKeyboard(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		inflate(context, R.layout.braillekeyboard, this);

		brailleGrid = findViewById(R.id.brailleGrid);
		leftInputButton = findViewById(R.id.leftInputButton);
		rightInputButton = findViewById(R.id.rightInputButton);
	}

	public void setOnSwipeListener(OnSwipeTouchListener swipeListener)
	{
		onSwipeListener = swipeListener;
	}

	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		super.onInterceptTouchEvent(event);
		if(onSwipeListener != null)
		{
			onSwipeListener.onTouch(this, event);
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e)
	{
		super.dispatchTouchEvent(e);
		if(onSwipeListener != null)
		{
			return onSwipeListener.onTouch(this, e);
		}
		else
		{
			return false;
		}
	}

	public void setRectoVerso(boolean b)
	{
		getBrailleGrid().setRectoVerso(b);
	}

	public BrailleCell getInput()
	{
		return getBrailleGrid().getInput();
	}

	public BrailleGridView getBrailleGrid()
	{
		return brailleGrid;
	}

	public void setLeftInputButtonListener(OnTouchListener cl)
	{
		leftInputButton.setOnTouchListener(cl);
	}

	public void setRightInputButtonListener(OnClickListener cl)
	{
		rightInputButton.setOnClickListener(cl);
	}

	public void setLongRightInputButtonListener(OnLongClickListener cl)
	{
		rightInputButton.setOnLongClickListener(cl);
	}

	public void setBrailleDotListener(OnClickListener cl)
	{
		brailleGrid.setDotClickListener(cl);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
		int widthAvail = widthPixels - getPaddingLeft() - getPaddingRight();
		int heightPixels = MeasureSpec.getSize(heightMeasureSpec);
		int heightAvail = heightPixels - getPaddingTop() - getPaddingBottom();

		int sideButtonWidth = widthAvail / 5;
		int sideButtonMarginTopBot = heightAvail / 7;

		RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) leftInputButton.getLayoutParams();
		p.width = sideButtonWidth;
		p.topMargin = sideButtonMarginTopBot;
		p.bottomMargin = sideButtonMarginTopBot;
		leftInputButton.setLayoutParams(p);

		p = (RelativeLayout.LayoutParams) rightInputButton.getLayoutParams();
		p.width = sideButtonWidth;
		p.topMargin = sideButtonMarginTopBot;
		p.bottomMargin = sideButtonMarginTopBot;
		rightInputButton.setLayoutParams(p);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public void clearBrailleGrid()
	{
		brailleGrid.clear();
	}
}
