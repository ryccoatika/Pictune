package com.ryccoatika.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.ryccoatika.core.R
import com.ryccoatika.core.domain.model.PhotoMinimal
import com.ryccoatika.core.utils.BlurHashDecoder
import kotlinx.android.synthetic.main.item_list_photo.view.*

class PhotoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val photos = mutableListOf<PhotoMinimal?>()
    private var onClickListener: ((PhotoMinimal) -> Unit)? = null

    fun setPhotos(photos: List<PhotoMinimal>) {
        this.photos.clear()
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }

    fun insertPhotos(photos: List<PhotoMinimal>) {
        this.photos.addAll(photos)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (PhotoMinimal) -> Unit) {
        this.onClickListener = listener
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class GridViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(photo: PhotoMinimal) {
            with (itemView) {

                val imageRatio = photo.height.toDouble().div(photo.width.toDouble())
                photo_image.layoutParams.apply {
                    height = (width.toDouble() * imageRatio).toInt()
                    photo_image.layoutParams = this

                    photo_image.setImageBitmap(
                        BlurHashDecoder.decode(photo.blurHash, width, height)
                    )

                    Glide.with(context)
                        .load(photo.urls.thumb)
                        .into(photo_image)
                }

                setOnClickListener { onClickListener?.invoke(photo) }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.Data.ordinal -> GridViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_photo, parent, false)
            )
            else -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_loading, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (photos[position] == null)
            Type.Loading.ordinal
        else
            Type.Data.ordinal
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GridViewHolder -> photos[position]?.let { holder.bindData(it) }
            else -> {
                (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun showLoading() {
        photos.add(null)
        notifyItemInserted(photos.count().minus(1))
    }

    fun hideLoading() {
        if (photos.isNotEmpty()) {
            photos.removeAt(photos.count().minus(1))
            notifyItemRemoved(photos.count())
        }
    }

    enum class Type {
        Data,
        Loading
    }
}