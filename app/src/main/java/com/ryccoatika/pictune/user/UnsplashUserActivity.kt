package com.ryccoatika.pictune.user

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.db.UnsplashUserResponse
import kotlinx.android.synthetic.main.activity_unsplash_user.*

class UnsplashUserActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel: UnsplashUserView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UnsplashUserViewModel::class.java)
    }

    companion object {
        const val EXTRA_USER_NAME = "user_name"
    }

    private lateinit var username: String
    private var user: UnsplashUserResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsplash_user)

        setSupportActionBar(unsplash_user_toolbar)

        username = intent.getStringExtra(EXTRA_USER_NAME) ?: ""

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is UnsplashUserViewState.UserLoading -> {
                    unsplash_user_pb.visibility = View.VISIBLE
                }
                is UnsplashUserViewState.UserSuccess -> {
                    unsplash_user_pb.visibility = View.INVISIBLE
                    user = state.response
                    initData(state.response)
                }
                is UnsplashUserViewState.UserError -> {
                    unsplash_user_pb.visibility = View.INVISIBLE
                    Log.w("190401", UnsplashUserActivity::class.simpleName, state.error)
                }
            }
        })

        viewModel.unsplashGetUser(username)

        unsplash_user_btn_web.setOnClickListener(this)
        unsplash_user_btn_instagram.setOnClickListener(this)
        unsplash_user_btn_twitter.setOnClickListener(this)
    }

    private fun initData(user: UnsplashUserResponse) {
        supportActionBar?.title = ""
        unsplash_user_name.text = user.name
        unsplash_user_tv_location.text = user.location
        unsplash_user_tv_bio.text = user.bio

        unsplash_user_image_location.visibility = if (user.location.isNullOrEmpty()) View.GONE else View.VISIBLE
        unsplash_user_tv_location.visibility = if (user.location.isNullOrEmpty()) View.GONE else View.VISIBLE

        unsplash_user_btn_web.visibility = if (user.portfolio.isNullOrEmpty()) View.GONE else View.VISIBLE
        unsplash_user_btn_instagram.visibility = if (user.instagramUsername.isNullOrEmpty()) View.GONE else View.VISIBLE
        unsplash_user_btn_twitter.visibility = if (user.twitterUsername.isNullOrEmpty()) View.GONE else View.VISIBLE

        Glide.with(this)
            .load(user.profileImage?.medium)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(R.drawable.ic_broken_image_black_24dp)
            .into(unsplash_user_image)

        val total = listOf(
            user.totalPhotos ?: 0,
            user.totalLikes ?: 0,
            user.totalCollections ?: 0)

        val pagerAdapter = UnsplashUserPagerAdapter(this, total, username, supportFragmentManager)
        unsplash_user_view_pager.adapter = pagerAdapter
        unsplash_user_view_pager.offscreenPageLimit = 3
        unsplash_user_tab_layout.setupWithViewPager(unsplash_user_view_pager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.unsplash_user_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         return when (item.itemId) {
            R.id.user_menu_open_link -> {
                user?.let {
                    val openLink = Intent(Intent.ACTION_VIEW)
                    openLink.data = Uri.parse(it.links?.html ?: "")
                    startActivity(Intent.createChooser(openLink, getString(R.string.text_open_with)))
                }
                true
            }
            R.id.user_menu_share -> {
                user?.let {
                    val shareLink = Intent(Intent.ACTION_SEND)
                    shareLink.putExtra(Intent.EXTRA_TEXT, it.links?.html)
                    shareLink.type = "text/plain"
                    startActivity(Intent.createChooser(shareLink, getString(R.string.text_share_using)))
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.unsplash_user_btn_web -> {
                val openPortfolio = Intent(Intent.ACTION_VIEW)
                openPortfolio.data = Uri.parse(user?.portfolio ?: "")
                startActivity(Intent.createChooser(openPortfolio, getString(R.string.text_open_with)))
            }
            R.id.unsplash_user_btn_instagram -> {
                val openInstagram = Intent(Intent.ACTION_VIEW)
                openInstagram.data = Uri.parse("https://instagram.com/${user?.instagramUsername}")
                startActivity(Intent.createChooser(openInstagram, getString(R.string.text_open_with)))
            }
            R.id.unsplash_user_btn_twitter -> {
                val openTwitter = Intent(Intent.ACTION_VIEW)
                openTwitter.data = Uri.parse("https://twitter.com/${user?.twitterUsername}")
                startActivity(Intent.createChooser(openTwitter, getString(R.string.text_open_with)))
            }
        }
    }
}
