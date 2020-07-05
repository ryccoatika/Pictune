package com.ryccoatika.pictune.collection

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.adapter.UnsplashCollectionGridAdapter
import com.ryccoatika.pictune.adapter.UnsplashPhotoGridAdapter
import com.ryccoatika.pictune.db.UnsplashCollectionResponse
import com.ryccoatika.pictune.user.UnsplashUserActivity
import kotlinx.android.synthetic.main.activity_unsplash_collection.*

class UnsplashCollectionActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_COLLECTION = "extra_collection"
    }

    // state for related collections expand
    private var isExpanded = true

    private var currentPage = 1
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    private var unsplashCollections: UnsplashCollectionResponse? = null
    private val viewModel: UnsplashCollectionView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UnsplashCollectionViewModel::class.java)
    }
    private val relatedAdapter = UnsplashCollectionGridAdapter()
    private val photosAdapter = UnsplashPhotoGridAdapter()

    private var collection: UnsplashCollectionResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsplash_collection)

        setSupportActionBar(unsplash_collection_toolbar)

        unsplashCollections = intent.getParcelableExtra(EXTRA_COLLECTION)

        if (unsplashCollections == null) {
            finish()
            return
        }

        unsplashCollections?.let {
            this.collection = it
            initData(it)
        }

        unsplash_collection_rv_related.adapter = relatedAdapter

        unsplash_collection_rv_photos.setHasFixedSize(true)
        photosAdapter.setHasStableIds(true)
        unsplash_collection_rv_photos.adapter = photosAdapter

        (unsplash_collection_rv_photos.layoutManager as StaggeredGridLayoutManager)
            .gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is UnsplashCollectionViewState.RelatedCollectionsLoading -> {
                    showRelated(false)
                }
                is UnsplashCollectionViewState.PhotosLoading -> {
                    showWarning(false)
                    if (!unsplash_collection_swipe_refresh.isRefreshing)
                        unsplash_collection_pb_photos.visibility = View.VISIBLE
                }
                is UnsplashCollectionViewState.LoadMorePhotosLoading -> {
                    photosAdapter.showLoading()
                }
                is UnsplashCollectionViewState.RelatedCollectionsOnSuccess -> {
                    showRelated(false)
                    relatedAdapter.collections = state.responses.toMutableList()
                }
                is UnsplashCollectionViewState.RelatedCollectionsOnError -> {
                    Log.w("190401", UnsplashCollectionActivity::class.simpleName, state.error)
                }
                is UnsplashCollectionViewState.PhotosOnSuccess -> {
                    showWarning(state.responses.isEmpty())
                    unsplash_collection_pb_photos.visibility = View.INVISIBLE
                    unsplash_collection_swipe_refresh.isRefreshing = false
                    photosAdapter.photos = state.responses.toMutableList()
                    if (state.responses.isNotEmpty()) {
                        currentPage++
                        scrollListener = RecycleViewLoadMoreScroll(
                            unsplash_collection_rv_photos.layoutManager as StaggeredGridLayoutManager
                        )
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMorePhotos(collection?.id ?: 0, currentPage)
                        }
                        scrollListener?.let {
                            unsplash_collection_rv_photos.addOnScrollListener(it)
                        }
                    }
                }
                is UnsplashCollectionViewState.PhotosOnError -> {
                    showWarning(true)
                    unsplash_collection_tv_warning.text = getString(R.string.text_failed_getting_data_try_refresh)
                    unsplash_collection_pb_photos.visibility = View.INVISIBLE
                    unsplash_collection_swipe_refresh.isRefreshing = false
                    Log.w("190401", UnsplashCollectionActivity::class.simpleName, state.error)
                }
                is UnsplashCollectionViewState.LoadMorePhotosSuccess -> {
                    photosAdapter.hideLoading()
                    if (state.responses.isNotEmpty()) {
                        scrollListener?.isLoading = false
                        photosAdapter.loadMore(state.responses.toMutableList())
                        currentPage++
                    }

                }
                is UnsplashCollectionViewState.LoadMorePhotosError -> {
                    photosAdapter.hideLoading()
                }
            }
        })

        unsplash_collection_iv_expand_more.setOnClickListener(this)
        unsplash_collection_image.setOnClickListener(this)
        unsplash_collection_tv_name.setOnClickListener(this)
    }

    private fun initData(collection: UnsplashCollectionResponse) {
        supportActionBar?.title = collection.title

        Glide.with(this)
            .load(collection.user?.profileImage?.medium)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .error(R.drawable.ic_broken_image_black_24dp)
            .into(unsplash_collection_image)

        unsplash_collection_tv_description.text = collection.description
        unsplash_collection_tv_description.visibility = if (collection.description.isNullOrEmpty()) View.GONE else View.VISIBLE
        unsplash_collection_tv_name.text = collection.user?.name

        // load related collections
        viewModel.getRelatedCollections(collection.id ?: 0)
        // load collection's photos
        viewModel.getPhotosOfCollections(collection.id ?: 0)

        unsplash_collection_swipe_refresh.setOnRefreshListener {
            viewModel.getPhotosOfCollections(collection.id ?: 0)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.unsplash_collection_iv_expand_more -> {
                showRelated(isExpanded)
            }
            R.id.unsplash_collection_image,
            R.id.unsplash_collection_tv_name -> {
                val intentUser = Intent(this, UnsplashUserActivity::class.java)
                intentUser.putExtra(UnsplashUserActivity.EXTRA_USER_NAME, collection?.user?.username)
                Log.d("190401", collection?.user?.username)
                startActivity(intentUser)
            }
        }
    }

    private fun showRelated(state: Boolean) {
        isExpanded = if (state) {
            unsplash_collection_rv_related.visibility = View.VISIBLE
            unsplash_collection_iv_expand_more.rotation = 180f
            false
        } else {
            unsplash_collection_iv_expand_more.rotation = 0f
            unsplash_collection_rv_related.visibility = View.GONE
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.collection_menu_open_link -> {
                val openLink = Intent(Intent.ACTION_VIEW)
                openLink.data = Uri.parse(unsplashCollections?.links?.html ?: "")
                startActivity(Intent.createChooser(openLink, getString(R.string.text_open_with)))
                true
            }
            R.id.collection_menu_share -> {
                val shareLink = Intent(Intent.ACTION_SEND)
                shareLink.putExtra(Intent.EXTRA_TEXT, unsplashCollections?.links?.html ?: "")
                shareLink.type = "text/plain"
                startActivity(Intent.createChooser(shareLink, getString(R.string.text_share_using)))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.unsplash_collection_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun showWarning(state: Boolean) {
        if (state) {
            unsplash_collection_rv_photos.visibility = View.INVISIBLE
            unsplash_collection_tv_warning.visibility = View.VISIBLE
            unsplash_collection_tv_warning.text = getString(R.string.text_no_datas_found)
        } else {
            unsplash_collection_rv_photos.visibility = View.VISIBLE
            unsplash_collection_tv_warning.visibility = View.INVISIBLE
        }
    }
}
