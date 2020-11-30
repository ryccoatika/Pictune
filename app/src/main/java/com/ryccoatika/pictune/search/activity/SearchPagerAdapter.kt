package com.ryccoatika.pictune.search.activity

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.search.activity.collections.SearchCollectionFragment
import com.ryccoatika.pictune.search.activity.photos.SearchPhotoFragment
import com.ryccoatika.pictune.search.activity.users.SearchUserFragment

class SearchPagerAdapter(
    private val query: String,
    private val context: Context,
    fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    enum class SearchFragment(val titleRes: Int) {
        PHOTO(R.string.photos),
        COLLECTION(R.string.collections),
        USER(R.string.users)
    }

    private val fragmentTypes = listOf(
        SearchFragment.PHOTO,
        SearchFragment.COLLECTION,
        SearchFragment.USER
    )

    override fun getPageTitle(position: Int): CharSequence =
        context.getString(fragmentTypes[position].titleRes)

    override fun getCount(): Int = fragmentTypes.size

    override fun getItem(position: Int): Fragment {
        return when (fragmentTypes[position]) {
            SearchFragment.PHOTO -> SearchPhotoFragment(query)
            SearchFragment.COLLECTION -> SearchCollectionFragment(query)
            SearchFragment.USER -> SearchUserFragment(query)
        }
    }

}