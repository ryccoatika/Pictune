package com.ryccoatika.pictune.search.activity

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.checkbox.MaterialCheckBox
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.dialog_search_filter.*

class SearchFilterDialog(context: Context) : Dialog(context), AdapterView.OnItemSelectedListener,
    View.OnClickListener {

    var isApplied = false

    var orderBy: String? = null
    var color: String? = null
    var orientation: String? = null

    private val orderByValues: Array<String> by lazy {
        context.resources.getStringArray(R.array.order_by_values)
    }
    private val colorEntries: Array<String> by lazy {
        context.resources.getStringArray(R.array.color_entries)
    }
    private val colorValues: Array<String> by lazy {
        context.resources.getStringArray(R.array.color_values)
    }
    private val orientationValues: Array<String> by lazy {
        context.resources.getStringArray(R.array.orientation_values)
    }
    private val colorToneValues: Array<String> by lazy {
        context.resources.getStringArray(R.array.color_tone_values)
    }
    private val colorToneIds = listOf(
        R.id.filterColorBlack,
        R.id.filterColorWhite,
        R.id.filterColorYellow,
        R.id.filterColorOrange,
        R.id.filterColorRed,
        R.id.filterColorPurple,
        R.id.filterColorMagenta,
        R.id.filterColorGreen,
        R.id.filterColorTeal,
        R.id.filterColorBlue
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_search_filter)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // order by spinner
        ArrayAdapter.createFromResource(
            context,
            R.array.order_by_entries,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_order_by.adapter = adapter
        }

        // color spinner
        ArrayAdapter.createFromResource(
            context,
            R.array.color_entries,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_color.adapter = adapter
        }

        // add on click listener for color checkbox
        colorToneIds.forEach { resId ->
            findViewById<MaterialCheckBox>(resId)?.setOnClickListener {
                selectColorButton(it.id)
            }
        }

        // orientation spinner
        ArrayAdapter.createFromResource(
            context,
            R.array.orientation_entries,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_orientation.adapter = adapter
        }

        // set spinner item selected listener
        spinner_order_by.onItemSelectedListener = this
        spinner_color.onItemSelectedListener = this
        spinner_orientation.onItemSelectedListener = this

        // set button click listener
        btn_apply.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        initFilterData()
    }

    private fun initFilterData() {
        val orderByIndex = orderByValues.indexOf(orderBy ?: orderByValues[0])
        val orientationIndex = orientationValues.indexOf(orientation ?: orientationValues[0])
        // is color value is on spinner entries
        val isColorOnSpinner = colorValues.contains(color ?: colorValues[0])

        spinner_order_by.setSelection(orderByIndex)

        if (isColorOnSpinner) {
            val colorIndex = colorValues.indexOf(color ?: colorValues[0])
            spinner_color.setSelection(colorIndex)
            color_container.isVisible = false
        } else {
            spinner_color.setSelection(colorValues.lastIndex)
            color_container.isVisible = true
            selectColorButton(colorToneIds[colorToneValues.indexOf(color)])
        }
        spinner_orientation.setSelection(orientationIndex)
    }

    private fun saveFilterData() {
        val orderByIndex = spinner_order_by.selectedItemPosition
        val colorSpinnerIndex = spinner_color.selectedItemPosition
        val orientationIndex = spinner_orientation.selectedItemPosition

        orderBy = orderByValues[orderByIndex]
        color = if (colorSpinnerIndex == colorValues.lastIndex) {
            // when spinner on "another tone"
            getSelectedColor()
        } else {
            colorValues[colorSpinnerIndex]
        }
        orientation = orientationValues[orientationIndex]
    }

    private fun getSelectedColor(): String {
        var checkColor = ""
        colorToneIds.forEach {
            findViewById<MaterialCheckBox>(it).also { checkBox ->
                if (checkBox.isChecked) {
                    checkColor = colorToneValues[colorToneIds.indexOf(checkBox.id)]
                    return@forEach
                }
            }
        }
        return checkColor
    }

    private fun selectColorButton(id: Int) {
        // uncheck all of the color checkbox
        colorToneIds.forEach {
            findViewById<MaterialCheckBox>(it).isChecked = false
        }
        // change background on selected color checkbox
        // black background for white color
        if (id == R.id.filterColorWhite)
            findViewById<MaterialCheckBox>(id).apply {
                background = ContextCompat.getDrawable(context, R.drawable.color_selected_black)
                backgroundTintMode = null
                backgroundTintList = null
            }
        else
            findViewById<MaterialCheckBox>(R.id.filterColorWhite).apply {
                background =
                    ContextCompat.getDrawable(context, R.drawable.custom_color_radio_button)
                backgroundTintMode = PorterDuff.Mode.ADD
                backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWhite))
            }
        findViewById<MaterialCheckBox>(id).isChecked = true
    }

    fun resetFilter() {
        orderBy = null
        color = null
        orientation = null
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spinner_color -> {
                // when dialog spinner position on last index then showing the tone selection
                color_container.isVisible = parent.selectedItemPosition == colorEntries.lastIndex
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_apply -> {
                isApplied = true
                saveFilterData()
                dismiss()
            }
            R.id.btn_cancel -> {
                isApplied = false
                cancel()
            }
        }
    }
}