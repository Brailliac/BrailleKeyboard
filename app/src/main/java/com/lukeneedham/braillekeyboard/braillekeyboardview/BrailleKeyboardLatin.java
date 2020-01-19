package com.lukeneedham.braillekeyboard.braillekeyboardview;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lukeneedham.brailledatabase.Braille.BrailleCell;
import com.lukeneedham.brailledatabase.Braille.BrailleCellDatabase;
import com.lukeneedham.brailledatabase.Braille.BrailleSymbolDatabaseEntry;
import com.lukeneedham.brailledatabase.Braille.BrailleSymbolTranslation;
import com.lukeneedham.brailledatabase.Braille.DictionaryType;
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase;
import com.lukeneedham.braillekeyboard.R;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * Created by Luke on 17/09/2016.
 */
public class BrailleKeyboardLatin extends BrailleKeyboardType
{
	private KeyboardPredictionBar predictionBar;
	private TextView cellsEnteredTextview;
	private BrailleSymbolTranslation translationJustCells = new BrailleSymbolTranslation();

	public BrailleKeyboardLatin(Context context)
	{
		this(context, null);
	}

	public BrailleKeyboardLatin(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public BrailleKeyboardLatin(Context context, AttributeSet attrs)
	{
		super(context, attrs, 0);
		inflate(context, R.layout.braillekeyboard_latin, this);
		predictionBar = findViewById(R.id.predictionBar);
		cellsEnteredTextview = findViewById(R.id.inputCellsTextView);
	}

	public void setPredictions(List<BrailleSymbolTranslation> predsIn, BrailleDatabase symbolDatabase)
	{
		predictionBar.setPredictions(predsIn, symbolDatabase);
	}

	public void setPredictionItemClickedListener(Function1<? super BrailleSymbolTranslation, Unit> predictionItemClickedListener)
	{
		predictionBar.setPredictionItemClickedListener( predictionItemClickedListener);
		cellsEnteredTextview.setOnClickListener(view ->
		{
			predictionItemClickedListener.invoke(translationJustCells);
		});
	}

	public void setCellsEntered(BrailleSymbolTranslation translation, BrailleCell currentCell)
	{
		translationJustCells = new BrailleSymbolTranslation();

		StringBuilder text = new StringBuilder();
		for(BrailleSymbolDatabaseEntry entry : translation.getSymbols())
		{
			String entryText = entry.getSymbol().getUnicodeCellSymbol();
			if(entry.getCells().length > 1)
			{
				entryText = "<b>" + entryText + "</b>";
			}
			text.append(entryText);

			// fill translationJustCells with dummy BrailleSymbolDatabaseEntry for each cell
			for(BrailleCell cell : entry.getCells())
			{
				BrailleSymbolDatabaseEntry entryForJustCell = BrailleSymbolDatabaseEntry.Companion.LegacyConstructor(""+cell.getUnicodeCellSymbol(), DictionaryType.INVISIBLE, BrailleSymbolDatabaseEntry.OnEntryClickType.NOTHING, cell);
				translationJustCells.add(entryForJustCell);
			}
		}

		String currentCellFormat = "<u>" + currentCell.getUnicodeCellSymbol() + "</u>";
		text.append(currentCellFormat);

		if (currentCell != BrailleCellDatabase.CellEmpty)
		{
			BrailleSymbolDatabaseEntry entryForJustCell = BrailleSymbolDatabaseEntry.Companion.LegacyConstructor("" + currentCell.getUnicodeCellSymbol(), DictionaryType.INVISIBLE, BrailleSymbolDatabaseEntry.OnEntryClickType.NOTHING, currentCell);
			translationJustCells.add(entryForJustCell);
		}

		String htmlText = text.toString();
		cellsEnteredTextview.setText(Html.fromHtml(htmlText));
	}
}
