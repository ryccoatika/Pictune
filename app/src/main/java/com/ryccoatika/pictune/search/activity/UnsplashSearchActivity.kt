package com.ryccoatika.pictune.search.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.viewpager.widget.ViewPager
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.activity_unsplash_search.*

class UnsplashSearchActivity : AppCompatActivity() {

    private var viewPagerAdapter: UnsplashSearchActivityViewPager? = null

    private var query = ""
    private var menu: Menu? = null
    private val filterDialog: UnsplashSearchPhotosFilterDialog by lazy {
        UnsplashSearchPhotosFilterDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsplash_search)

        setSupportActionBar(unsplash_search_activity_toolbar)

        unsplash_search_activity_tab_layout.visibility = View.GONE

        unsplash_search_activity_search_edit.requestFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // when search button on keyboard cliecked
        unsplash_search_activity_search_edit.setOnEditorActionListener { v, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    unsplash_search_activity_tab_layout.visibility = View.VISIBLE
                    query = v.text.toString()
                    performSearch(query)
                    unsplash_search_activity_search_edit.clearFocus()
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(unsplash_search_activity_search_edit.windowToken, 0)
                    true
                }
                else -> false
            }
        }

        unsplash_search_activity_view_pager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // showing filter menu
                when (position) {
                    0 -> {
                        menuInflater.inflate(R.menu.unsplash_search_filter, menu)
                    }
                    else -> menu?.clear()
                }
            }

        })

        unsplash_search_activity_view_pager.addOnAdapterChangeListener { _, _, _ ->
            menu?.clear()
            menuInflater.inflate(R.menu.unsplash_search_filter, menu)
        }

        filterDialog.setOnDismissListener {
            with (filterDialog) {
                if (isApplied) {
                    viewPagerAdapter?.updateFilterForPhotos(orderBy, contentFilter, color, orientation)
                    viewPagerAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun performSearch(
        query: String,
        orderBy: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null
    ) {
        viewPagerAdapter = UnsplashSearchActivityViewPager(
            this,
            query,
            supportFragmentManager,
            orderBy,
            contentFilter,
            color,
            orientation
        )
        unsplash_search_activity_view_pager.adapter = viewPagerAdapter
        unsplash_search_activity_view_pager.offscreenPageLimit = 3
        unsplash_search_activity_tab_layout.setupWithViewPager(unsplash_search_activity_view_pager)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.unsplash_search_filter_button -> {
                filterDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
