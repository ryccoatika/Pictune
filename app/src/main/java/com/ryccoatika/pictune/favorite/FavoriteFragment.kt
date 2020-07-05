package com.ryccoatika.pictune.favorite

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.UnsplashPhotoGridAdapter
import kotlinx.android.synthetic.main.fragment_favorite.*

/**
 * A simple [Fragment] subclass.
 */
class FavoriteFragment : Fragment() {

    companion object {
        fun changeFavoriteButtonColor(isFavorite: Boolean, imageView: ImageView) {
            if (isFavorite) {
                imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.ic_favorite_black_24dp))
                imageView.imageTintList = ColorStateList.valueOf(imageView.context.getColor(R.color.colorPrimary))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.ic_favorite_border_black_24dp))
                imageView.imageTintList = ColorStateList.valueOf(imageView.context.getColor(R.color.colorPrimary))
            }
        }
    }

    class FavoriteViewModelProvider(val context: Context): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return (FavoriteViewModel(context) as T)
        }
    }

    private val viewModel: FavoriteView by lazy {
        ViewModelProvider(this, FavoriteViewModelProvider(requireContext())).get(FavoriteViewModel::class.java)
    }
    private val photosAdapter = UnsplashPhotoGridAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()
        photosAdapter.setHasStableIds(true)
        favorite_rv.adapter = photosAdapter
        favorite_rv.setHasFixedSize(true)

        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is FavoriteViewState.Loading -> {
                    showWarning(false)
                    photosAdapter.photos.clear()
                    if (!favorite_swipe_refresh.isRefreshing)
                        favorite_pb.visibility = View.VISIBLE
                }
                is FavoriteViewState.SuccessGetFavorites -> {
                    showWarning(state.response.isEmpty())
                    favorite_pb.visibility = View.INVISIBLE
                    favorite_swipe_refresh.isRefreshing = false
                    state.response.forEach {
                        viewModel.getUnsplashPhotoFromInternet(it.photoId)
                    }
                }
                is FavoriteViewState.SuccessGetPhoto -> {
                    favorite_pb.visibility = View.INVISIBLE
                    photosAdapter.addPhoto(state.response)
                }
                is FavoriteViewState.Error -> {
                    showWarning(true)
                    favorite_pb.visibility = View.INVISIBLE
                    favorite_swipe_refresh.isRefreshing = false
                    favorite_tv_warning.text = getString(R.string.text_failed_getting_data_try_refresh)
                    Log.w("190401", FavoriteFragment::class.simpleName, state.error)
                }
            }
        })

        favorite_swipe_refresh.setOnRefreshListener {
            startLoad()
        }

        startLoad()
    }

    private fun showWarning(state: Boolean) {
        if (state) {
            favorite_rv.visibility = View.INVISIBLE
            favorite_tv_warning.visibility = View.VISIBLE
            favorite_tv_warning.text = getString(R.string.text_you_dont_have_favorite)
        } else {
            favorite_rv.visibility = View.VISIBLE
            favorite_tv_warning.visibility = View.INVISIBLE
        }
    }

    private fun startLoad() {
        viewModel.getAllFavoritePhotos()
    }
}
