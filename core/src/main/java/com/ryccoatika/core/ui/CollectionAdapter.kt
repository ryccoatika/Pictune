package com.ryccoatika.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ryccoatika.core.R
import com.ryccoatika.core.domain.model.CollectionMinimal
import com.ryccoatika.core.utils.getAspectRatio
import com.ryccoatika.core.utils.loadBlurredImage
import com.ryccoatika.core.utils.setHeightAsRatio
import kotlinx.android.synthetic.main.item_list_collections.view.*

class CollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val collections = mutableListOf<CollectionMinimal?>()
    private var onClickListener: ((CollectionMinimal) -> Unit)? = null

    fun setCollections(collections: List<CollectionMinimal>?) {
        if (collections == null) return
        this.collections.clear()
        this.collections.addAll(collections)
        notifyDataSetChanged()
    }

    fun insertCollections(collections: List<CollectionMinimal>?) {
        if (collections == null) return
        this.collections.addAll(collections)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (CollectionMinimal) -> Unit) {
        this.onClickListener = listener
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class CollectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(collection: CollectionMinimal) {
            with (itemView) {
                val coverPhoto = collection.coverPhoto
                user_image.setHeightAsRatio(coverPhoto.getAspectRatio)
                user_image.loadBlurredImage(coverPhoto.urls.thumb, coverPhoto.color)
                user_image.contentDescription = coverPhoto.altDescription

                tv_title.text = collection.title
                tv_total_photos.text = context.getString(R.string.total_photos, collection.totalPhotos)

                setOnClickListener { onClickListener?.invoke(collection) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.Data.ordinal -> CollectionViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_collections, parent, false)
            )
            else -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_loading, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (collections[position] == null)
            Type.Loading.ordinal
        else
            Type.Data.ordinal
    }

    override fun getItemCount(): Int = collections.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CollectionViewHolder -> collections[position]?.let { holder.bindData(it) }
            else -> (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun showLoading() {
        collections.add(null)
        notifyItemInserted(collections.count().minus(1))
    }

    fun hideLoading() {
        if (collections.isNotEmpty()) {
            collections.removeAt(collections.count().minus(1))
            notifyItemRemoved(collections.count())
        }
    }

    enum class Type {
        Data,
        Loading
    }
}