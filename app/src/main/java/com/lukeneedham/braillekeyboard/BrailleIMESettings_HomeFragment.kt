package com.lukeneedham.braillekeyboard

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lukeneedham.braillekeyboard.Util.*
import com.lukeneedham.braillekeyboard.braillekeyboardview.getDefaultDictionaryTypes
import com.lukeneedham.braillekeyboard.setpredictiontypeorder.SetTypeOrderDialog
import kotlinx.android.synthetic.main.settingsactivity_home.*

/*
 * Created by Luke on 06/06/2018.
 */
class BrailleIMESettings_HomeFragment : Fragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.settingsactivity_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.setDatabaseButton).setOnClickListener {
            val dialog = ChangeDatabaseDialog()
            val prefsRepo = PreferencesRepository(view.context)
            dialog.previousDatabase = prefsRepo.symbolDatabaseType
            dialog.onDatabaseChangeListener = {
                val newSymbolDatabase = it.database
                prefsRepo.symbolDatabaseType = it
                prefsRepo.dictionaryTypesTranslationPriority = getDefaultDictionaryTypes(newSymbolDatabase, view.context)
            }

            dialog.show(fragmentManager, "")
        }

        setupSwitch(view.findViewById(R.id.alwaysShowPredictionsSwitch), ALWAYS_SHOW_PREDICTIONS_MODE, ALWAYS_SHOW_PREDICTIONS_MODE_DEFAULT)
        setupSwitch(view.findViewById(R.id.cellAsPredictionSwitch), SHOW_CELL_AS_PREDICTION_MODE, SHOW_CELL_AS_PREDICTION_MODE_DEFAULT)

        val autoSpaceAfterStandalone = view.findViewById<Switch>(R.id.autoinsertSpaceStandalone)
        setupSwitch(autoSpaceAfterStandalone, AUTO_INSERT_SPACE_AFTER_STANDALONE, AUTO_INSERT_SPACE_AFTER_STANDALONE_DEFAULT)
        autoSpaceAfterStandalone.isEnabled = Util.getSharedPrefs(context).getBoolean(OBEY_BRAILLE_USAGE_RULE_MODE, OBEY_BRAILLE_USAGE_RULE_MODE_DEFAULT)

        setupSwitch(view.findViewById(R.id.obeyBrailleSymbolRules), OBEY_BRAILLE_USAGE_RULE_MODE, OBEY_BRAILLE_USAGE_RULE_MODE_DEFAULT) { autoSpaceAfterStandalone.isEnabled = it }

        setupSwitch(view.findViewById(R.id.setRectoVersoSwitch), RECTO_VERSO_MODE, RECTO_VERSO_MODE_DEFAULT)

        view.findViewById<Button>(R.id.changeSizeButton).setOnClickListener { (activity as CanChangeFragment).setFragment(BrailleIMESettings_ChangeSizeFragment()) }
        // view.findViewById<Button>(R.id.donateButton).setOnClickListener {  }


        val autoFillSpinner = view.findViewById<Spinner>(R.id.autoFillTranslationMode)
        val autoFillModeAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.autoFillBrailleMode, R.layout.spinner_item_switch_text)
        autoFillModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        autoFillSpinner.adapter = autoFillModeAdapter

        autoFillSpinner.setSelection(Util.getSharedPrefs(context).getInt(AUTOFILL_PREDICTION_MODE, AUTOFILL_PREDICTION_MODE_DEFAULT))

        autoFillSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                Util.getSharedPrefs(context).edit().putInt(AUTOFILL_PREDICTION_MODE, position).apply()
                setPredictionTypeOrderButton.visibility = if (position == AUTOFILL_PREDICTION_MODE_CUSTOM) View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>?)
            {
            }
        }

        setPredictionTypeOrderButton.setOnClickListener {
            SetTypeOrderDialog().show(fragmentManager, "SetTypeOrderDialog")
        }

        (activity as HasToolbar).showToolbar()
    }

    private fun setupSwitch(switch: Switch, key: String, default: Boolean, extraCallback: (Boolean) -> Unit = {})
    {
        val loaded = Util.getSharedPrefs(context).getBoolean(key, default)
        Log.v("loaded for $key is", loaded.toString());
        switch.isChecked = loaded
        switch.setOnCheckedChangeListener { _, isChecked -> Util.getSharedPrefs(context).edit().putBoolean(key, isChecked).apply(); extraCallback(isChecked) }
    }
}