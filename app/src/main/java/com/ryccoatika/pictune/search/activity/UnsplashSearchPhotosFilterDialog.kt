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
import com.google.android.material.checkbox.MaterialCheckBox
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.unsplash_search_filter_dialog.*

class UnsplashSearchPhotosFilterDialog(context: Context)
    : Dialog(context), AdapterView.OnItemSelectedListener, View.OnClickListener {

    var isApplied = false

    var orderBy: String? = null
    var contentFilter: String? = null
    var color: String? = null
    var orientation: String? = null

    private val orderByValues: Array<String> by lazy {
        context.resources.getStringArray(R.array.order_by_values)
    }
    private val contentFilterValues: Array<String> by lazy {
        context.resources.getStringArray(R.array.content_filter_values)
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

    // colorToneValues and colorToneIds must be the same queue position
    // these are for comparison
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
        setContentView(R.layout.unsplash_search_filter_dialog)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)

        // order by spinner
        ArrayAdapter.createFromResource(
            context,
            R.array.order_by_entries,
            R.layout.unsplash_search_filter_dialog_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unsplash_search_filter_dialog_spinner_order_by.adapter = adapter
        }

        // content filter spinner
        ArrayAdapter.createFromResource(
            context,
            R.array.content_filter_entries,
            R.layout.unsplash_search_filter_dialog_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unsplash_search_filter_dialog_spinner_content_filter.adapter = adapter
        }

        // color spinner
        ArrayAdapter.createFromResource(
            context,
            R.array.color_entries,
            R.layout.unsplash_search_filter_dialog_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unsplash_search_filter_dialog_spinner_color.adapter = adapter
        }

        // add on click listener for color checkbox
        colorToneIds.forEach {
            findViewById<MaterialCheckBox>(it).setOnClickListener {
                selectColorButton(it.id)
            }
        }

        // orientation spinner
        ArrayAdapter.createFromResource(
            context,
            R.array.orientation_entries,
            R.layout.unsplash_search_filter_dialog_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            unsplash_search_filter_dialog_spinner_orientation.adapter = adapter
        }

        // set spinner item selected listener
        unsplash_search_filter_dialog_spinner_order_by.onItemSelectedListener = this
        unsplash_search_filter_dialog_spinner_content_filter.onItemSelectedListener = this
        unsplash_search_filter_dialog_spinner_color.onItemSelectedListener = this
        unsplash_search_filter_dialog_spinner_orientation.onItemSelectedListener = this

        // set button click listener
        unsplash_search_filter_dialog_btn_apply.setOnClickListener(this)
        unsplash_search_filter_dialog_btn_cancel.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        initFilterData()
    }

    private fun initFilterData() {
        val orderByIndex = orderByValues.indexOf(orderBy ?: orderByValues[0])
        val contentFilterIndex = contentFilterValues.indexOf(contentFilter ?: contentFilterValues[0])
        val orientationIndex = orientationValues.indexOf(orientation ?: orientationValues[0])
        // is color value is on spinner entries
        val isColorOnSpinner = colorValues.contains(color ?: colorValues[0])

        unsplash_search_filter_dialog_spinner_order_by.setSelection(orderByIndex)
        unsplash_search_filter_dialog_spinner_content_filter.setSelection(contentFilterIndex)

        if (isColorOnSpinner) {
            val colorIndex = colorValues.indexOf(color ?: colorValues[0])
            unsplash_search_filter_dialog_spinner_color.setSelection(colorIndex)
            unsplash_search_filter_dialog_container_4.visibility = View.GONE
        } else {
            unsplash_search_filter_dialog_spinner_color.setSelection(colorValues.lastIndex)
            unsplash_search_filter_dialog_container_4.visibility = View.VISIBLE
            selectColorButton(colorToneIds[colorToneValues.indexOf(color)])
        }
        unsplash_search_filter_dialog_spinner_orientation.setSelection(orientationIndex)
    }

    private fun saveFilterData() {
        val orderByIndex = unsplash_search_filter_dialog_spinner_order_by.selectedItemPosition
        val contentFilterIndex = unsplash_search_filter_dialog_spinner_content_filter.selectedItemPosition
        val colorSpinnerIndex = unsplash_search_filter_dialog_spinner_color.selectedItemPosition
        val orientationIndex = unsplash_search_filter_dialog_spinner_orientation.selectedItemPosition

        orderBy = orderByValues[orderByIndex]
        contentFilter = contentFilterValues[contentFilterIndex]
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
        colorToneIds.forEach{
            findViewById<MaterialCheckBox>(it).isChecked = false
        }
        // change background on selected color checkbox
        // black background for white color
        if (id == R.id.filterColorWhite)
            findViewById<MaterialCheckBox>(id).apply {
                background = context.getDrawable(R.drawable.color_selected_black)
                backgroundTintMode = null
                backgroundTintList = null
            }
        else
            findViewById<MaterialCheckBox>(R.id.filterColorWhite).apply {
                background = context.getDrawable(R.drawable.custom_color_radio_button)
                backgroundTintMode = PorterDuff.Mode.ADD
                backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.colorWhite))
            }
        findViewById<MaterialCheckBox>(id).isChecked = true
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent?.id) {
            R.id.unsplash_search_filter_dialog_spinner_color -> {
                // when dialog spinner position on last index then showing the tone selection
                if (parent.selectedItemPosition == colorEntries.lastIndex) {
                    unsplash_search_filter_dialog_container_4.visibility = View.VISIBLE
                } else {
                    unsplash_search_filter_dialog_container_4.visibility = View.GONE
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.unsplash_search_filter_dialog_btn_apply -> {
                isApplied = true
                saveFilterData()
                dismiss()
            }
            R.id.unsplash_search_filter_dialog_btn_cancel -> {
                isApplied = false
                cancel()
            }
        }
    }
}