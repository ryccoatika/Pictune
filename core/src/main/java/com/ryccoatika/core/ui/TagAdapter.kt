package com.ryccoatika.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryccoatika.core.R
import com.ryccoatika.core.domain.model.Tag
import kotlinx.android.synthetic.main.item_list_tag.view.*

class TagAdapter(
    private val tags: List<Tag>,
    private val onClickListener: (Tag) -> Unit
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {


    inner class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(tag: Tag) {
            with(itemView) {
                tv_title.text = tag.title
                setOnClickListener { onClickListener(tag) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        return TagViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_tag, parent, false))
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItemCount(): Int = tags.size
}