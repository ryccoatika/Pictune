package com.ryccoatika.pictune.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.collection.CollectionFragment
import com.ryccoatika.pictune.photo.PhotoFragment

class HomePagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    enum class HomeFragment(val titleRes: Int) {
        PHOTO(R.string.photos),
        COLLECTION(R.string.collections)
    }

    private val fragmentType = listOf(
        HomeFragment.PHOTO,
        HomeFragment.COLLECTION
    )

    override fun getItem(position: Int): Fragment {
        return when (fragmentType[position]) {
            HomeFragment.PHOTO -> PhotoFragment()
            HomeFragment.COLLECTION -> CollectionFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence =
        context.getString(fragmentType[position].titleRes)

    override fun getCount(): Int = fragmentType.size
}