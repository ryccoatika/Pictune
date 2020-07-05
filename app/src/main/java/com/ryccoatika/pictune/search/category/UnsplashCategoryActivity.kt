package com.ryccoatika.pictune.search.category

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.adapter.UnsplashPhotoGridAdapter
import kotlinx.android.synthetic.main.activity_unsplash_category.*

class UnsplashCategoryActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
    }

    private val viewModel: UnsplashCategoryView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UnsplashCategoryViewModel::class.java)
    }
    private val photosAdapter = UnsplashPhotoGridAdapter()
    private var category: String? = null

    private var currentPage = 1
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsplash_category)

        category = intent.getStringExtra(EXTRA_CATEGORY)
        if (category == null) {
            finish()
            return
        }

        setSupportActionBar(unsplash_category_toolbar)
        supportActionBar?.title = category

        photosAdapter.setHasStableIds(true)
        unsplash_category_rv.setHasFixedSize(true)
        unsplash_category_rv.adapter = photosAdapter

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is UnsplashCategoryViewState.Loading -> {
                    unsplash_category_pb.visibility = View.VISIBLE
                }
                is UnsplashCategoryViewState.LoadMoreLoading -> {
                    Log.d("190401", currentPage.toString())
                    photosAdapter.showLoading()
                }
                is UnsplashCategoryViewState.Success -> {
                    unsplash_category_pb.visibility = View.INVISIBLE
                    photosAdapter.photos = state.response.results?.toMutableList() ?: mutableListOf()
                    if (!state.response.results.isNullOrEmpty()) {
                        currentPage++
                        scrollListener = RecycleViewLoadMoreScroll(
                            unsplash_category_rv.layoutManager as StaggeredGridLayoutManager
                        )
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMore(category ?: "", currentPage)
                        }
                        scrollListener?.let { unsplash_category_rv.addOnScrollListener(it)}
                    }
                }
                is UnsplashCategoryViewState.Error -> {
                    unsplash_category_pb.visibility = View.INVISIBLE
                    Log.d("190401", UnsplashCategoryActivity::class.simpleName, state.error)
                }
                is UnsplashCategoryViewState.LoadMoreSuccess -> {
                    photosAdapter.hideLoading()
                    if (!state.response.results.isNullOrEmpty()) {
                        scrollListener?.isLoading = false
                        photosAdapter.loadMore(state.response.results.toMutableList())
                        currentPage++
                    }
                }
                is UnsplashCategoryViewState.LoadMoreError -> {
                    scrollListener?.isLoading = false
                    photosAdapter.hideLoading()
                }
            }
        })

        category?.let {
            viewModel.searchCategory(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
