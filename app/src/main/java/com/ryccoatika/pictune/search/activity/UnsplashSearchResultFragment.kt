package com.ryccoatika.pictune.search.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.*

import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.adapter.UnsplashCollectionGridAdapter
import com.ryccoatika.pictune.adapter.UnsplashPhotoGridAdapter
import com.ryccoatika.pictune.adapter.UnsplashUserListAdapter
import kotlinx.android.synthetic.main.fragment_unsplash_search_result.*

class UnsplashSearchResultFragment : Fragment() {

    companion object {
        const val EXTRA_QUERY = "extra_query"
        const val EXTRA_MODE = "extra_mode"

        // only for photos mode
        const val EXTRA_ORDER_BY = "extra_order_by"
        const val EXTRA_CONTENT_FILTER = "extra_content_filter"
        const val EXTRA_COLOR = "extra_color"
        const val EXTRA_ORIENTATION = "extra_orientation"

        const val MODE_PHOTOS = "photos"
        const val MODE_COLLECTIONS = "collections"
        const val MODE_USERS = "users"

        fun newInstance(query: String,
                        mode: String,
                        orderBy: String? = null,
                        contentFilter: String? = null,
                        color: String? = null,
                        orientation: String? = null): Fragment {
            val bundle = Bundle()
            bundle.putString(EXTRA_QUERY, query)
            bundle.putString(EXTRA_MODE, mode)
            bundle.putString(EXTRA_ORDER_BY, orderBy)
            bundle.putString(EXTRA_CONTENT_FILTER, contentFilter)
            bundle.putString(EXTRA_COLOR, color)
            bundle.putString(EXTRA_ORIENTATION, orientation)
            return UnsplashSearchResultFragment().apply { arguments = bundle }
        }
    }

    fun updateFilter(
        orderBy: String? = null,
        contentFilter: String? = null,
        color: String? = null,
        orientation: String? = null
    ) {
        if (mode == MODE_PHOTOS)
            viewModel.searchPhotos(
                query,
                orderBy = orderBy,
                contentFilter = contentFilter,
                color = color,
                orientation = orientation
            )
    }

    private lateinit var mode: String
    private lateinit var query: String
    private var orderBy: String? = null
    private var contentFilter: String? = null
    private var color: String? = null
    private var orientation: String? = null

    private var currentPage = 1
    private var scrollListener: RecycleViewLoadMoreScroll? = null

    private val photosAdapter: UnsplashPhotoGridAdapter by lazy {
        UnsplashPhotoGridAdapter()
    }

    private val collectionAdapter: UnsplashCollectionGridAdapter by lazy {
        UnsplashCollectionGridAdapter()
    }

    private val usersAdapter: UnsplashUserListAdapter by lazy {
        UnsplashUserListAdapter()
    }

    private val viewModel: UnsplashSearchResultView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())
            .get(UnsplashSearchResultViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unsplash_search_result, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        query = arguments?.getString(EXTRA_QUERY, "") ?: ""
        mode = arguments?.getString(EXTRA_MODE, "") ?: ""

        unsplash_search_result_rv.setHasFixedSize(true)

        when (mode) {
            MODE_PHOTOS -> {
                val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                scrollListener = RecycleViewLoadMoreScroll(layoutManager)

                layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
                unsplash_search_result_rv.layoutManager = layoutManager

                photosAdapter.setHasStableIds(true)
                unsplash_search_result_rv.adapter = photosAdapter
                orderBy = arguments?.getString(EXTRA_ORDER_BY)
                contentFilter = arguments?.getString(EXTRA_CONTENT_FILTER)
                color = arguments?.getString(EXTRA_COLOR)
                orientation = arguments?.getString(EXTRA_ORIENTATION)
                viewModel.searchPhotos(
                    query,
                    orderBy = orderBy,
                    contentFilter = contentFilter,
                    color = color,
                    orientation = orientation
                )
            }
            MODE_COLLECTIONS -> {
                val layoutManager = GridLayoutManager(requireContext(), 3)
                scrollListener = RecycleViewLoadMoreScroll(layoutManager)

                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (collectionAdapter.getItemViewType(position)) {
                            UnsplashCollectionGridAdapter.Type.Data.ordinal -> 1
                            UnsplashCollectionGridAdapter.Type.Loading.ordinal -> 3
                            else -> 1
                        }
                    }
                }

                unsplash_search_result_rv.layoutManager = layoutManager
                unsplash_search_result_rv.adapter = collectionAdapter
                viewModel.searchCollections(query)
            }
            MODE_USERS -> {
                val layoutManager = LinearLayoutManager(requireContext())
                scrollListener = RecycleViewLoadMoreScroll(layoutManager)
                unsplash_search_result_rv.layoutManager = layoutManager
                unsplash_search_result_rv.adapter = usersAdapter
                unsplash_search_result_rv
                    .addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                viewModel.searchUsers(query)
            }
            else -> {
                return
            }
        }

        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is UnsplashSearchResultViewState.Loading -> {
                    showWarning(false)
                    unsplash_search_result_pb.visibility = View.VISIBLE
                }
                is UnsplashSearchResultViewState.LoadMoreLoading -> {
                    when (mode) {
                        MODE_PHOTOS -> photosAdapter.showLoading()
                        MODE_COLLECTIONS -> collectionAdapter.showLoading()
                        MODE_USERS -> usersAdapter.showLoading()
                    }
                }
                is UnsplashSearchResultViewState.PhotosSuccess -> {
                    showWarning(state.response.results?.isEmpty() == true)
                    unsplash_search_result_pb.visibility = View.GONE
                    photosAdapter.photos = state.response.results?.toMutableList() ?: mutableListOf()
                    if (!state.response.results.isNullOrEmpty()) {
                        currentPage++
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMoreSearchPhotos(
                                query = query,
                                page = currentPage,
                                orderBy = orderBy,
                                contentFilter = contentFilter,
                                color = color,
                                orientation = orientation
                            )
                        }
                    }
                }
                is UnsplashSearchResultViewState.CollectionsSuccess -> {
                    showWarning(state.response.results?.isEmpty() == true)
                    unsplash_search_result_pb.visibility = View.GONE
                    collectionAdapter.collections = state.response.results?.toMutableList() ?: mutableListOf()
                    if (!state.response.results.isNullOrEmpty()) {
                        currentPage++
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMoreSearchCollections(query, currentPage)
                        }
                    }
                }
                is UnsplashSearchResultViewState.UsersSuccess -> {
                    showWarning(state.response.results?.isEmpty() == true)
                    unsplash_search_result_pb.visibility = View.GONE
                    usersAdapter.users = state.response.results?.toMutableList() ?: mutableListOf()
                    if (!state.response.results.isNullOrEmpty()) {
                        currentPage++
                        scrollListener?.setOnLoadMoreListener {
                            viewModel.loadMoreSearchUsers(query, currentPage)
                        }
                    }
                }
                is UnsplashSearchResultViewState.LoadMorePhotosSuccess -> {
                    photosAdapter.hideLoading()
                    if (!state.response.results.isNullOrEmpty()) {
                        scrollListener?.isLoading = false
                        photosAdapter.loadMore(state.response.results.toMutableList())
                        currentPage++
                    }
                }
                is UnsplashSearchResultViewState.LoadMoreCollectionsSuccess -> {
                    collectionAdapter.hideLoading()
                    if (!state.response.results.isNullOrEmpty()) {
                        scrollListener?.isLoading = false
                        collectionAdapter.loadMore(state.response.results.toMutableList())
                        currentPage++
                    }
                }
                is UnsplashSearchResultViewState.LoadMoreUsersSuccess -> {
                    usersAdapter.hideLoading()
                    if (!state.response.results.isNullOrEmpty()) {
                        scrollListener?.isLoading = false
                        usersAdapter.loadMore(state.response.results.toMutableList())
                        currentPage++
                    }
                }
                is UnsplashSearchResultViewState.Error -> {
                    showWarning(true)
                    unsplash_search_result_pb.visibility = View.GONE
                    unsplash_search_result_tv_warning.text = getString(R.string.text_failed_getting_data)
                    Log.d("190401", UnsplashSearchResultFragment::class.simpleName, state.error)
                }
                is UnsplashSearchResultViewState.LoadMoreError -> {
                    when (mode) {
                        MODE_PHOTOS -> {
                            scrollListener?.isLoading = false
                            photosAdapter.hideLoading()
                        }
                        MODE_COLLECTIONS -> {
                            scrollListener?.isLoading = false
                            collectionAdapter.hideLoading()
                        }
                        MODE_USERS -> {
                            scrollListener?.isLoading = false
                            usersAdapter.hideLoading()
                        }
                    }
                }
            }
        })

        scrollListener?.let { unsplash_search_result_rv.addOnScrollListener(it) }
    }

    private fun showWarning(state: Boolean) {
        if (state) {
            unsplash_search_result_rv.visibility = View.GONE
            unsplash_search_result_tv_warning.visibility = View.VISIBLE
            unsplash_search_result_tv_warning.text = getString(R.string.text_no_datas_found)
        } else {
            unsplash_search_result_rv.visibility = View.VISIBLE
            unsplash_search_result_tv_warning.visibility = View.GONE
        }
    }
}
