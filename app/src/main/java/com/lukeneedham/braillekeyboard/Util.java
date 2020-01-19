package com.lukeneedham.braillekeyboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.lukeneedham.brailledatabase.Braille.DictionaryType;
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase;
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabaseType;

import java.util.List;

/**
 * Created by Luke on 05/06/2018.
 */

public abstract class Util
{
	/**
	 * Shared Pref keys and values
	 */

	public static final String INPUT_MODE_BRAILLE = "INPUT_MODE_BRAILLE";
	public static final boolean INPUT_MODE_BRAILLE_DEFAULT = false;

	public static final String ALWAYS_SHOW_PREDICTIONS_MODE = "PREDICTIONS_ALWAYS_SHOW_MODE";
	public static final boolean ALWAYS_SHOW_PREDICTIONS_MODE_DEFAULT = false;

	public static final String RECTO_VERSO_MODE = "RECTO_VERSO_MODE";
	public static final boolean RECTO_VERSO_MODE_DEFAULT = false;

	public static final String SHOW_CELL_AS_PREDICTION_MODE = "CELL_AS_PREDICTION_MODE";
	public static final boolean SHOW_CELL_AS_PREDICTION_MODE_DEFAULT = false;

	public static final String OBEY_BRAILLE_USAGE_RULE_MODE = "OBEY_BRAILLE_USAGE_RULE";
	public static final boolean OBEY_BRAILLE_USAGE_RULE_MODE_DEFAULT = false;

	public static final String AUTO_INSERT_SPACE_AFTER_STANDALONE = "AUTO_INSERT_SPACE_AFTER_STANDALONE";
	public static final boolean AUTO_INSERT_SPACE_AFTER_STANDALONE_DEFAULT = false;


	public static final String AUTOFILL_PREDICTION_MODE = "AUTOFILL_PREDICTION_MODE";
	// numbers must match index of option in spinner
	public static final int AUTOFILL_PREDICTION_MODE_SIMPLEST = 0;
	public static final int AUTOFILL_PREDICTION_MODE_MOST_COMPLEX = 1;
	public static final int AUTOFILL_PREDICTION_MODE_LONGEST = 2;
	public static final int AUTOFILL_PREDICTION_MODE_CUSTOM = 3;
	public static final int AUTOFILL_PREDICTION_MODE_DEFAULT = AUTOFILL_PREDICTION_MODE_SIMPLEST;


	public static final String KEYBOARD_SIZE_PERCENT = "KEYBOARD_SIZE_PERCENT";
	public static final float KEYBOARD_SIZE_PERCENT_DEFAULT = 0.25f;


	public static int getResIdFromAttribute(final int attr, Context c)
	{
		TypedArray a = c.obtainStyledAttributes(new int[]{attr});
		int attributeResourceId = a.getResourceId(0, 0);
		a.recycle();
		return attributeResourceId;
	}

	public static void vibrate(int length, Context c)
	{
		if (getSharedPrefs(c).getBoolean("vibrateOn", true))
			((Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(length);
	}

	public static SharedPreferences getSharedPrefs(Context c)
	{
		return PreferenceManager.getDefaultSharedPreferences(c);
	}

	public static int getIntFromAttr(final int attr, Context c)
	{
		TypedValue typedValue = new TypedValue();
		c.getTheme().resolveAttribute(attr, typedValue, true);
		return typedValue.data;
	}

	/**
	 * This method converts dp unit to equivalent pixels, depending on device density.
	 *
	 * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
	 * @return A float value to represent px equivalent to dp depending on device density
	 */
	public static int convertDpToPixel(int dp)
	{
		return dp * (Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}

	/**
	 * This method converts device specific pixels to density independent pixels.
	 *
	 * @param px A value in px (pixels) unit. Which we need to convert into db
	 * @return A float value to represent dp equivalent to px value
	 */
	public static int convertPixelsToDp(int px)
	{
		return px / (Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}
}
