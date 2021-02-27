package com.ryccoatika.pictune.search.topic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.ui.PhotoAdapter
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.photo.detail.PhotoDetailActivity
import com.ryccoatika.pictune.utils.ReviewHelper
import kotlinx.android.synthetic.main.activity_topic.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopicActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TOPIC_ID = "extra_topic_id"
        const val EXTRA_TOPIC_TITLE = "extra_topic_title"
    }

    private val viewModel: TopicViewModel by viewModel()
    private val photoAdapter = PhotoAdapter()

    private var currentPage = 1
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(rv_photos.layoutManager as StaggeredGridLayoutManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        photoAdapter.setHasStableIds(true)

        photoAdapter.setOnClickListener {
            val toDetail = Intent(this, PhotoDetailActivity::class.java)
            toDetail.putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, it.id)
            startActivity(toDetail)
        }

        val topicId = intent.getStringExtra(EXTRA_TOPIC_ID)
        val topicTitle = intent.getStringExtra(EXTRA_TOPIC_TITLE)
        if (topicId != null) {
            supportActionBar?.title = topicTitle

            with(rv_photos) {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                addOnScrollListener(scrollListener)
                adapter = photoAdapter
            }

            viewModel.viewState.observe(this) { state ->
                when (state) {
                    is TopicViewState.Loading -> {
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        rv_photos.isVisible = false
                        currentPage = 1
                        scrollListener.isLoading = true
                    }
                    is TopicViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                        rv_photos.isVisible = false
                    }
                    is TopicViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        rv_photos.isVisible = false
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    is TopicViewState.Success -> {
                        swipe_refresh.isRefreshing = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        rv_photos.isVisible = true
                        photoAdapter.setPhotos(state.data)
                        currentPage++
                        scrollListener.isLoading = false
                        ReviewHelper.launchInAppReview(this)
                    }

                    // load more topic photos
                    is TopicViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        photoAdapter.showLoading()
                    }
                    is TopicViewState.LoadMoreEmpty -> {
                        photoAdapter.hideLoading()
                    }
                    is TopicViewState.LoadMoreSuccess -> {
                        photoAdapter.hideLoading()
                        scrollListener.isLoading = false
                        photoAdapter.insertPhotos(state.data)
                        currentPage++
                    }
                }
            }

            viewModel.getTopicPhotos(topicId)

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMoreTopicPhotos(topicId, currentPage)
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.getTopicPhotos(topicId)
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
