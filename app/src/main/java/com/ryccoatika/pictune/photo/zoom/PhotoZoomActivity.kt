package com.ryccoatika.pictune.photo.zoom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ryccoatika.core.utils.loadBlurredImage
import com.ryccoatika.pictune.R
import kotlinx.android.synthetic.main.activity_photo_zoom.*

class PhotoZoomActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LINK = "extra_link"
        const val EXTRA_COLOR = "extra_color"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_zoom)

        supportActionBar?.title = ""

        val link = intent.getStringExtra(EXTRA_LINK)
        val color = intent.getStringExtra(EXTRA_COLOR)
        if (link != null && color != null) {
            photo_image.loadBlurredImage(link, color)
        }
    }
}
