package com.ryccoatika.pictune.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.ui.PhotoAdapter
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.photo.detail.PhotoDetailActivity
import kotlinx.android.synthetic.main.fragment_photo.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private val photoAdapter: PhotoAdapter = PhotoAdapter()
    private val viewModel: FavoriteViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            (requireActivity() as AppCompatActivity).supportActionBar?.hide()

            photoAdapter.setHasStableIds(true)

            photoAdapter.setOnClickListener {
                val toDetail = Intent(requireContext(), PhotoDetailActivity::class.java)
                toDetail.putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, it.id)
                startActivity(toDetail)
            }

            with(rv_photos) {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                adapter = photoAdapter
            }

            viewModel.viewState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is FavoriteViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is FavoriteViewState.Loading -> {
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_photos.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                    }
                    is FavoriteViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is FavoriteViewState.Success -> {
                        swipe_refresh.isRefreshing = false
                        rv_photos.isVisible = true
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        photoAdapter.setPhotos(state.data)
                    }
                }
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.getAllFavoritePhotos()
            }

            viewModel.getAllFavoritePhotos()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        super.onPrepareOptionsMenu(menu)
    }
}
