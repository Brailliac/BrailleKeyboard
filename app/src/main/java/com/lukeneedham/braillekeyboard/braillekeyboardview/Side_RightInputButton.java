package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.lukeneedham.braillekeyboard.R;


/**
 * Created by Luke on 17/09/2016.
 */
public class Side_RightInputButton extends SideInputButton
{
	public Side_RightInputButton(Context context)
	{
		this(context, null);
	}

	public Side_RightInputButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public Side_RightInputButton(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		View.inflate(context, R.layout.inputbutton_sideright, this);

		imageView = findViewById(R.id.image);
		base = findViewById(R.id.base);

		baseDrawable = R.drawable.input_right;

		setColor(getResources().getColor(R.color.rightInputButton));
		setImage(R.drawable.arrow_next);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int imageWidth = calculateImageWidth(widthMeasureSpec, heightMeasureSpec);
		int imageMargin = calculateImageMargin(widthMeasureSpec, imageWidth);

		RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
		p.width = imageWidth;
		p.height = (int) (imageWidth * imageAspectRatio);
		p.rightMargin = imageMargin;
		imageView.setLayoutParams(p);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
