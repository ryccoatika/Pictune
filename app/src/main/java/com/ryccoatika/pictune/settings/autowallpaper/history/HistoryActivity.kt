package com.ryccoatika.pictune.settings.autowallpaper.history

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.ui.PhotoAdapter
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.photo.detail.PhotoDetailActivity
import kotlinx.android.synthetic.main.activity_history.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryActivity : AppCompatActivity() {

    private val viewModel: HistoryViewModel by viewModel()
    private val photoAdapter = PhotoAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        photoAdapter.setHasStableIds(true)

        photoAdapter.setOnClickListener {
            val toDetail = Intent(this, PhotoDetailActivity::class.java)
            toDetail.putExtra(PhotoDetailActivity.EXTRA_PHOTO_ID, it.id)
            startActivity(toDetail)
        }

        with(rv_photos) {
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            adapter = photoAdapter
        }

        viewModel.viewState.observe(this) { state ->
            when (state) {
                is HistoryViewState.Loading -> {
                    if (!swipe_refresh.isRefreshing)
                        view_loading.isVisible = true
                    view_empty.isVisible = false
                    view_error.isVisible = false
                    rv_photos.isVisible = false
                }
                is HistoryViewState.Empty -> {
                    swipe_refresh.isRefreshing = false
                    view_loading.isVisible = false
                    view_empty.isVisible = true
                    view_error.isVisible = false
                    rv_photos.isVisible = false
                }
                is HistoryViewState.Error -> {
                    swipe_refresh.isRefreshing = false
                    view_loading.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = true
                    rv_photos.isVisible = false
                    view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                        state.message ?: getString(R.string.something_went_wrong)
                }
                is HistoryViewState.Success -> {
                    swipe_refresh.isRefreshing = false
                    view_loading.isVisible = false
                    view_empty.isVisible = false
                    view_error.isVisible = false
                    rv_photos.isVisible = true
                    photoAdapter.setPhotos(state.data)
                }
            }
        }

        viewModel.getAllPhotoHistory()
        swipe_refresh.setOnRefreshListener {
            viewModel.getAllPhotoHistory()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_clear -> {
                viewModel.deleteAllPhotoFromHistory()
                viewModel.getAllPhotoHistory()
                true
            }
            else -> false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}