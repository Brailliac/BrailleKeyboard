package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by Luke on 17/09/2016.
 */
public abstract class SideInputButton extends InputButton
{
	public SideInputButton(Context context)
	{
		this(context, null);
	}

	public SideInputButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public SideInputButton(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public int calculateImageWidth(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
		int widthAvail = widthPixels - getPaddingLeft() - getPaddingRight();
		int heightPixels = MeasureSpec.getSize(heightMeasureSpec);
		int heightAvail = heightPixels - getPaddingTop() - getPaddingBottom();

		int imageWidth = (int) (widthAvail / 2);

		int newHeight = (int) (imageWidth * imageAspectRatio);
		int maxHeight = (int) (heightAvail / 3);
		if (newHeight > maxHeight)
		{
			imageWidth = (int) (maxHeight / imageAspectRatio);
		}

		return imageWidth;
	}

	public int calculateImageMargin(int widthMeasureSpec, int imageWidth)
	{
		int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
		int widthAvail = widthPixels - getPaddingLeft() - getPaddingRight();

		return (int) ((widthAvail-imageWidth)/3);
	}
}
