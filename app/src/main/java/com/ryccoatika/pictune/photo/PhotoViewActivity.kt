package com.ryccoatika.pictune.photo

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.activity_photo_view.*

class PhotoViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LINK = "extra_link"
        const val EXTRA_COLOR = "extra_color"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view)

        supportActionBar?.title = ""

        val link = intent.getStringExtra(EXTRA_LINK)
        val color = intent.getStringExtra(EXTRA_COLOR)
        if (link.isNullOrEmpty()) {
            finish()
            return
        }
        val circularProgress = CircularProgressDrawable(this)
        circularProgress.strokeWidth = 5f
        circularProgress.centerRadius = 30f
        circularProgress.start()

        Glide.with(this)
            .load(link)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .crossFade()
            .error(R.drawable.ic_broken_image_black_24dp)
            .placeholder(circularProgress)
            .into(photo_view_iv)

        photo_view_background.setBackgroundColor(Color.parseColor(color ?: "#000"))
        photo_view_btn_close.setOnClickListener { onBackPressed() }
    }
}
