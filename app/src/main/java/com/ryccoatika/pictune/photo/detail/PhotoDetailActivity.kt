package com.ryccoatika.pictune.photo.detail

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.domain.model.PhotoDetail
import com.ryccoatika.core.ui.TagAdapter
import com.ryccoatika.core.utils.filename
import com.ryccoatika.core.utils.loadBlurredImage
import com.ryccoatika.core.utils.loadProfilePicture
import com.ryccoatika.core.utils.toPhotoMinimal
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.download.DownloadQualityChooserDialog
import com.ryccoatika.pictune.photo.zoom.PhotoZoomActivity
import com.ryccoatika.pictune.search.activity.SearchActivity
import com.ryccoatika.pictune.user.UserActivity
import com.ryccoatika.pictune.utils.DownloadManager
import com.ryccoatika.pictune.utils.PermissionHelper
import com.ryccoatika.pictune.utils.formattedDate
import com.ryccoatika.pictune.utils.formattedNumber
import kotlinx.android.synthetic.main.activity_photo_detail.*
import kotlinx.android.synthetic.main.photo_detail_content.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PhotoDetailActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXTRA_PHOTO_ID = "extra_photo_id"
    }

    private val downloadManager: DownloadManager by lazy { DownloadManager(this) }
    private val viewModel: PhotoDetailViewModel by viewModel()
    private var photo: PhotoDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.title = ""
        }

        val photoId = intent.getStringExtra(EXTRA_PHOTO_ID)

        if (photoId != null) {
            viewModel.viewState.observe(this) { state ->
                when (state) {
                    is PhotoDetailViewState.Loading -> {
                        photo_detail_content.isVisible = false
                        view_loading.isVisible = true
                        view_error.isVisible = false
                    }
                    is PhotoDetailViewState.Error -> {
                        photo_detail_content.isVisible = false
                        view_loading.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    is PhotoDetailViewState.Success -> {
                        photo_detail_content.isVisible = true
                        view_loading.isVisible = false
                        view_error.isVisible = false
                        state.data?.let {
                            this.photo = it
                            populatePhoto(it)
                        }
                    }
                }
            }

            viewModel.getPhoto(photoId)
        }
    }

    private fun populatePhoto(photo: PhotoDetail) {
        photo_image.loadBlurredImage(photo.urls.regular, photo.color)
        photo_user.loadProfilePicture(photo.user.profileImage.medium)

        // set image content description
        photo_image.contentDescription = photo.altDescription

        // set user name
        photo_user_name.text = photo.user.name

        tv_published.text = getString(R.string.published_on, formattedDate(photo.createdAt))

        if (photo.location.title != "undefined") {
            tv_location.text = photo.location.title
            tv_location.setOnClickListener {
                val searchIntent = Intent(this, SearchActivity::class.java)
                searchIntent.putExtra(SearchActivity.EXTRA_QUERY, photo.location.title)
                startActivity(searchIntent)
            }
        } else {
            tv_location.isVisible = false
            image_location.isVisible = false
        }

        if (photo.description != "undefined")
            tv_description.text = photo.description
        else
            tv_description.isVisible = false

        tv_views.text = formattedNumber(photo.views)
        tv_downloads.text = formattedNumber(photo.downloads)

        tv_camera.text = if (photo.exif.make != "undefined") {
            if (photo.exif.model.toLowerCase(Locale.ROOT)
                    .contains(photo.exif.make.toLowerCase(Locale.ROOT).split(" ")[0])
            )
                photo.exif.model else photo.exif.make + " " + photo.exif.model
        } else "-"

        tv_focal.text = if (photo.exif.focalLength != "undefined")
            getString(R.string.text_focal_length, photo.exif.focalLength) else "-"

        tv_aperture.text = if (photo.exif.aperture != "undefined")
            getString(R.string.text_aperture, photo.exif.aperture) else "-"

        tv_shutter.text = if (photo.exif.exposureTime != "undefined")
            getString(R.string.text_shutter_speed, photo.exif.exposureTime) else "-"

        tv_iso.text = if (photo.exif.iso != 0)
            photo.exif.iso.toString() else "-"

        tv_dimensions.text = if (photo.width != 0)
            getString(R.string.text_dimensions, photo.width, photo.height) else "-"

        val tagAdapter = TagAdapter(photo.tags.filter { it.type == "search" }) {
            val searchIntent = Intent(this, SearchActivity::class.java)
            searchIntent.putExtra(SearchActivity.EXTRA_QUERY, it.title)
            startActivity(searchIntent)
        }
        rv_tag.adapter = tagAdapter

        var statusFavorite = false
        viewModel.isFavorite(photo.id).observe(this) {
            setStatusFavorite(it)
            statusFavorite = it
        }
        btn_favorite.setOnClickListener {
            viewModel.setPhotoFavorite(photo.toPhotoMinimal(), !statusFavorite)
        }

        photo_user_name.setOnClickListener(this)
        photo_user.setOnClickListener(this)
        btn_download.setOnClickListener(this)
        btn_set_as.setOnClickListener(this)
        photo_image.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_open_link -> {
                photo?.let {
                    val link = getString(R.string.link_referral, it.links.html)
                    val openIntent = Intent(Intent.ACTION_VIEW)
                    openIntent.data = Uri.parse(link)
                    startActivity(Intent.createChooser(openIntent, getString(R.string.open_with)))
                }
                true
            }
            R.id.menu_share_link -> {
                photo?.let {
                    val shareLink = Intent(Intent.ACTION_SEND)
                    shareLink.putExtra(Intent.EXTRA_TEXT, it.links.html)
                    shareLink.type = "text/plain"
                    startActivity(Intent.createChooser(shareLink, getString(R.string.share_using)))
                }
                true
            }
            R.id.menu_share_image -> {
                if (PermissionHelper.shouldAskPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                    photo?.let { photo ->
                        if (downloadManager.isFileExist(photo.filename)) {
                            val uri = downloadManager.getUriForPhoto(photo.filename)
                            uri?.let { shareImage(it, photo.links.html) }
                        } else {
                            val snackbar = Snackbar.make(
                                coordinator_layout,
                                getString(R.string.downloading_photo),
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .setAction(R.string.cancel) { downloadManager.cancelDownload() }
                            snackbar.show()
                            downloadManager.setOnCompletedListener {
                                snackbar.dismiss()
                                shareImage(it, photo.links.html)
                                viewModel.triggerDownload(photo.id)
                            }
                            downloadManager.startDownload(photo.filename, photo.urls.full, false)
                        }
                    }
                else
                    PermissionHelper.askPermission(
                        this,
                        listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun shareImage(uri: Uri, link: String) {
        val shareImage = Intent(Intent.ACTION_SEND)
        shareImage.putExtra(Intent.EXTRA_STREAM, uri)
        shareImage.putExtra(Intent.EXTRA_TEXT, link)
        shareImage.type = "image/*"
        shareImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(shareImage, getString(R.string.share_using)))
    }

    private fun setImageAs(uri: Uri) {
        val setWallpaper = Intent()
        setWallpaper.action = Intent.ACTION_ATTACH_DATA
        setWallpaper.addCategory(Intent.CATEGORY_DEFAULT)
        setWallpaper.setDataAndType(uri, "image/jpeg")
        setWallpaper.putExtra("mimeType", "image/jpeg")
        setWallpaper.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(
            Intent.createChooser(setWallpaper, getString(R.string.open_with))
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.photo_user,
            R.id.photo_user_name -> {
                photo?.let {
                    val intentUser = Intent(this, UserActivity::class.java)
                    intentUser.putExtra(UserActivity.EXTRA_USER_NAME, it.user.username)
                    startActivity(intentUser)
                }
            }
            R.id.photo_image -> {
                photo?.let {
                    val photoZoom = Intent(this, PhotoZoomActivity::class.java)
                    photoZoom.putExtra(PhotoZoomActivity.EXTRA_LINK, it.urls.regular)
                    photoZoom.putExtra(PhotoZoomActivity.EXTRA_COLOR, it.color)
                    startActivity(photoZoom)
                }
            }
            R.id.btn_download -> {
                if (PermissionHelper.shouldAskPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                    photo?.let { photo ->
                        DownloadQualityChooserDialog(this, photo.urls)
                            .setOnResultListener { url ->
                                downloadManager.setOnCompletedListener {
                                    viewModel.triggerDownload(photo.id)
                                }
                                if (downloadManager.isFileExist(photo.filename))
                                    MaterialAlertDialogBuilder(this)
                                        .setTitle(getString(R.string.file_exist_title))
                                        .setMessage(getString(R.string.file_exist_message))
                                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                                            downloadManager.startDownload(photo.filename, url, true)
                                        }
                                        .setNegativeButton(getString(R.string.no), null)
                                        .show()
                                else
                                    downloadManager.startDownload(photo.filename, url, true)
                            }
                            .show()
                    }
                else
                    PermissionHelper.askPermission(
                        this,
                        listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
            }
            R.id.btn_set_as -> {
                if (PermissionHelper.shouldAskPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                    photo?.let { photo ->
                        if (downloadManager.isFileExist(photo.filename)) {
                            val uri = downloadManager.getUriForPhoto(photo.filename)
                            uri?.let {
                                setImageAs(it)
                            }
                        } else {
                            val snackbar = Snackbar.make(
                                coordinator_layout,
                                getString(R.string.downloading_photo),
                                Snackbar.LENGTH_INDEFINITE
                            )
                                .setAction(R.string.cancel) { downloadManager.cancelDownload() }
                            snackbar.show()
                            downloadManager.setOnCompletedListener {
                                viewModel.triggerDownload(photo.id)
                                snackbar.dismiss()
                                setImageAs(it)
                            }
                            downloadManager.startDownload(photo.filename, photo.urls.full, false)
                        }
                    }
                else
                    PermissionHelper.askPermission(
                        this,
                        listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
            }
        }
    }

    private fun setStatusFavorite(isFavorite: Boolean) {
        if (isFavorite) {
            btn_favorite.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_favorite_black)
            )
        } else {
            btn_favorite.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black)
            )
        }
    }
}
