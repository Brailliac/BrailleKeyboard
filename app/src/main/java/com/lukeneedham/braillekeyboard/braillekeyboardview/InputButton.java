package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lukeneedham.braillekeyboard.Util;

/**
 * Created by Luke on 13/06/2017.
 */

public abstract class InputButton extends LinearLayout
{
	int baseDrawable;
	public ImageView imageView;
	public double imageAspectRatio;
	public View base;
	public int color;

	public InputButton(Context context)
	{
		this(context, null);
	}

	public InputButton(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public InputButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public void setOnClickListener(@Nullable OnClickListener l)
	{
		super.setOnClickListener(view ->
		{
			Util.vibrate(15, getContext());
			if (l != null)
			{
				l.onClick(view);
			}
		});
	}

	public void setColor(int c)
	{
		color = c;

		GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(baseDrawable).mutate();
		drawable.setColor(color);
		base.setBackgroundDrawable(drawable);
		invalidate();
	}

	public void setImage(int im)
	{
		Drawable imageRes = getResources().getDrawable(im).mutate();

		BitmapFactory.Options dimensions = new BitmapFactory.Options();
		dimensions.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), im, dimensions);
		imageAspectRatio = dimensions.outHeight / (dimensions.outWidth + 0.0);

		int imagesColor = 0xffffffff;
		imageRes.setColorFilter(imagesColor, PorterDuff.Mode.MULTIPLY);
		imageView.setImageDrawable(imageRes);

		invalidate();
	}
}
