package com.ryccoatika.pictune.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.collection.UnsplashCollectionActivity
import com.ryccoatika.pictune.db.UnsplashCollectionResponse
import kotlinx.android.synthetic.main.unsplash_collections_grid_item.view.*

class UnsplashCollectionGridAdapter
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var collections = mutableListOf<UnsplashCollectionResponse?>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class CollectionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(collection: UnsplashCollectionResponse) {
            with (itemView) {
                val circularProgress = CircularProgressDrawable(context)
                circularProgress.centerRadius = 30f
                circularProgress.strokeWidth = 5f
                circularProgress.start()

                Glide.with(context)
                    .load(collection.coverPhoto?.urls?.thumb)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .centerCrop()
                    .placeholder(circularProgress)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(unsplash_collection_item_thumb)

                unsplash_collection_item_thumb.setOnClickListener {
                    val openCollection = Intent(context, UnsplashCollectionActivity::class.java)
                    openCollection.putExtra(UnsplashCollectionActivity.EXTRA_COLLECTION, collection)
                    context.startActivity(openCollection)
                }

                unsplash_collection_item_thumb.contentDescription = collection.title
                unsplash_collection_item_total_photos.text = collection.totalPhotos.toString()
                unsplash_collection_item_description.text = collection.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.Data.ordinal -> CollectionsViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.unsplash_collections_grid_item, parent, false)
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

    override fun getItemCount(): Int = collections.count()

    override fun getItemViewType(position: Int): Int {
        return when (collections[position]) {
            null -> Type.Loading.ordinal
            else -> Type.Data.ordinal
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CollectionsViewHolder -> collections[position]?.let { holder.bindData(it) }
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

    fun loadMore(results: MutableList<UnsplashCollectionResponse?>) {
        collections.addAll(results)
        notifyDataSetChanged()
    }
}