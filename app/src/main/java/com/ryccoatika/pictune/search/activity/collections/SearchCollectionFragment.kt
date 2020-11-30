package com.ryccoatika.pictune.search.activity.collections

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.ui.CollectionAdapter
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.collection.detail.CollectionDetailActivity
import kotlinx.android.synthetic.main.fragment_collection.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchCollectionFragment(
    private val query: String
) : Fragment() {

    private val collectionAdapter: CollectionAdapter = CollectionAdapter()
    private val viewModelSearch: SearchCollectionViewModel by viewModel()

    private var currentPage = 1
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(rv_collections.layoutManager as StaggeredGridLayoutManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                getString(R.string.home)

            // setting staggered grid layout to not blinking and change position
            collectionAdapter.setHasStableIds(true)

            collectionAdapter.setOnClickListener {
                val toDetail = Intent(requireContext(), CollectionDetailActivity::class.java)
                toDetail.putExtra(CollectionDetailActivity.EXTRA_COLLECTION_ID, it.id)
                startActivity(toDetail)
            }

            with(rv_collections) {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                addOnScrollListener(scrollListener)
                adapter = collectionAdapter
            }

            viewModelSearch.viewStateSearch.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is SearchCollectionViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_collections.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is SearchCollectionViewState.Loading -> {
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_collections.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        currentPage = 1
                    }
                    is SearchCollectionViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_collections.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is SearchCollectionViewState.Success -> {
                        swipe_refresh.isRefreshing = false
                        rv_collections.isVisible = true
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        collectionAdapter.setCollections(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                    // loadmore
                    is SearchCollectionViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        collectionAdapter.showLoading()
                    }
                    is SearchCollectionViewState.LoadMoreEmpty -> {
                        collectionAdapter.hideLoading()
                    }
                    is SearchCollectionViewState.LoadMoreSuccess -> {
                        collectionAdapter.hideLoading()
                        collectionAdapter.insertCollections(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModelSearch.loadMoreCollections(query, currentPage)
            }

            swipe_refresh.setOnRefreshListener {
                viewModelSearch.searchCollections(query)
            }
            viewModelSearch.searchCollections(query)
        }

    }
}
