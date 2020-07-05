package com.ryccoatika.pictune.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ryccoatika.pictune.*
import com.ryccoatika.pictune.db.UnsplashPhotoResponse
import com.ryccoatika.pictune.db.room.AppDatabase
import com.ryccoatika.pictune.db.room.FavoritePhotoEntity
import com.ryccoatika.pictune.download.*
import com.ryccoatika.pictune.favorite.FavoriteFragment
import com.ryccoatika.pictune.photo.UnsplashPhotoActivity
import com.ryccoatika.pictune.settings.autowallpaper.Wallpaper
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.unsplash_photos_grid_item.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File

class UnsplashPhotoGridAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var photos = mutableListOf<UnsplashPhotoResponse?>()
        set(value) {
            field.clear()
            field.addAll(value)
            notifyDataSetChanged()
        }

    fun addPhoto(photo: UnsplashPhotoResponse?) {
        photos.add(photo)
        notifyDataSetChanged()
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val database = AppDatabase.getInstance(view.context)
        fun bindData(photo: UnsplashPhotoResponse) {
            with (itemView) {

                // when more clicked would showing popup menu
                unsplash_photos_item_more.setOnClickListener {
                    val popupMenu = PopupMenu(context, unsplash_photos_item_more)
                    popupMenu.menuInflater.inflate(R.menu.unsplash_item_popup_menu, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        var filename = photo.user?.name?.toLowerCase()?.replace(" ", "-")
                        filename = context.getString(R.string.download_filename, filename, photo.id)
                        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: ""
                        val urlRegular = photo.urls?.regular ?: ""
                        val urlFull = photo.urls?.full ?: ""

                        val downloadDialog = DownloadDialog(context)

                        when (menuItem?.itemId) {

                            R.id.popup_unsplash_share -> {
                                object : Downloader() {
                                    override fun onStart() {
                                        downloadDialog.apply {
                                            show()
                                            title = context.getString(R.string.text_wait_connection)
                                            indeterminate = true
                                            progress = 0
                                            cancelEnable = false
                                        }
                                    }

                                    override fun onFailed(error: Throwable) {
                                        downloadDialog.apply {
                                            indeterminate = false
                                            title = context.getString(R.string.text_download_failed)
                                            progress = 0
                                            cancelEnable = true
                                        }
                                    }

                                    override fun onDownloadStart(call: Call<ResponseBody>, file: File): Boolean {
                                        return if (!file.exists()){
                                            file.createNewFile()
                                            downloadDialog.apply {
                                                title = context.getString(R.string.text_downloading)
                                                indeterminate = false
                                                cancelEnable = true
                                                progress = 0
                                                this.call = call
                                            }
                                            true
                                        } else {
                                            shareImage(context, file.toString(), photo.links?.html!!)
                                            downloadDialog.dismiss()
                                            call.cancel()
                                            false
                                        }
                                    }

                                    override fun onProgress(progress: Int) {
                                        (context as Activity).runOnUiThread {
                                            downloadDialog.progress = progress
                                        }
                                    }

                                    override fun onCancelled() {
                                        downloadDialog.dismiss()
                                    }

                                    override fun onDownloaded(absolutePath: String) {
                                        downloadDialog.dismiss()
                                        shareImage(context, absolutePath, photo.links?.html!!)
                                    }
                                }.startDownload(dir, filename, urlRegular)
                                true
                            }

                            R.id.popup_unsplash_download -> {
                                // check permission
                                if (PermissionManager.isWriteExternalPermissionGranted(context)) {
                                    // showing quality chooser dialog
                                    photo.urls?.let {
                                        val qualityChoose = DownloadQualityDialog(context, filename, it)
                                        qualityChoose.show()
                                    }
                                } else {
                                    PermissionManager.requestWriteExternalPermission(context)
                                }
                                true
                            }

                            R.id.popup_unsplash_set_as -> {
                                object : Downloader() {
                                    override fun onStart() {
                                        downloadDialog.apply {
                                            show()
                                            title = context.getString(R.string.text_wait_connection)
                                            indeterminate = true
                                            progress = 0
                                            cancelEnable = false
                                        }
                                    }

                                    override fun onFailed(error: Throwable) {
                                        downloadDialog.apply {
                                            indeterminate = false
                                            title = context.getString(R.string.text_download_failed)
                                            progress = 0
                                            cancelEnable = true
                                        }
                                    }

                                    override fun onDownloadStart(call: Call<ResponseBody>, file: File): Boolean {
                                        return if (!file.exists()){
                                            file.createNewFile()
                                            downloadDialog.apply {
                                                title = context.getString(R.string.text_downloading)
                                                indeterminate = false
                                                cancelEnable = true
                                                progress = 0
                                            }
                                            true
                                        } else {
                                            Wallpaper.setWallpaper(context, file.absolutePath)
                                            downloadDialog.dismiss()
                                            call.cancel()
                                            false
                                        }
                                    }

                                    override fun onProgress(progress: Int) {
                                        (context as Activity).runOnUiThread {
                                            downloadDialog.progress = progress
                                        }
                                    }

                                    override fun onCancelled() {
                                        downloadDialog.dismiss()
                                    }

                                    override fun onDownloaded(absolutePath: String) {
                                        downloadDialog.dismiss()
                                        Wallpaper.setWallpaper(context, absolutePath)
                                    }
                                }.startDownload(dir, filename, urlFull )
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                }


                val circularProgress = CircularProgressDrawable(context)
                circularProgress.centerRadius = 30f
                circularProgress.strokeWidth = 5f
                circularProgress.start()

                val ratio = photo.height?.toDouble()?.div(photo.width?.toDouble()!!)
                ratio?.let {
                    val slp = unsplash_photos_item_thumb.layoutParams
                    slp.height = (slp.width.toDouble() * it).toInt()
                    unsplash_photos_item_thumb.layoutParams = slp
                }
                val slp = unsplash_photos_item_thumb.layoutParams

                unsplash_photos_item_thumb.setBackgroundColor(Color.parseColor(photo.color))

                Glide.with(context)
                    .load(photo.urls?.thumb)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .override(slp.width, slp.height)
                    .placeholder(circularProgress)
                    .into(unsplash_photos_item_thumb)

                unsplash_photos_item_thumb.setOnClickListener {
                    val openPhoto = Intent(context, UnsplashPhotoActivity::class.java)
                    openPhoto.putExtra(UnsplashPhotoActivity.EXTRA_PHOTO, photo)
                    context.startActivity(openPhoto)
                }

                unsplash_photos_item_thumb.contentDescription = photo.altDescription
                unsplash_photos_item_author.text = photo.user?.name ?: "-"

                // check if data exist or not
                var isFavorite = false
                Thread {
                    Runnable {
                        database?.let {
                            it.getPhotoDao().getPhotoById(photo.id ?: "").observeOn(Schedulers.io())
                                .subscribe({ responses ->
                                    // if not exist
                                    isFavorite = responses.count() != 0
                                    FavoriteFragment.changeFavoriteButtonColor(isFavorite, unsplash_photos_item_favorite)
                                }, {
                                    Log.d("190401", "Error when check if data exist!", it)
                                })
                        }
                    }.run()
                }.start()

                // when favorite button clicked
                unsplash_photos_item_favorite.setOnClickListener {
                    val favoritePhoto = FavoritePhotoEntity(photo.id ?: "")
                    Thread {
                        Runnable {
                            database?.let {
                                if (isFavorite) {
                                    it.getPhotoDao().delete(favoritePhoto)
                                        .observeOn(Schedulers.io())
                                        .subscribe({
                                            isFavorite = !isFavorite
                                            FavoriteFragment.changeFavoriteButtonColor(isFavorite, unsplash_photos_item_favorite)
                                        }, {
                                            Log.d("190401", "error when removing photo from favorite", it)
                                        })
                                } else {
                                    it.getPhotoDao().insert(favoritePhoto)
                                        .observeOn(Schedulers.io())
                                        .subscribe({
                                            isFavorite = !isFavorite
                                            FavoriteFragment.changeFavoriteButtonColor(isFavorite, unsplash_photos_item_favorite)
                                        }, {
                                            Log.d("190401", "error when adding photo into favorite", it)
                                        })
                                }
                            }
                        }.run()
                    }.start()
                }
            }
        }

    }

    private fun shareImage(context: Context, path: String, url: String) {
        val shareImage = Intent()
        shareImage.action = Intent.ACTION_SEND
        shareImage.putExtra(Intent.EXTRA_TEXT, url)
        shareImage.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
        shareImage.type = "image/*"
        shareImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(
            Intent.createChooser(shareImage, context.getString(R.string.text_share_image))
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.Data.ordinal -> GridViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.unsplash_photos_grid_item, parent, false)
            )
            else -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_more_loading_item, parent, false)
            )
        }
    }

    enum class Type {
        Data,
        Loading
    }

    override fun getItemViewType(position: Int): Int {
        return if (photos[position] == null)
            Type.Loading.ordinal
        else
            Type.Data.ordinal
    }

    override fun getItemCount(): Int = photos.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GridViewHolder -> photos[position]?.let { holder.bindData(it) }
            else -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun showLoading() {
        addPhoto(null)
        notifyItemInserted(photos.count().minus(1))
    }

    fun hideLoading() {
        if (photos.isNotEmpty()) {
            photos.removeAt(photos.count().minus(1))
            notifyItemRemoved(photos.count())
        }
    }

    fun loadMore(results: MutableList<UnsplashPhotoResponse?>) {
        photos.addAll(results)
        notifyDataSetChanged()
    }
}