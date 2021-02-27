package com.ryccoatika.pictune.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.domain.model.UserDetail
import com.ryccoatika.core.utils.loadProfilePicture
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.utils.ReviewHelper
import kotlinx.android.synthetic.main.activity_user.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModel()
    private var user: UserDetail? = null

    companion object {
        const val EXTRA_USER_NAME = "user_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val username = intent.getStringExtra(EXTRA_USER_NAME)

        if (username != null) {
            viewModel.viewState.observe(this) { state ->
                when (state) {
                    is UserViewState.Loading -> {
                        container_about.isVisible = false
                        nested_scroll_view.isVisible = false
                        view_loading.isVisible = true
                        view_error.isVisible = false
                        nested_scroll_view.isVisible = false
                    }
                    is UserViewState.Error -> {
                        container_about.isVisible = false
                        nested_scroll_view.isVisible = false
                        view_loading.isVisible = false
                        view_error.isVisible = true
                        nested_scroll_view.isVisible = false
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    is UserViewState.Success -> {
                        container_about.isVisible = true
                        nested_scroll_view.isVisible = true
                        view_loading.isVisible = false
                        view_error.isVisible = false
                        nested_scroll_view.isVisible = true
                        state.data?.let {
                            this.user = it
                            populateUser(it)
                        }
                    }
                }
            }

            viewModel.getUser(username)
        }
    }

    private fun populateUser(user: UserDetail) {
        user_image.loadProfilePicture(user.profileImage.medium)
        supportActionBar?.title = user.username

        tv_user_name.text = user.name
        if (user.bio != "undefined")
            tv_bio.text = user.bio
        else
            tv_bio.isVisible = false
        if (user.location != "undefined") {
            tv_location.text = user.location
            tv_location.setOnClickListener {
                val toMaps = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${user.location}"))
                startActivity(Intent.createChooser(toMaps, getString(R.string.open_with)))
            }
        } else {
            image_location.isVisible = false
            tv_location.isVisible = false
        }

        if (user.portfolioUrl != "undefined") {
            tv_portfolio.text = user.portfolioUrl
            tv_portfolio.setOnClickListener {
                val toWeb = Intent(Intent.ACTION_VIEW, Uri.parse(user.portfolioUrl))
                startActivity(Intent.createChooser(toWeb, getString(R.string.open_with)))
            }
        } else {
            image_portfolio.isVisible = false
            tv_portfolio.isVisible = false
        }

        tv_total_photos.text = user.totalPhotos.toString()
        tv_total_likes.text = user.totalLikes.toString()
        tv_total_collection.text = user.totalCollection.toString()

        val pagerAdapter = UserPagerAdapter(
            this,
            supportFragmentManager,
            user
        )
        view_pager.adapter = pagerAdapter
        tab_layout.setupWithViewPager(view_pager)

        ReviewHelper.launchInAppReview(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_open_link -> {
                user?.let {
                    val link = getString(R.string.link_referral, it.links.html)
                    val openLink = Intent(Intent.ACTION_VIEW)
                    openLink.data = Uri.parse(link)
                    startActivity(Intent.createChooser(openLink, getString(R.string.open_with)))
                }
                true
            }
            R.id.menu_share -> {
                user?.let {
                    val shareLink = Intent(Intent.ACTION_SEND)
                    shareLink.putExtra(Intent.EXTRA_TEXT, it.links.html)
                    shareLink.type = "text/plain"
                    startActivity(Intent.createChooser(shareLink, getString(R.string.share_using)))
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
