package com.ryccoatika.pictune.discoverCollections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.ryccoatika.core.ui.CollectionAdapter
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.fragment_discover_collections.*
import kotlinx.android.synthetic.main.view_error.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DiscoverCollectionFragment : Fragment() {

    private val collectionAdapter: CollectionAdapter = CollectionAdapter()
    private val viewModel: DiscoverCollectionsViewModel by viewModel()

    private var currentPage = 1
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discover_collections, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        // setting staggered grid layout to not blinking and change position
        collectionAdapter.setHasStableIds(true)

        with(rv_collections) {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = collectionAdapter
        }

        viewModel.viewState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is DiscoverCollectionViewState.Error -> {
                    swipe_refresh.isRefreshing = false
                    rv_collections.isVisible = false
                    view_loading.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = true
                    tv_error.text = result.message ?: getString(R.string.something_went_wrong)
                    scrollListener?.isLoading = false
                    collectionAdapter.hideLoading()
                }
                // discover
                is DiscoverCollectionViewState.DiscoverLoading -> {
                    if (!swipe_refresh.isRefreshing)
                        view_loading.isVisible = true
                    rv_collections.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = false
                    currentPage = 1
                }
                is DiscoverCollectionViewState.DiscoverEmpty -> {
                    swipe_refresh.isRefreshing = false
                    rv_collections.isVisible = false
                    view_loading.isVisible = false
                    view_empty.isVisible = true
                    view_error.isVisible = false
                }
                is DiscoverCollectionViewState.DiscoverSuccess -> {
                    swipe_refresh.isRefreshing = false
                    rv_collections.isVisible = true
                    view_loading.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = false
                    collectionAdapter.setCollections(result.data)
                    currentPage++
                    scrollListener = RecycleViewLoadMoreScroll(
                        rv_collections.layoutManager as StaggeredGridLayoutManager
                    )
                    scrollListener?.let {
                        it.setOnLoadMoreListener {
                            viewModel.loadMoreCollections(currentPage)
                        }
                        rv_collections.addOnScrollListener(it)
                    }
                }
                // loadmore
                is DiscoverCollectionViewState.LoadMoreLoading -> {
                    collectionAdapter.showLoading()
                }
                is DiscoverCollectionViewState.LoadMoreEmpty -> {

                }
                is DiscoverCollectionViewState.LoadMoreSuccess -> {
                    collectionAdapter.hideLoading()
                    collectionAdapter.insertCollections(result.data)
                    scrollListener?.isLoading = false
                    currentPage++
                }
            }
        }

        swipe_refresh.setOnRefreshListener {
            viewModel.discoverCollections(currentPage)
        }
        viewModel.discoverCollections(currentPage)
    }
}
