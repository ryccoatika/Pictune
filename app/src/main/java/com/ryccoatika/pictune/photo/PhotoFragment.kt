package com.ryccoatika.pictune.photo

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
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

class PhotoFragment : Fragment() {

    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    private val viewModel: PhotoViewModel by viewModel()

    private var currentPage = 1
    private var orderBy = DiscoverPhotoOrder.LATEST
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(
            rv_photos.layoutManager as StaggeredGridLayoutManager
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false)
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
                    is PhotoViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is PhotoViewState.Loading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_photos.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                    }
                    is PhotoViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is PhotoViewState.Success -> {
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
                    is PhotoViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        photoAdapter.showLoading()
                    }
                    is PhotoViewState.LoadMoreEmpty -> {
                        photoAdapter.hideLoading()
                    }
                    is PhotoViewState.LoadMoreSuccess -> {
                        photoAdapter.hideLoading()
                        photoAdapter.insertPhotos(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMorePhoto(orderBy.value, currentPage)
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.discoverPhoto(orderBy.value)
            }
            viewModel.discoverPhoto(orderBy.value)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filter_latest -> {
                orderBy = DiscoverPhotoOrder.LATEST
                viewModel.discoverPhoto(orderBy.value)
                true
            }
            R.id.filter_popular -> {
                orderBy = DiscoverPhotoOrder.POPULAR
                viewModel.discoverPhoto(orderBy.value)
                true
            }
            R.id.filter_oldest -> {
                orderBy = DiscoverPhotoOrder.OLDEST
                viewModel.discoverPhoto(orderBy.value)
                true
            }
            else -> false
        }
    }

    override fun onDestroyView() {
        rv_photos.clearOnScrollListeners()
        scrollListener.layoutManager = null
        rv_photos.adapter = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.photo_filter, menu)
    }
}
