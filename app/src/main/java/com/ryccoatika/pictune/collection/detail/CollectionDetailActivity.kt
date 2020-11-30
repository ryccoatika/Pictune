package com.ryccoatika.pictune.collection.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.domain.model.CollectionDetail
import com.ryccoatika.core.ui.PhotoAdapter
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.core.utils.loadProfilePicture
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.photo.detail.PhotoDetailActivity
import com.ryccoatika.pictune.user.UserActivity
import kotlinx.android.synthetic.main.activity_collection_detail.*
import kotlinx.android.synthetic.main.collection_detail_content.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CollectionDetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_COLLECTION_ID = "extra_collection_id"
    }

    private val viewModel: CollectionDetailViewModel by viewModel()
    private val photoAdapter = PhotoAdapter()
    private var collection: CollectionDetail? = null

    private var currentPage = 1
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(rv_photos.layoutManager as StaggeredGridLayoutManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val collectionId = intent.getStringExtra(EXTRA_COLLECTION_ID)

        if (collectionId != null) {
            viewModel.viewState.observe(this) { state ->
                when (state) {
                    // collection
                    is CollectionDetailViewState.Error -> {
                        container_about.isVisible = false
                        view_loading.isVisible = false
                        view_error.isVisible = true
                        view_rv.isVisible = false
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    is CollectionDetailViewState.Loading -> {
                        container_about.isVisible = false
                        view_loading.isVisible = true
                        view_error.isVisible = false
                        view_rv.isVisible = false
                    }
                    is CollectionDetailViewState.Success -> {
                        container_about.isVisible = true
                        view_loading.isVisible = false
                        view_error.isVisible = false
                        view_rv.isVisible = true
                        state.data?.let {
                            this.collection = it
                            populateCollection(it)
                        }
                    }

                    // collection photos
                    is CollectionDetailViewState.GetPhotosLoading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_rv_loading.isVisible = true
                        rv_photos.isVisible = false
                        view_rv_empty.isVisible = false
                        view_rv_error.isVisible = false
                    }
                    is CollectionDetailViewState.GetPhotosEmpty -> {
                        swipe_refresh.isRefreshing = false
                        view_rv_loading.isVisible = false
                        rv_photos.isVisible = false
                        view_rv_empty.isVisible = true
                        view_rv_error.isVisible = false
                    }
                    is CollectionDetailViewState.ListPhotosError -> {
                        swipe_refresh.isRefreshing = false
                        view_rv_loading.isVisible = false
                        rv_photos.isVisible = false
                        view_rv_empty.isVisible = false
                        view_rv_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    is CollectionDetailViewState.GetPhotosSuccess -> {
                        scrollListener.isLoading = false
                        swipe_refresh.isRefreshing = false
                        view_rv_loading.isVisible = false
                        rv_photos.isVisible = true
                        view_rv_empty.isVisible = false
                        view_rv_error.isVisible = false

                        photoAdapter.setPhotos(state.data)
                        currentPage++
                    }

                    // load more collection photos
                    is CollectionDetailViewState.LoadMorePhotoLoading -> {
                        scrollListener.isLoading = true
                        photoAdapter.showLoading()
                    }
                    is CollectionDetailViewState.LoadMorePhotosEmpty -> {
                        photoAdapter.hideLoading()
                    }
                    is CollectionDetailViewState.LoadMorePhotoSuccess -> {
                        photoAdapter.hideLoading()
                        scrollListener.isLoading = false
                        currentPage++
                        photoAdapter.insertPhotos(state.data)
                    }
                }
            }

            viewModel.getCollection(collectionId)
        }
    }

    private fun populateCollection(collection: CollectionDetail) {
        supportActionBar?.title = collection.title

        user_image.loadProfilePicture(collection.user.profileImage.medium)

        tv_user_name.text = collection.user.name
        if (collection.description != "undefined")
            tv_title.text = collection.description
        else
            tv_title.isVisible = false

        tv_total_photos.text = getString(R.string.total_photos, collection.totalPhotos)

        photoAdapter.setHasStableIds(true)
        photoAdapter.setOnClickListener {
            val toDetail = Intent(this, PhotoDetailActivity::class.java)
            toDetail.putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, it.id)
            startActivity(toDetail)
        }

        with(rv_photos) {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = photoAdapter
            addOnScrollListener(scrollListener)
        }

        user_image.setOnClickListener(this)
        tv_user_name.setOnClickListener(this)

        viewModel.getCollectionPhotos(collection.id, currentPage)
        swipe_refresh.setOnRefreshListener {
            viewModel.getCollectionPhotos(collection.id)
        }
        scrollListener.setOnLoadMoreListener {
            viewModel.loadMoreCollectionPhotos(collection.id, currentPage)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.user_image,
            R.id.tv_user_name -> {
                collection?.let {
                    val intentUser = Intent(this, UserActivity::class.java)
                    intentUser.putExtra(UserActivity.EXTRA_USER_NAME, it.user.username)
                    startActivity(intentUser)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_open_link -> {
                collection?.let {
                    val link = getString(R.string.link_referral, it.links.html)
                    val openLink = Intent(Intent.ACTION_VIEW)
                    openLink.data = Uri.parse(link)
                    startActivity(Intent.createChooser(openLink, getString(R.string.open_with)))
                }
                true
            }
            R.id.menu_share -> {
                collection?.let {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.collection_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
