package com.ryccoatika.pictune.search.activity.photos

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.ui.PhotoAdapter
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.photo.detail.PhotoDetailActivity
import com.ryccoatika.pictune.search.activity.SearchFilterDialog
import com.ryccoatika.pictune.utils.ReviewHelper
import kotlinx.android.synthetic.main.fragment_photo.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchPhotoFragment(
    private val query: String
) : Fragment() {

    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    private val viewModel: SearchPhotoViewModel by viewModel()

    private var currentPage = 1
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(rv_photos.layoutManager as StaggeredGridLayoutManager)
    }
    private val filterDialog: SearchFilterDialog by lazy {
        SearchFilterDialog(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            setHasOptionsMenu(true)
            // setting staggered grid layout to not blinking and change position

            photoAdapter.setOnClickListener {
                val toDetail = Intent(requireContext(), PhotoDetailActivity::class.java)
                toDetail.putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, it.id)
                startActivity(toDetail)
            }

            photoAdapter.setHasStableIds(true)

            with(rv_photos) {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                addOnScrollListener(scrollListener)
                adapter = photoAdapter
            }

            viewModel.viewState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is SearchPhotoViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is SearchPhotoViewState.Loading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_photos.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                    }
                    is SearchPhotoViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is SearchPhotoViewState.Success -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = true
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        photoAdapter.setPhotos(state.data)
                        currentPage++
                        scrollListener.isLoading = false
                        ReviewHelper.launchInAppReview(requireActivity())
                    }
                    // loadmore
                    is SearchPhotoViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        photoAdapter.showLoading()
                    }
                    is SearchPhotoViewState.LoadMoreEmpty -> {
                        photoAdapter.hideLoading()
                    }
                    is SearchPhotoViewState.LoadMoreSuccess -> {
                        photoAdapter.hideLoading()
                        photoAdapter.insertPhotos(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                }
            }

            filterDialog.setOnDismissListener {
                with(filterDialog) {
                    if (isApplied) {
                        viewModel.searchPhoto(
                            query = query,
                            orderBy = orderBy,
                            color = color,
                            orientation = orientation
                        )
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMorePhoto(
                    query = query,
                    page = currentPage,
                    orderBy = filterDialog.orderBy,
                    color = filterDialog.color,
                    orientation = filterDialog.orientation
                )
            }

            swipe_refresh.setOnRefreshListener {
                performSearch()
            }
            performSearch()
        }
    }

    private fun performSearch() {
        filterDialog.resetFilter()
        viewModel.searchPhoto(
            query = query,
            orderBy = filterDialog.orderBy,
            color = filterDialog.color,
            orientation = filterDialog.orientation
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_filter -> {
                filterDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        (activity as AppCompatActivity).menuInflater.inflate(R.menu.search_filter, menu)
        super.onPrepareOptionsMenu(menu)
    }
}