package com.ryccoatika.pictune.user

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.user.inventory.UnsplashUserInventoryFragment

class UnsplashUserPagerAdapter(
    val context: Context,
    val total: List<Int>,
    val username: String,
    fragmentManager: FragmentManager)
    : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val TAB_TITLES = listOf(
        R.string.tab_user_photos,
        R.string.tab_user_likes,
        R.string.tab_user_collections
    )

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UnsplashUserInventoryFragment
                .newInstance(username, UnsplashUserInventoryFragment.MODE_PHOTOS)
            1 -> UnsplashUserInventoryFragment
                .newInstance(username, UnsplashUserInventoryFragment.MODE_LIKES)
            2 -> UnsplashUserInventoryFragment
                .newInstance(username, UnsplashUserInventoryFragment.MODE_COLLECTIONS)
            else -> UnsplashUserInventoryFragment
                .newInstance(username, UnsplashUserInventoryFragment.MODE_PHOTOS)
        }
    }

    override fun getCount(): Int = TAB_TITLES.count()

    override fun getPageTitle(position: Int): CharSequence? {
        return "${total[position]} ${context.getString(TAB_TITLES[position])}"
    }

}