package com.ryccoatika.pictune.discoverCollections

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.adapter.UnsplashCollectionGridAdapter
import kotlinx.android.synthetic.main.fragment_unsplash_discover_collections.*

class UnsplashDiscoverCollectionsFragment : Fragment() {

    private var currentPage = 1
    private var filter = UnsplashDiscoverCollectionsViewModel.COLLECTION_ALL
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    private val collectionsAdapter: UnsplashCollectionGridAdapter by lazy {
        UnsplashCollectionGridAdapter()
    }
    private val viewModel: UnsplashDiscoverCollectionsView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(UnsplashDiscoverCollectionsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unsplash_discover_collections, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        unsplash_discover_collections_rv.setHasFixedSize(true)
        unsplash_discover_collections_rv.adapter = collectionsAdapter

        (unsplash_discover_collections_rv.layoutManager as GridLayoutManager)
            .spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (collectionsAdapter.getItemViewType(position)) {
                    UnsplashCollectionGridAdapter.Type.Data.ordinal -> 1
                    UnsplashCollectionGridAdapter.Type.Loading.ordinal -> 3
                    else -> 1
                }
            }
        }

        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UnsplashDiscoverCollectionsViewState.DiscoverLoading -> {
                    showWarning(false)
                    if (!unsplash_discover_collections_swipe_refresh.isRefreshing)
                        unsplash_discover_collections_pb.visibility = View.VISIBLE
                }
                is UnsplashDiscoverCollectionsViewState.LoadMoreLoading -> {
                    collectionsAdapter.showLoading()
                }
                is UnsplashDiscoverCollectionsViewState.DiscoverSuccess -> {
                    showWarning(state.responses.isEmpty())
                    unsplash_discover_collections_swipe_refresh.isRefreshing = false
                    unsplash_discover_collections_pb.visibility = View.INVISIBLE
                    collectionsAdapter.collections = state.responses.toMutableList()
                    if (state.responses.isNotEmpty()) {
                        currentPage++
                        scrollListener = RecycleViewLoadMoreScroll(
                            unsplash_discover_collections_rv.layoutManager as GridLayoutManager
                        )
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMoreCollections(currentPage, filter)
                        }
                        scrollListener?.let {
                            unsplash_discover_collections_rv.addOnScrollListener(it)
                        }
                    }
                }
                is UnsplashDiscoverCollectionsViewState.LoadMoreSuccess -> {
                    collectionsAdapter.hideLoading()
                    if (state.responses.isNotEmpty()) {
                        collectionsAdapter.loadMore(state.responses.toMutableList())
                        scrollListener?.isLoading = false
                        currentPage++
                    }
                }
                is UnsplashDiscoverCollectionsViewState.DiscoverError -> {
                    showWarning(true)
                    unsplash_discover_collections_swipe_refresh.isRefreshing = false
                    unsplash_discover_collections_pb.visibility = View.INVISIBLE
                    unsplash_discover_collection_tv_warning.text = getString(R.string.text_failed_getting_data_try_refresh)
                    Log.w("190401", UnsplashDiscoverCollectionsFragment::class.simpleName, state.error)
                }
                is UnsplashDiscoverCollectionsViewState.LoadMoreError -> {
                    scrollListener?.isLoading = false
                    collectionsAdapter.hideLoading()
                }
            }
        })

        unsplash_discover_collections_swipe_refresh.setOnRefreshListener {
                viewModel.discoverCollections(filter)
        }

        viewModel.discoverCollections(filter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.collection_all -> {
                filter = UnsplashDiscoverCollectionsViewModel.COLLECTION_ALL
                viewModel.discoverCollections(filter)
                true
            }
            R.id.collection_featured -> {
                filter = UnsplashDiscoverCollectionsViewModel.COLLECTION_FEATURED
                viewModel.discoverCollections(filter)
                true
            }
            else -> false
        }
    }

    private fun showWarning(state: Boolean) {
        if (state) {
            unsplash_discover_collections_rv.visibility = View.INVISIBLE
            unsplash_discover_collection_tv_warning.visibility = View.VISIBLE
            unsplash_discover_collection_tv_warning.text = getString(R.string.text_no_datas_found)
        } else {
            unsplash_discover_collections_rv.visibility = View.VISIBLE
            unsplash_discover_collection_tv_warning.visibility = View.INVISIBLE
        }
    }

}
