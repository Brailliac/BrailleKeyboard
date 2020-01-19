package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.lukeneedham.brailledatabase.Braille.BrailleCell;
import com.lukeneedham.braillekeyboard.R;

/**
 * Created by Luke on 17/09/2016.
 */
public class BrailleGridView extends RelativeLayout
{
	private boolean rectoVerso;

	private BrailleDotView dot1;
	private BrailleDotView dot2;
	private BrailleDotView dot3;
	private BrailleDotView dot4;
	private BrailleDotView dot5;
	private BrailleDotView dot6;

	private int dotDiam;

	public BrailleGridView(Context context)
	{
		this(context, null);
	}

	public BrailleGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public BrailleGridView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		inflate(context, R.layout.dot_grid, this);

		dot1 = findViewById(R.id.dot1);
		dot2 = findViewById(R.id.dot2);
		dot3 = findViewById(R.id.dot3);
		dot4 = findViewById(R.id.dot4);
		dot5 = findViewById(R.id.dot5);
		dot6 = findViewById(R.id.dot6);
	}

	public int getRealGridHeight()
	{
		return dotDiam;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int widthPixels = MeasureSpec.getSize(widthMeasureSpec);
		int widthAvail = widthPixels - getPaddingLeft() - getPaddingRight();
		int heightPixels = MeasureSpec.getSize(heightMeasureSpec);
		int heightAvail = heightPixels - getPaddingTop() - getPaddingBottom();

		int dotpadding = (int) getResources().getDimension(R.dimen.brailledotpadding);

		int dotWidth = (widthAvail/2); //- (2 * dotpadding);
		int dotHeight = (heightAvail/3); //- (2 * dotpadding);

		dotDiam = Math.min(dotWidth, dotHeight);

		int paddeddotsize = dotDiam ;//+ (2 * dotpadding);

		setDotLayoutParams(dot1, dotDiam, 0, 0);
		setDotLayoutParams(dot2, dotDiam, 0, paddeddotsize);
		setDotLayoutParams(dot3, dotDiam, 0, 2*paddeddotsize);
		setDotLayoutParams(dot4, dotDiam, paddeddotsize, 0);
		setDotLayoutParams(dot5, dotDiam, paddeddotsize, paddeddotsize);
		setDotLayoutParams(dot6, dotDiam, paddeddotsize, 2*paddeddotsize);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//setMeasuredDimension(paddeddotsize * 2, paddeddotsize * 3);
		//setMeasuredDimension(widthPixels, heightPixels);
	}

	public void setDotLayoutParams(BrailleDotView dot, int diam, int xoffset, int yoffset)
	{
		LayoutParams params = new LayoutParams(diam, diam);
		params.leftMargin = xoffset;
		params.topMargin = yoffset;
		dot.setLayoutParams(params);
	}

	public void setRectoVerso(boolean b)
	{
		rectoVerso = b;
		dot1.setRectoVerso(rectoVerso);
		dot2.setRectoVerso(rectoVerso);
		dot3.setRectoVerso(rectoVerso);
		dot4.setRectoVerso(rectoVerso);
		dot5.setRectoVerso(rectoVerso);
		dot6.setRectoVerso(rectoVerso);
	}

	public void setDotClickListener(OnClickListener cl)
	{
		dot1.setOnClickListener(cl);
		dot2.setOnClickListener(cl);
		dot3.setOnClickListener(cl);
		dot4.setOnClickListener(cl);
		dot5.setOnClickListener(cl);
		dot6.setOnClickListener(cl);
	}

	public void clear()
	{
		dot1.turnOff();
		dot2.turnOff();
		dot3.turnOff();
		dot4.turnOff();
		dot5.turnOff();
		dot6.turnOff();
	}

	public BrailleCell getInput()
	{
		if(rectoVerso)
			return new BrailleCell(dot4.isOn(), dot5.isOn(), dot6.isOn(), dot1.isOn(), dot2.isOn(), dot3.isOn());
		else
			return new BrailleCell(dot1.isOn(), dot2.isOn(), dot3.isOn(), dot4.isOn(), dot5.isOn(), dot6.isOn());
	}
}
