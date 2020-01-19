package com.lukeneedham.braillekeyboard.braillekeyboardview

import android.content.Context
import com.lukeneedham.brailledatabase.Braille.DictionaryType
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase

fun getDefaultDictionaryTypes(database: BrailleDatabase, context: Context): List<DictionaryType>
{
    return database.dictionaryTypes.toMutableList().filter { it.getName(context).isNotBlank() }
}