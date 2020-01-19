package com.lukeneedham.braillekeyboard

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.lukeneedham.brailledatabase.Braille.*
import com.lukeneedham.brailledatabase.Braille.BrailleCellDatabase.CellEmpty
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase.Companion.NO_STRING
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabaseType
import com.lukeneedham.braillekeyboard.braillekeyboardview.BrailleKeyboardLatin
import com.lukeneedham.braillekeyboard.Util.*
import com.lukeneedham.braillekeyboard.braillekeyboardview.getDefaultDictionaryTypes

class BrailleIMELatin(service: BrailleIMEService) : BrailleIME(service)
{
    private val symbolDatabase: BrailleDatabase

    private var currentCell = BrailleCellDatabase.CellEmpty
        get() = keyboard.brailleGrid.input

    private val brailleSymbolsInWord = BrailleSymbolTranslation()

    private val cellsInWord: List<BrailleCell>
        get() = brailleSymbolsInWord.cells

    private lateinit var keyboard: BrailleKeyboardLatin

    private var expectedSelStart = 0
    private var expectedSelEnd = 0

    private var prevSelStart = 0
    private var prevSelEnd = 0


    private var defaultPrediction: BrailleSymbolTranslation? = null

    private var deletedDifferentComposingText: String = ""


    init
    {
        service.onUpdateSelectionListener = ::selectionUpdate
        val prefsRepo = PreferencesRepository(service)
        symbolDatabase = prefsRepo.symbolDatabaseType.database
        if(sharedPrefsRepository.dictionaryTypesTranslationPriority == null) {
            sharedPrefsRepository.dictionaryTypesTranslationPriority = getDefaultDictionaryTypes(symbolDatabase, service)
        }
    }

    override fun onCreateInputView(): View
    {
        keyboard = BrailleKeyboardLatin(service)

        keyboard.setPredictionItemClickedListener { predictionSelected(it) }

        val cursorPos = getAllTextBeforeCursor().length
        // Log.v("cursorPos", cursorPos.toString())
        expectedSelStart = cursorPos
        expectedSelEnd = cursorPos

        reset()

        val layout = setupKeyboard(keyboard)

        return layout
    }

    private fun selectionUpdate(oldSelStart: Int, oldSelEnd: Int, newSelStart: Int, newSelEnd: Int, candidatesStart: Int, candidatesEnd: Int)
    {
        Log.v("selectionUpdate", "start: " + expectedSelStart.toString() + " -> " + newSelStart.toString() + ", end: " + expectedSelEnd + " -> " + newSelEnd)

        if (newSelStart != expectedSelStart || newSelEnd != expectedSelEnd)
        {
            prevSelStart = expectedSelStart
            prevSelEnd = expectedSelEnd

            expectedSelStart = newSelStart
            expectedSelEnd = newSelEnd

            //Log.v("composingText", "=" + composingText)
            if (composingText == "")
            //if (currentCell == CellEmpty)
            {
                Log.v("selectionUpdate", "reset")
                reset()
            }
        }
    }

    private fun getAllTextBeforeCursor(): CharSequence
    {
        return inputConnection?.getTextBeforeCursor(Int.MAX_VALUE, 0) ?: ""
    }

    override fun backspaceButtonClicked()
    {
        if (currentCell == CellEmpty)
        {
            if (brailleSymbolsInWord.symbols.size > 0)
            {
                doBatchEdit {
                    clearTextEntered()
                    brailleSymbolsInWord.pop()
                    fillTextEntered()
                }
            }
            else
            {
                if (TextUtils.isEmpty(inputConnection?.getSelectedText(0)))
                {
                    deleteTextBeforeCursorInputConnection(1)
                }
                else
                {
                    commitTextToInputConnection("")
                }
            }
        }
        else
        {
            clearComposingText()
            keyboard.clearBrailleGrid()
        }

        fillPredictions()
    }

    private fun reset()
    {
        keyboard.clearBrailleGrid()
        defaultPrediction = BrailleSymbolTranslation()

        brailleSymbolsInWord.clear()
        updateCellsEntered()
        keyboard.setPredictions(listOf(), symbolDatabase)
    }

    private fun clearTextEntered()
    {
        val lenToDelete = brailleSymbolsInWord.getLatinTranslation(symbolDatabase)?.length ?: 0
        deleteTextBeforeCursorInputConnection(lenToDelete)
    }

    private fun fillTextEntered()
    {
        val predictionText = brailleSymbolsInWord.getLatinTranslation(symbolDatabase) ?: ""
        commitTextToInputConnection(predictionText)
    }

    override fun inputButtonClicked()
    {
        if (currentCell == BrailleCellDatabase.CellEmpty)
        {
            brailleSymbolsInWord.clear()
            commitTextToInputConnection(" ")
        }
        else
        {
            val defPred = defaultPrediction
            if (defPred == null)
            {
                Toast.makeText(service, "No valid translation for these cells", Toast.LENGTH_SHORT).show()
            }
            else
            {
                predictionSelected(defPred)
            }
        }

        keyboard.clearBrailleGrid()
        fillPredictions()
    }

    override fun dotButtonClicked()
    {
        fillPredictions()
    }

    private fun updateCellsEntered()
    {
        keyboard.setCellsEntered(brailleSymbolsInWord, currentCell)
    }


    private fun moveExpectedSelection(offset: Int)
    {
        Log.v("change expected offset", " by " + offset.toString())
        expectedSelStart += offset
        expectedSelEnd += offset

        if (expectedSelStart < 0)
        {
            expectedSelStart = 0
        }

        if (expectedSelEnd < 0)
        {
            expectedSelEnd = 0
        }
    }

    override fun deleteTextBeforeCursorInputConnection(length: Int)
    {
        if (inputConnection != null)
        {
            moveExpectedSelection(-length)
            super.deleteTextBeforeCursorInputConnection(length)
        }
    }

    override fun commitTextToInputConnection(text: String)
    {
        if (inputConnection != null)
        {
            moveExpectedSelection(text.length)
            super.commitTextToInputConnection(text)
        }
    }

    override fun updateComposingText(s: String)
    {
        if (inputConnection != null)
        {
            moveExpectedSelection(-composingText.length)
            moveExpectedSelection(s.length)
            super.updateComposingText(s)
        }
    }

    override fun clearComposingText()
    {
        if (inputConnection != null)
        {
            moveExpectedSelection(-composingText.length)
            super.clearComposingText()
        }
    }

    private fun doBatchEdit(toDo: () -> Unit)
    {
        if (inputConnection != null)
        {
            inputConnection.beginBatchEdit()
            // Log.v("batchEdit", "begin")
            toDo()
            inputConnection.endBatchEdit()
            // Log.v("batchEdit", "end")
        }
    }

    private fun predictionSelected(prediction: BrailleSymbolTranslation)
    {
        // Log.v("predictionSelected", prediction.toString())

        doBatchEdit {
            clearComposingText()
            clearTextEntered()

            brailleSymbolsInWord.clear()

            // exclude any word splitters which may be in the prediction, and set brailleSymbolsInWord to be the symbols after the last word splitter
            val lastSpacePos = prediction.symbols.indexOfLast { it == symbolDatabase.space }
            // Log.v("pred symbols start", (lastSpacePos + 1).toString())
            // Log.v("pred symbols end", prediction.symbols.size.toString())

            if (lastSpacePos == -1)
            {
                brailleSymbolsInWord.addAll(prediction.symbols)
                fillTextEntered()
            }
            else
            {
                brailleSymbolsInWord.addAll(prediction.symbols.subList(0, lastSpacePos + 1))
                fillTextEntered()
                brailleSymbolsInWord.clear()

                brailleSymbolsInWord.addAll(prediction.symbols.subList(lastSpacePos + 1, prediction.symbols.size))
                fillTextEntered()
            }
        }

        keyboard.clearBrailleGrid()
        fillPredictions()
        deletedDifferentComposingText = ""
    }

    private fun getDefaultSymbolForCell(cell: BrailleCell): BrailleSymbolDatabaseEntry
    {
        val results = symbolDatabase.findSymbolDataBySymbol(BrailleSymbol(cell))
        return if (results.size > 0) results[0] else makePlaceholderSymbolForCells(listOf(cell))
    }

    private fun makePlaceholderSymbolForCells(cells: List<BrailleCell>): BrailleSymbolDatabaseEntry
    {
        return BrailleSymbolDatabaseEntry(listOf(NO_STRING), dictionaryType = DictionaryType.INVISIBLE, cells = cells)
    }

    private fun findPredictionsForSplit(splitPos: Int, wordCellsAndCurrent: List<BrailleCell>): List<BrailleSymbolTranslation>
    {
        val preds = mutableListOf<BrailleSymbolTranslation>()

        val cellsToLookup = wordCellsAndCurrent.subList(splitPos, wordCellsAndCurrent.size).toList()
        val brailleDataEntries = symbolDatabase.findSymbolDataBySymbol(BrailleSymbol(cellsToLookup))

        // find point where brailleSymbolsInWord is split by this

        val translationPosInfo = if (splitPos < brailleSymbolsInWord.cells.size) brailleSymbolsInWord.findAtCellPosition(splitPos) else Pair<Int, Int>(brailleSymbolsInWord.symbols.size, 0)
        // Log.v("splitPos", splitPos.toString())
        // Log.v("brailleSymbolsInWord", brailleSymbolsInWord.nestedCells.toString())
        // Log.v("translationPosInfo", translationPosInfo.toString())

        if (translationPosInfo != null)
        {
            val symbolPos = translationPosInfo.first
            val cellPosInSymbol = translationPosInfo.second

            val preTranslation = BrailleSymbolTranslation(brailleSymbolsInWord.symbols.subList(0, symbolPos))

            if (cellPosInSymbol != 0)
            {
                // some dataEntry is split - have to re-calculate
                val splitSymbolCells = brailleSymbolsInWord.symbols[symbolPos].cells.toList()
                val preSplitCells = splitSymbolCells.subList(0, cellPosInSymbol)

                for (cell in preSplitCells)
                {
                    preTranslation.add(getDefaultSymbolForCell(cell))
                }
            }

            for (brailleDataEntry in brailleDataEntries)
            {
                val pred = BrailleSymbolTranslation(preTranslation)
                val predLatinPrev = pred.getLatinTranslation(symbolDatabase)

                pred.add(brailleDataEntry)

                val respectUsageRules = sharedPrefs.getBoolean(OBEY_BRAILLE_USAGE_RULE_MODE, OBEY_BRAILLE_USAGE_RULE_MODE_DEFAULT)
                val addSpaceAfterStandalone = sharedPrefs.getBoolean(AUTO_INSERT_SPACE_AFTER_STANDALONE, AUTO_INSERT_SPACE_AFTER_STANDALONE_DEFAULT)

                if (respectUsageRules && addSpaceAfterStandalone && brailleDataEntry.ruleForUsage == BrailleSymbolUsageRule.STANDALONE)
                {
                    pred.add(symbolDatabase.space)
                }

                val predLatin = pred.getLatinTranslation(symbolDatabase)

                if (predLatinPrev != null && predLatin != null)
                {
                    // check that usage rules are respected, if option selected
                    if (!respectUsageRules || brailleDataEntry.ruleForUsage.isLegal(predLatinPrev.length, predLatin.length, predLatin))
                    {
                        fun repeatLatinTranslation(): Boolean
                        {
                            for (p in preds)
                            {
                                if (p.getLatinTranslation(symbolDatabase) == predLatin)
                                {
                                    return true
                                }
                            }
                            return false
                        }


                        // Log.v(pred.unicode, predLatin ?: "null")

                        if (!repeatLatinTranslation())
                        {
                            preds.add(pred)
                        }
                    }
                }
            }
        }
        return preds
    }

    private fun fillPredictions()
    {
        val preds = mutableListOf<BrailleSymbolTranslation>()

        val wordCellsAndCurrent = cellsInWord.toMutableList()
        if (currentCell != BrailleCellDatabase.CellEmpty)
        {
            wordCellsAndCurrent.add(currentCell)
        }

        // Log.v("preds - wordCellsAndCurrent", wordCellsAndCurrent.toString())

        for (splitPos in wordCellsAndCurrent.size - 1 downTo 0)
        {
            preds.addAll(findPredictionsForSplit(splitPos, wordCellsAndCurrent))
        }

        // add a placeholder symbol when the currentcell doesnt mean anything on its own - for example contraction modifiers
        if (preds.isEmpty() && currentCell != BrailleCellDatabase.CellEmpty)
        {
            val newPred = BrailleSymbolTranslation(brailleSymbolsInWord)
            newPred.add(getDefaultSymbolForCell(currentCell))
            // Log.v("no entries- placeholder", newPred.toString())
            val predLatin = newPred.getLatinTranslation(symbolDatabase)
            if (predLatin != null)
            {
                preds.add(newPred)
            }
        }

        // Log.v("preds", preds.toString())

        val defaultPredMode = Util.getSharedPrefs(service).getInt(AUTOFILL_PREDICTION_MODE, AUTOFILL_PREDICTION_MODE_DEFAULT)
        if (preds.size > 0)
        {
            when (defaultPredMode)
            {
                AUTOFILL_PREDICTION_MODE_SIMPLEST ->
                {
                    // already in right order
                }
                AUTOFILL_PREDICTION_MODE_MOST_COMPLEX ->
                {
                    sortPredictionsByTypePriority(getDefaultDictionaryTypes(symbolDatabase, service).reversed(), preds)
                }
                AUTOFILL_PREDICTION_MODE_LONGEST ->
                {
                    preds.sortByDescending {
                        val res = it.getLatinTranslation(symbolDatabase)?.length ?: 0
                        res
                    }
                }
                AUTOFILL_PREDICTION_MODE_CUSTOM ->
                {
                    val dictionaryTypesTranslationPriority = sharedPrefsRepository.dictionaryTypesTranslationPriority ?: getDefaultDictionaryTypes(symbolDatabase, service)
                    sortPredictionsByTypePriority(dictionaryTypesTranslationPriority, preds)
                }
            }
        }

        val defaultPred = if (preds.size > 0) preds[0] else null

        if (currentCell == CellEmpty)
        {
            updateComposingText("")
        }
        else
        {
            updateDefaultPredictionAndComposing(defaultPred)
        }

        val showCellAsPrediction = sharedPrefs.getBoolean(SHOW_CELL_AS_PREDICTION_MODE, SHOW_CELL_AS_PREDICTION_MODE_DEFAULT)
        if (showCellAsPrediction && currentCell != CellEmpty)
        {
            val predictionWithCellAlone = BrailleSymbolTranslation(brailleSymbolsInWord)
            val entryForJustCell = makeDataEntryForJustCell(currentCell)
            predictionWithCellAlone.add(entryForJustCell)
            preds.add(predictionWithCellAlone)
        }

        val alwaysShowPredictions = sharedPrefs.getBoolean(ALWAYS_SHOW_PREDICTIONS_MODE, ALWAYS_SHOW_PREDICTIONS_MODE_DEFAULT)
        if (alwaysShowPredictions || (currentCell != BrailleCellDatabase.CellEmpty))
        {
            keyboard.setPredictions(preds, symbolDatabase)
        }
        else
        {
            keyboard.setPredictions(listOf<BrailleSymbolTranslation>(), symbolDatabase)
        }

        updateCellsEntered()
    }

    private fun sortPredictionsByTypePriority(priority: List<DictionaryType>, preds: MutableList<BrailleSymbolTranslation>) {

        val allPredsTypeCounts = HashMap<BrailleSymbolTranslation, HashMap<DictionaryType, Int>>()

        for (pred in preds)
        {
            val currentTypeCount = HashMap<DictionaryType, Int>()
            for (symbol in pred.symbols)
            {
                val currentType = symbol.dictionaryType
                currentTypeCount[currentType] = currentTypeCount[currentType]?.plus(1) ?: 1
            }

            //Log.v("pred type count", pred.toString() + " : " + currentTypeCount.toString())

            allPredsTypeCounts[pred] = currentTypeCount
        }

        val comparatorForAllTypes = mutableListOf<Comparator<BrailleSymbolTranslation>>()
        for (type in priority)
        {
            val comparatorForType: Comparator<BrailleSymbolTranslation> = Comparator { o1, o2 ->
                val o1Count = allPredsTypeCounts[o1]?.get(type) ?: 0
                val o2Count = allPredsTypeCounts[o2]?.get(type) ?: 0
                o1Count.compareTo(o2Count)
            }
            comparatorForAllTypes.add(comparatorForType)
        }

        val overallComparator = Comparator<BrailleSymbolTranslation> { o1, o2 ->
            for (comp in comparatorForAllTypes)
            {
                val res = comp.compare(o2, o1)
                if (res != 0)
                {
                    return@Comparator res
                }
            }
            0
        }

        preds.sortWith(overallComparator)
    }

    private fun updateComposingTextForDefaultPrediction()
    {
        val pred = defaultPrediction

        if (deletedDifferentComposingText != "") // text was deleted and the prediction in question was not chosen, so but it back
        {
            // Log.v("re commit deleted", deletedDifferentComposingText)
            commitTextToInputConnection(deletedDifferentComposingText)
            deletedDifferentComposingText = ""
        }

        val newComposingText = if (pred == null) ""
        else
        {
            val previousPredLatin = brailleSymbolsInWord.getLatinTranslation(symbolDatabase) ?: ""
            // Log.v("previousPredLatin", previousPredLatin)
            val predLatin = pred.getLatinTranslation(symbolDatabase) ?: ""

            // Log.v("previousPredLatin", previousPredLatin)
            // Log.v("predLatin", predLatin)

            val prefix = previousPredLatin.commonPrefixWith(predLatin)
            if (prefix == previousPredLatin) // normal case
            {
                // Log.v("pred case", "normal")
                predLatin.substring(previousPredLatin.length, predLatin.length)
            }
            else if (prefix == predLatin) //backspace
            {
                // Log.v("pred case", "backspace")
                ""
            }
            else
            {
                // Log.v("pred case", "other")
                deletedDifferentComposingText = previousPredLatin
                // Log.v("deletedDiff set", deletedDifferentComposingText)
                deleteTextBeforeCursorInputConnection(previousPredLatin.length)
                predLatin
            }
        }

        // Log.v("newComposingText", newComposingText)

        updateComposingText(newComposingText)
    }

    private fun updateDefaultPredictionAndComposing(pred: BrailleSymbolTranslation?)
    {
        defaultPrediction = pred
        updateComposingTextForDefaultPrediction()
    }

    private fun makeDataEntryForJustCell(cell: BrailleCell): BrailleSymbolDatabaseEntry
    {
        return BrailleSymbolDatabaseEntry(listOf("" + cell.unicodeCellSymbol), dictionaryType = DictionaryType.INVISIBLE, onEntryClickType = BrailleSymbolDatabaseEntry.OnEntryClickType.NOTHING, cells = listOf(cell))
    }
}