package com.lukeneedham.braillekeyboard

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lukeneedham.brailledatabase.Braille.DictionaryType
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabase
import com.lukeneedham.brailledatabase.Braille.SymbolDatabases.BrailleDatabaseType
import com.lukeneedham.braillekeyboard.PreferencesRepository.Companion.DEFAULT_PREFS
import java.lang.reflect.Type

/**
 * Created by Luke on 07/10/2018.
 */

class PreferencesRepository(context: Context)
{
    private val sharedPreferences = getDefaultSharedPreferences(context)
    private val gson = Gson()

    var dictionaryTypesTranslationPriority: List<DictionaryType>?
        get()
        {
            val listType = object : TypeToken<List<DictionaryType>>()
            {}.type
            val typeOrder: List<DictionaryType>? = fromSharedPrefGson(DICTIONARY_TYPES_TRANSLATION_PRIORITY, listType)
            if(typeOrder.isNullOrEmpty()) return null
            return typeOrder
        }
        set(value)
        {
            saveSharedPrefGson(DICTIONARY_TYPES_TRANSLATION_PRIORITY, value)
        }

    var symbolDatabaseType: BrailleDatabaseType
        get()
        {
            return BrailleDatabaseType.fromSave(sharedPreferences.getString(BRAILLE_DATABASE, BRAILLE_DATABASE_DEFAULT))
        }
        set(value)
        {
            sharedPreferences.edit().putString(BRAILLE_DATABASE, value.toSave()).apply()
        }


    private fun saveSharedPrefGson(key: String, data: Any?)
    {
        val json = gson.toJson(data)
        sharedPreferences.edit().putString(key, json).apply()
    }

    private fun <T> fromSharedPrefGson(key: String, clazz: Class<T>): T?
    {
        val json = sharedPreferences.getString(key, null)
        return gson.fromJson(json, clazz)
    }

    private fun <T> fromSharedPrefGson(key: String, type: Type): T?
    {
        val json = sharedPreferences.getString(key, null) ?: return null
        return gson.fromJson(json, type)
    }


    companion object
    {
        const val DEFAULT_PREFS = "MySharedPreferences"
        val DICTIONARY_TYPES_TRANSLATION_PRIORITY = "DICTIONARY_TYPES_TRANSLATION_PRIORITY"

        val BRAILLE_DATABASE = "BRAILLE_DATABASE"
        val BRAILLE_DATABASE_DEFAULT = BrailleDatabaseType.UEB.toSave()
    }
}

fun getDefaultSharedPreferences(context: Context): SharedPreferences
{
    return context.getSharedPreferences(DEFAULT_PREFS, 0)
}