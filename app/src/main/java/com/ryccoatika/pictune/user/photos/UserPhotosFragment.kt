package com.ryccoatika.pictune.user.photos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.domain.repository.DiscoverPhotoOrder
import com.ryccoatika.core.ui.PhotoAdapter
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.photo.detail.PhotoDetailActivity
import kotlinx.android.synthetic.main.fragment_photo.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserPhotosFragment(
    private val username: String
) : Fragment() {

    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    private val viewModel: UserPhotosViewModel by viewModel()

    private var currentPage = 1
    private var orderBy = DiscoverPhotoOrder.LATEST
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(rv_photos.layoutManager as StaggeredGridLayoutManager)
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
            // setting staggered grid layout to not blinking and change position
            photoAdapter.setHasStableIds(true)

            photoAdapter.setOnClickListener {
                val toDetail = Intent(requireContext(), PhotoDetailActivity::class.java)
                toDetail.putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, it.id)
                startActivity(toDetail)
            }

            with(rv_photos) {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                addOnScrollListener(scrollListener)
                adapter = photoAdapter
            }

            viewModel.viewState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is UserPhotosViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is UserPhotosViewState.Loading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_photos.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                    }
                    is UserPhotosViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is UserPhotosViewState.Success -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = true
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        photoAdapter.setPhotos(state.data)
                        currentPage++
                        scrollListener.isLoading = false
                    }
                    // loadmore
                    is UserPhotosViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        photoAdapter.showLoading()
                    }
                    is UserPhotosViewState.LoadMoreEmpty -> {
                        photoAdapter.hideLoading()
                    }
                    is UserPhotosViewState.LoadMoreSuccess -> {
                        photoAdapter.hideLoading()
                        photoAdapter.insertPhotos(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMorePhoto(
                    username = username,
                    page = currentPage,
                    orderBy = orderBy.value
                )
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.getUserPhotos(
                    username = username,
                    orderBy = orderBy.value
                )
            }

            viewModel.getUserPhotos(
                username = username,
                orderBy = orderBy.value
            )
        }
    }
}