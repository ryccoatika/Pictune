package com.ryccoatika.pictune.user

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.ryccoatika.core.domain.model.UserDetail
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.user.collections.UserCollectionsFragment
import com.ryccoatika.pictune.user.likes.UserLikesFragment
import com.ryccoatika.pictune.user.photos.UserPhotosFragment

class UserPagerAdapter(
    private val context: Context,
    fm: FragmentManager,
    private val user: UserDetail
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentTypes = mutableListOf<UserFragment>()

    enum class UserFragment(val titleRes: Int) {
        PHOTO(R.string.tab_user_photos),
        LIKE(R.string.tab_user_likes),
        COLLECTION(R.string.tab_user_collections)
    }

    init {
        if (user.totalPhotos != 0) fragmentTypes.add(UserFragment.PHOTO)
        if (user.totalLikes != 0) fragmentTypes.add(UserFragment.LIKE)
        if (user.totalCollection != 0) fragmentTypes.add(UserFragment.COLLECTION)
    }

    override fun getCount(): Int = fragmentTypes.size

    override fun getPageTitle(position: Int): CharSequence? =
        context.getString(fragmentTypes[position].titleRes)

    override fun getItem(position: Int): Fragment {
        return when (fragmentTypes[position]) {
            UserFragment.PHOTO -> UserPhotosFragment(user.username)
            UserFragment.LIKE -> UserLikesFragment(user.username)
            UserFragment.COLLECTION -> UserCollectionsFragment(user.username)
        }
    }

}