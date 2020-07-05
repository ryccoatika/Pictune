package com.ryccoatika.pictune.search.activity

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.ryccoatika.pictune.R

class UnsplashSearchActivityViewPager(val context: Context, val query: String, fm: FragmentManager):
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    var orderBy: String? = null
    var contentFilter: String? = null
    var color: String? = null
    var orientation: String? = null

    constructor(
        context: Context,
        query: String,
        fm: FragmentManager,
        orderBy: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null
    ): this(context, query, fm) {
        this.orderBy = orderBy
        this.contentFilter = contentFilter
        this.color = color
        this.orientation = orientation
    }

    private val TAB_TITLES = arrayOf(
        R.string.tab_search_photos,
        R.string.tab_search_collections,
        R.string.tab_search_users
    )

    fun updateFilterForPhotos(
        orderBy: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null
    ) {
        this.orderBy = orderBy
        this.contentFilter = contentFilter
        this.color = color
        this.orientation = orientation
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> UnsplashSearchResultFragment.newInstance(
                query,
                UnsplashSearchResultFragment.MODE_PHOTOS,
                orderBy,
                contentFilter,
                color,
                orientation)
            1 -> UnsplashSearchResultFragment.newInstance(query, UnsplashSearchResultFragment.MODE_COLLECTIONS)
            2 -> UnsplashSearchResultFragment.newInstance(query, UnsplashSearchResultFragment.MODE_USERS)
            else -> Fragment()
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        (`object` as UnsplashSearchResultFragment).updateFilter(orderBy, contentFilter, color, orientation)
        return super.getItemPosition(`object`)
    }

    override fun getCount(): Int = TAB_TITLES.count()

    override fun getPageTitle(position: Int): CharSequence? = context.getString(TAB_TITLES[position])

}