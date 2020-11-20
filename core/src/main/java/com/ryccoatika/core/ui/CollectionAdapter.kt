package com.ryccoatika.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.ryccoatika.core.R
import com.ryccoatika.core.domain.model.Collection
import com.ryccoatika.core.utils.BlurHashDecoder
import kotlinx.android.synthetic.main.item_list_collections.view.*

class CollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val collections = mutableListOf<Collection?>()
    private var onClickListener: ((Collection) -> Unit)? = null

    fun setCollections(collections: List<Collection>) {
        this.collections.clear()
        this.collections.addAll(collections)
        notifyDataSetChanged()
    }

    fun insertCollections(collections: List<Collection>) {
        this.collections.addAll(collections)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (Collection) -> Unit) {
        this.onClickListener = listener
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class CollectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(collection: Collection) {
            with (itemView) {
                val coverPhoto = collection.coverPhoto
                val imageRatio = coverPhoto.height.toDouble().div(coverPhoto.width.toDouble())
                collection_image.layoutParams.apply {
                    height = (width.toDouble() * imageRatio).toInt()
                    collection_image.layoutParams = this

                    collection_image.setImageBitmap(
                        BlurHashDecoder.decode(coverPhoto.blurHash, width, height)
                    )

                    Glide.with(context)
                        .load(coverPhoto.urls.thumb)
                        .into(collection_image)

                    setOnClickListener { onClickListener?.invoke(collection) }
                }
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

    override fun getItemCount(): Int = collections.size

    override fun getItemViewType(position: Int): Int {
        return when (collections[position]) {
            null -> Type.Loading.ordinal
            else -> Type.Data.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CollectionViewHolder -> collections[position]?.let { holder.bindData(it) }
            else -> (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }
    }

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