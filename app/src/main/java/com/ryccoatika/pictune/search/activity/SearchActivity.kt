package com.ryccoatika.pictune.search.activity

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QUERY = "extra_query"
    }

    private lateinit var pagerAdapter: SearchPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        pagerAdapter = SearchPagerAdapter("", this, supportFragmentManager)
        view_pager.adapter = pagerAdapter
        tab_layout.setupWithViewPager(view_pager)

        // when search button on keyboard cliecked
        search_edit.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    if (v.text.isNotEmpty()) {
                        search_edit.clearFocus()
                        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                            .hideSoftInputFromWindow(search_edit.windowToken, 0)

                        performSearch(v.text.toString())
                    }
                    true
                }
                else -> false
            }
        }

        val query = intent.getStringExtra(EXTRA_QUERY)
        query?.let {
            search_edit.setText(it)
            performSearch(it)
        }
    }

    private fun performSearch(query: String) {
        pagerAdapter = SearchPagerAdapter(query, this, supportFragmentManager)

        view_pager.adapter = pagerAdapter
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
