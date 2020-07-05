package com.ryccoatika.pictune.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.db.UnsplashUserResponse
import com.ryccoatika.pictune.user.UnsplashUserActivity
import kotlinx.android.synthetic.main.unsplash_user_list_item.view.*

class UnsplashUserListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var users = mutableListOf<UnsplashUserResponse?>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(user: UnsplashUserResponse) {
            with(itemView) {
                Glide.with(context)
                    .load(user.profileImage?.large)
                    .thumbnail(Glide.with(context).load(user.profileImage?.small))
                    .placeholder(context.getColor(R.color.colorAccent))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_broken_image_black_24dp)
                    .into(unsplash_user_item_image)
                unsplash_user_item_tv_name.text = user.name
                unsplash_user_item_tv_username.text = "@${user.username}"

                itemView.setOnClickListener {
                    val openUser = Intent(context, UnsplashUserActivity::class.java)
                    openUser.putExtra(UnsplashUserActivity.EXTRA_USER_NAME, user.username)
                    context.startActivity(openUser)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.Data.ordinal -> UserViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.unsplash_user_list_item, parent, false)
            )
            else -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_more_loading_item, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = users.count()

    override fun getItemViewType(position: Int): Int {
        return if (users[position] == null)
            Type.Loading.ordinal
        else
            Type.Data.ordinal
    }

    enum class Type {
        Data,
        Loading
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> users[position]?.let { holder.bindData(it) }
        }
    }

    fun showLoading() {
        users.add(null)
        notifyItemInserted(users.count().minus(1))
    }

    fun hideLoading() {
        if (users.isNotEmpty()) {
            users.removeAt(users.count().minus(1))
            notifyItemRemoved(users.count())
        }
    }

    fun loadMore(results: MutableList<UnsplashUserResponse?>) {
        users.addAll(results)
        notifyDataSetChanged()
    }
}