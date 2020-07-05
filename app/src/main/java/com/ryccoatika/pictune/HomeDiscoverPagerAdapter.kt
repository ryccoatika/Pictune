package com.ryccoatika.pictune

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ryccoatika.pictune.discoverCollections.UnsplashDiscoverCollectionsFragment
import com.ryccoatika.pictune.discoverPhotos.UnsplashDiscoverPhotosFragment

class HomeDiscoverPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val TAB_TITLES = intArrayOf(R.string.tab_discover_photos, R.string.tab_discover_collections)

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UnsplashDiscoverPhotosFragment()
            1 -> UnsplashDiscoverCollectionsFragment()
            else -> UnsplashDiscoverPhotosFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? = context.getString(TAB_TITLES[position])
    override fun getCount(): Int = TAB_TITLES.count()
}