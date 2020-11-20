package com.ryccoatika.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.ryccoatika.core.R
import com.ryccoatika.core.domain.model.Topic
import com.ryccoatika.core.utils.BlurHashDecoder
import kotlinx.android.synthetic.main.item_list_topic.view.*

class TopicAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val topics = mutableListOf<Topic?>()
    private var onClickListener: ((Topic) -> Unit)? = null

    fun setTopics(topics: List<Topic>) {
        this.topics.clear()
        this.topics.addAll(topics)
        notifyDataSetChanged()
    }

    fun insertTopics(topics: List<Topic>) {
        this.topics.addAll(topics)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (Topic) -> Unit) {
        this.onClickListener = listener
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class TopicViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(topic: Topic) {
            with(itemView) {
                val coverPhoto = topic.coverPhoto
                val imageRatio = coverPhoto.height.toDouble().div(coverPhoto.width.toDouble())
                topic_image.layoutParams.apply {
                    height = (width.toDouble() * imageRatio).toInt()
                    topic_image.layoutParams = this

                    topic_image.setImageBitmap(
                        BlurHashDecoder.decode(coverPhoto.blurHash, width, height)
                    )

                    Glide.with(context)
                        .load(coverPhoto.urls.thumb)
                        .into(topic_image)
                }

                setOnClickListener { onClickListener?.invoke(topic) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.Data.ordinal -> TopicViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_topic, parent, false)
            )
            else -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_loading, parent, false)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (topics[position] == null)
            Type.Loading.ordinal
        else
            Type.Data.ordinal
    }

    override fun getItemCount(): Int = topics.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TopicViewHolder -> topics[position]?.let { holder.bindData(it) }
            else -> (holder.itemView.layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun showLoading() {
        topics.add(null)
        notifyItemInserted(topics.count().minus(1))
    }

    fun hideLoading() {
        if (topics.isNotEmpty()) {
            topics.removeAt(topics.count().minus(1))
            notifyItemRemoved(topics.count())
        }
    }

    enum class Type {
        Data,
        Loading
    }
}