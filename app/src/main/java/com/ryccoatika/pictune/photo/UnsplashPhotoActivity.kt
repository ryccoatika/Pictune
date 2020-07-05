package com.ryccoatika.pictune.photo

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ryccoatika.pictune.PermissionManager
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.db.UnsplashPhotoResponse
import com.ryccoatika.pictune.db.room.AppDatabase
import com.ryccoatika.pictune.db.room.FavoritePhotoEntity
import com.ryccoatika.pictune.download.*
import com.ryccoatika.pictune.favorite.FavoriteFragment
import com.ryccoatika.pictune.settings.autowallpaper.Wallpaper
import com.ryccoatika.pictune.user.UnsplashUserActivity
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_unsplash_photo.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat

class UnsplashPhotoActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_PHOTO = "extra_photo"
    }

    private var unsplashPhoto: UnsplashPhotoResponse? = null
    private var database: AppDatabase? = null
    private var isFavorite = false
    private var filename = ""
    private var dir = ""
    private var urlRegular = ""
    private var urlFull = ""
    private val downloadDialog: DownloadDialog by lazy {
        DownloadDialog(this)
    }
    private val viewModel: UnsplashPhotoView by lazy {
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(UnsplashPhotoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsplash_photo)

        setSupportActionBar(unsplash_photo_toolbar)

        unsplashPhoto = intent.getParcelableExtra(EXTRA_PHOTO)

        if (unsplashPhoto == null) {
            finish()
            return
        }

        unsplashPhoto?.let {
            initData(it)
        }

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is UnsplashPhotoViewState.LoadStats -> {
                    unsplash_photo_tv_likes.text = getString(R.string.text_likes, state.response.likes?.total)
                    unsplash_photo_tv_download.text = getString(R.string.text_downloads, state.response.downloads?.total)
                    unsplash_photo_tv_views.text = getString(R.string.text_views, state.response.views?.total)
                }
                is UnsplashPhotoViewState.Error -> {
                    unsplash_photo_tv_likes.text = "-"
                    unsplash_photo_tv_download.text = "-"
                    unsplash_photo_tv_views.text = "-"
                    Log.w("190401", UnsplashPhotoActivity::class.simpleName, state.error)
                }
            }
        })

        unsplash_photo_tv_name.setOnClickListener(this)
        unsplash_photo_user_image.setOnClickListener(this)
        unsplash_photo_btn_download.setOnClickListener(this)
        unsplash_photo_btn_favorite.setOnClickListener(this)
        unsplash_photo_btn_set_as.setOnClickListener(this)
        unsplash_photo_image.setOnClickListener(this)
    }

    private fun initData(photo: UnsplashPhotoResponse) {

        photo.id?.let {
            viewModel.getPhotoStatistics(it)
        }

        filename = photo.user?.name?.toLowerCase()?.replace(" ", "-") ?: ""
        filename = getString(R.string.download_filename, filename, photo.id)
        dir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath ?: ""
        urlRegular = photo.urls?.regular ?: ""
        urlFull = photo.urls?.full ?: ""

        database = AppDatabase.getInstance(this)

        // create circular progress drawable for load photo
        val circularProgress = CircularProgressDrawable(this)
        circularProgress.strokeWidth = 5f
        circularProgress.centerRadius = 30f
        circularProgress.start()

        // load photo from internet
        Glide.with(this)
            .load(photo.urls?.small)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .thumbnail(Glide.with(this).load(photo.urls?.thumb))
            .crossFade()
            .error(R.drawable.ic_broken_image_black_24dp)
            .placeholder(circularProgress)
            .into(unsplash_photo_image)

        Glide.with(this)
            .load(photo.user?.profileImage?.medium)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .crossFade()
            .error(R.drawable.ic_broken_image_black_24dp)
            .into(unsplash_photo_user_image)

        Thread {
            Runnable {
                database?.let {
                    it.getPhotoDao().getPhotoById(photo.id ?: "").observeOn(Schedulers.io())
                        .subscribe({ responses ->
                            // if not exist
                            isFavorite = responses.count() != 0
                            FavoriteFragment.changeFavoriteButtonColor(isFavorite, unsplash_photo_btn_favorite)
                        }, {
                            Log.d("190401", "Error when check if data exist!", it)
                        })
                }
            }.run()
        }.start()

        // set image content description
        unsplash_photo_image.contentDescription = photo.altDescription

        // set offset color when photo's width not full
        unsplash_photo_image.setBackgroundColor(Color.parseColor(photo.color))

        // set user name
        unsplash_photo_tv_name.text = photo.user?.name

        unsplash_photo_tv_description.text = photo.description
        unsplash_photo_tv_description.visibility = if (photo.description.isNullOrEmpty()) View.GONE else View.VISIBLE

        val createdAt = photo.craetedAt!!.split("T")[0]
        val dateCreatedAt = SimpleDateFormat("yyyy-MM-dd").parse(createdAt)
        unsplash_photo_tv_date_created.text = DateFormat.getDateInstance(DateFormat.DEFAULT).format(dateCreatedAt!!)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.unsplash_photo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.photo_menu_open_link -> {
                unsplashPhoto?.let {
                    val openIntent = Intent(Intent.ACTION_VIEW)
                    openIntent.data = Uri.parse(it.links?.html)
                    startActivity(Intent.createChooser(openIntent, getString(R.string.text_open_with)))
                }
                true
            }
            R.id.photo_menu_share_link -> {
                unsplashPhoto?.let {
                    val shareLink = Intent(Intent.ACTION_SEND)
                    shareLink.putExtra(Intent.EXTRA_TEXT, it.links?.html)
                    shareLink.type = "text/plain"
                    startActivity(Intent.createChooser(shareLink, getString(R.string.text_share_using)))
                }
                true
            }
            R.id.photo_menu_share_image -> {
                unsplashPhoto?.let {
                    object : Downloader() {
                        override fun onStart() {
                            downloadDialog.apply {
                                show()
                                title = getString(R.string.text_wait_connection)
                                indeterminate = true
                                progress = 0
                                cancelEnable = false
                            }
                        }

                        override fun onFailed(error: Throwable) {
                            downloadDialog.apply {
                                indeterminate = false
                                title = getString(R.string.text_download_failed)
                                progress = 0
                                cancelEnable = true
                            }
                        }

                        override fun onDownloadStart(call: Call<ResponseBody>, file: File): Boolean {
                            return if (!file.exists()){
                                file.createNewFile()
                                downloadDialog.apply {
                                    title = getString(R.string.text_downloading)
                                    indeterminate = false
                                    cancelEnable = true
                                    progress = 0
                                    this.call = call
                                }
                                true
                            } else {
                                shareImage(file.absolutePath, unsplashPhoto?.links?.html ?: "")
                                downloadDialog.dismiss()
                                call.cancel()
                                false
                            }
                        }

                        override fun onProgress(progress: Int) {
                            this@UnsplashPhotoActivity.runOnUiThread {
                                downloadDialog.progress = progress
                            }
                        }

                        override fun onCancelled() {
                            downloadDialog.dismiss()
                        }

                        override fun onDownloaded(absolutePath: String) {
                            downloadDialog.dismiss()
                            shareImage(absolutePath, unsplashPhoto?.links?.html ?: "")
                        }
                    }.startDownload(dir, filename, urlRegular)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareImage(absolutePath: String, link: String) {
        val shareImage = Intent(Intent.ACTION_SEND)
        shareImage.putExtra(Intent.EXTRA_STREAM, Uri.parse(absolutePath))
        shareImage.putExtra(Intent.EXTRA_TEXT, link)
        shareImage.type = "image/*"
        shareImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(
            Intent.createChooser(shareImage, getString(R.string.text_share_using))
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.unsplash_photo_user_image,
            R.id.unsplash_photo_tv_name -> {
                val intentUser = Intent(this, UnsplashUserActivity::class.java)
                intentUser.putExtra(UnsplashUserActivity.EXTRA_USER_NAME, unsplashPhoto?.user?.username)
                startActivity(intentUser)
            }
            R.id.unsplash_photo_image -> {
                val photo_view = Intent(this, PhotoViewActivity::class.java)
                photo_view.putExtra(PhotoViewActivity.EXTRA_LINK, unsplashPhoto?.urls?.regular)
                photo_view.putExtra(PhotoViewActivity.EXTRA_COLOR, unsplashPhoto?.color)
                startActivity(photo_view)
            }
            R.id.unsplash_photo_btn_download -> {
                if (PermissionManager.isWriteExternalPermissionGranted(this)) {
                    unsplashPhoto?.urls?.let {
                        val qualityChoose = DownloadQualityDialog(this, filename, it)
                        qualityChoose.show()
                    }
                } else {
                    PermissionManager.requestWriteExternalPermission(this)
                }
            }
            R.id.unsplash_photo_btn_set_as -> {
                object : Downloader() {
                    override fun onStart() {
                        downloadDialog.apply {
                            show()
                            title = getString(R.string.text_wait_connection)
                            indeterminate = true
                            progress = 0
                            cancelEnable = false
                        }
                    }

                    override fun onFailed(error: Throwable) {
                        downloadDialog.apply {
                            indeterminate = false
                            title = getString(R.string.text_download_failed)
                            progress = 0
                            cancelEnable = true
                        }
                    }

                    override fun onDownloadStart(call: Call<ResponseBody>, file: File): Boolean {
                        return if (!file.exists()){
                            file.createNewFile()
                            downloadDialog.apply {
                                title = getString(R.string.text_downloading)
                                indeterminate = false
                                cancelEnable = true
                                progress = 0
                            }
                            true
                        } else {
                            Wallpaper.setWallpaper(this@UnsplashPhotoActivity, file.absolutePath)
                            downloadDialog.dismiss()
                            call.cancel()
                            false
                        }
                    }

                    override fun onProgress(progress: Int) {
                        this@UnsplashPhotoActivity.runOnUiThread {
                            downloadDialog.progress = progress
                        }
                    }

                    override fun onCancelled() {
                        downloadDialog.dismiss()
                    }

                    override fun onDownloaded(absolutePath: String) {
                        downloadDialog.dismiss()
                        Wallpaper.setWallpaper(this@UnsplashPhotoActivity, absolutePath)
                    }
                }.startDownload(dir, filename, urlFull)
            }
            R.id.unsplash_photo_btn_favorite -> {
                val favoritePhoto = FavoritePhotoEntity(unsplashPhoto?.id ?: "")
                Thread {
                    Runnable {
                        database?.let {
                            if (isFavorite) {
                                it.getPhotoDao().delete(favoritePhoto)
                                    .observeOn(Schedulers.io())
                                    .subscribe({
                                        isFavorite = !isFavorite
                                        FavoriteFragment.changeFavoriteButtonColor(isFavorite, unsplash_photo_btn_favorite)
                                    }, {
                                        Log.d("190401", "error when removing photo from favorite", it)
                                    })
                            } else {
                                it.getPhotoDao().insert(favoritePhoto)
                                    .observeOn(Schedulers.io())
                                    .subscribe({
                                        isFavorite = !isFavorite
                                        FavoriteFragment.changeFavoriteButtonColor(isFavorite, unsplash_photo_btn_favorite)
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
