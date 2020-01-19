package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Luke on 15/06/2018.
 */
public class OnSwipeTouchListener implements View.OnTouchListener
{
	private final GestureDetector gestureDetector;

	public OnSwipeTouchListener(Context ctx)
	{
		gestureDetector = new GestureDetector(ctx, new GestureListener());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		return gestureDetector.onTouchEvent(event);
	}

	private final class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		private static final int SWIPE_THRESHOLD = 100;
		private static final int SWIPE_VELOCITY_THRESHOLD = 100;

		@Override
		public boolean onDown(MotionEvent e)
		{
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
		{
			boolean result = false;
			float diffY = e2.getY() - e1.getY();
			float diffX = e2.getX() - e1.getX();

			/*
			Log.v("diffY", diffY + "");
			Log.v("diffX", diffX + "");
			Log.v("velocityX", velocityX + "");
			Log.v("velocityY", velocityY + "");
			*/

			if (Math.abs(diffX) > Math.abs(diffY))
			{
				if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD)
				{
					if (diffX > 0)
					{
						onSwipeRight();
					}
					else
					{
						onSwipeLeft();
					}
					result = true;
				}
			}
			else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD)
			{
				if (diffY > 0)
				{
					onSwipeDown();
				}
				else
				{
					onSwipeUp();
				}
				result = true;
			}

			return result;
		}
	}

	public void onSwipeRight()
	{
	}

	public void onSwipeLeft()
	{
	}

	public void onSwipeUp()
	{
	}

	public void onSwipeDown()
	{
	}
}