package com.ryccoatika.pictune.user.likes

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
import com.ryccoatika.core.ui.PhotoAdapter
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.photo.detail.PhotoDetailActivity
import kotlinx.android.synthetic.main.fragment_photo.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserLikesFragment(
    private val username: String
) : Fragment() {

    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    private val viewModel: UserLikesViewModel by viewModel()

    private var currentPage = 1
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
                    is UserLikesViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is UserLikesViewState.Loading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_photos.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                    }
                    is UserLikesViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is UserLikesViewState.Success -> {
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
                    is UserLikesViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        photoAdapter.showLoading()
                    }
                    is UserLikesViewState.LoadMoreEmpty -> {
                        photoAdapter.hideLoading()
                    }
                    is UserLikesViewState.LoadMoreSuccess -> {
                        photoAdapter.hideLoading()
                        photoAdapter.insertPhotos(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMoreLikes(
                    username = username,
                    page = currentPage
                )
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.getUserLikes(
                    username = username
                )
            }
            viewModel.getUserLikes(
                username = username
            )
        }
    }
}