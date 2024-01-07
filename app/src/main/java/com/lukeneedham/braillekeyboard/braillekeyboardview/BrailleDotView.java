package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lukeneedham.braillekeyboard.R;
import com.lukeneedham.braillekeyboard.Util;

/**
 * Created by Luke on 17/09/2016.
 */
public class BrailleDotView extends View implements View.OnClickListener
{
	private boolean rectoVerso = false;
    private boolean on = false;
	private Drawable filled;
	private Drawable unfilled;

	private Drawable showing;

	private int diameterMeasured;
	private int emptyTop;
	private int emptyLeft;

	OnClickListener clickListener;

    public BrailleDotView(Context context)
    {
        this(context, null);
    }

    public BrailleDotView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BrailleDotView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

		filled = getResources().getDrawable(R.drawable.fullbraillebutton);
		unfilled = getResources().getDrawable(R.drawable.emptybraillebutton);

		super.setOnClickListener(this);

		showRandomUnfilled();
    }

	@Override
	public void setOnClickListener(@Nullable OnClickListener l)
	{
		clickListener = l;
	}

    public void setDiameter(int diam)
	{
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = diam;
		params.width = diam;
		setLayoutParams(params);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
		int circWidthAvail = widthPixels - getPaddingLeft() - getPaddingRight();
		int heightPixels = MeasureSpec.getSize(heightMeasureSpec);
		int circHeightAvail = heightPixels - getPaddingTop() - getPaddingBottom();

		diameterMeasured = Math.min(circWidthAvail, circHeightAvail);

		emptyTop = ((heightPixels - getPaddingTop() - getPaddingBottom() - diameterMeasured) / 2) + getPaddingTop();
		emptyLeft = ((widthPixels - getPaddingLeft() - getPaddingRight() - diameterMeasured)/2) + getPaddingLeft();

		setMeasuredDimension(widthPixels, heightPixels);
	}

	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		if(showing != null)
		{
			showing.setBounds(emptyLeft, emptyTop, emptyLeft + diameterMeasured, emptyTop + diameterMeasured);
			showing.draw(canvas);
		}
	}

	@Override
	public void onClick(View view)
	{
		Util.vibrate(15, getContext());
		if(on)
		{
			turnOff();
		}
		else
		{
			turnOn();
		}

		if(clickListener != null)
		{
			clickListener.onClick(view);
		}
	}

	public void setRectoVerso(boolean b)
	{
		rectoVerso = b;
		if(rectoVerso)
			showing = rectoVersoColor(showing);
		invalidate();
	}

	public boolean isOn()
	{
		return on;
	}

	public void turnOn()
	{
		if(!on)
		{
			on = true;
			showImage(filled);
		}
	}

	public void turnOff()
	{
		if(on)
		{
			on = false;
			showRandomUnfilled();
		}
	}

	public void showRandomUnfilled()
	{
		showImage(unfilled);
	}

	public Drawable rectoVersoColor(Drawable d)
	{
		Drawable draw = d.mutate();
		int rectoVersoDotColour = 0xffe5ebff;
		draw.setColorFilter(rectoVersoDotColour, PorterDuff.Mode.MULTIPLY);
		return draw;
	}

	public void showImage(Drawable d)
	{
		showing = d.mutate();
		if(rectoVerso)
			showing = rectoVersoColor(showing);
		invalidate();
	}
}
