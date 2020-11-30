package com.ryccoatika.pictune.collection

import android.content.Intent
import android.os.Bundle
import android.view.*
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

class CollectionFragment : Fragment() {

    private val collectionAdapter: CollectionAdapter = CollectionAdapter()
    private val viewModel: CollectionViewModel by viewModel()

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

    override fun onResume() {
        super.onResume()
        swipe_refresh.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        swipe_refresh.isEnabled = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            setHasOptionsMenu(true)
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

            viewModel.viewState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is CollectionViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_collections.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is CollectionViewState.Loading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_collections.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                    }
                    is CollectionViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_collections.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is CollectionViewState.Success -> {
                        swipe_refresh.isRefreshing = false
                        rv_collections.isVisible = true
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        collectionAdapter.setCollections(state.data)
                        currentPage++
                        scrollListener.isLoading = false
                    }
                    // loadmore
                    is CollectionViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        collectionAdapter.showLoading()
                    }
                    is CollectionViewState.LoadMoreEmpty -> {
                        collectionAdapter.hideLoading()
                    }
                    is CollectionViewState.LoadMoreSuccess -> {
                        collectionAdapter.hideLoading()
                        collectionAdapter.insertCollections(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMoreCollections(currentPage)
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.discoverCollections()
            }
            viewModel.discoverCollections()
        }

    }

//    override fun onDestroyView() {
//        rv_collections.clearOnScrollListeners()
//        scrollListener.layoutManager = null
//        rv_collections.adapter = null
//        super.onDestroyView()
//    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }
}
