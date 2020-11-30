package com.ryccoatika.core.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ryccoatika.core.R
import com.ryccoatika.core.domain.model.UserMinimal
import com.ryccoatika.core.utils.loadProfilePicture
import kotlinx.android.synthetic.main.item_list_user.view.*

class UserAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val users = mutableListOf<UserMinimal?>()
    private var onClickListener: ((UserMinimal) -> Unit)? = null

    fun setUsers(users: List<UserMinimal>?) {
        if (users == null) return
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun insertUsers(users: List<UserMinimal>?) {
        if (users == null) return
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    fun setOnClickListener(listener: (UserMinimal) -> Unit) {
        this.onClickListener = listener
    }

    inner class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(user: UserMinimal) {
            with(itemView) {
                user_image.loadProfilePicture(user.profileImage.medium)

                tv_user_name.text = user.name
                tv_username.text = context.getString(R.string.username, user.username)

                setOnClickListener { onClickListener?.invoke(user) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Type.Data.ordinal -> UserViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_user, parent, false)
            )
            else -> LoadingViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_loading, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = users.size

    override fun getItemViewType(position: Int): Int {
        return if (users[position] == null)
            Type.Loading.ordinal
        else
            Type.Data.ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder -> users[position]?.let { holder.bindData(it) }
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()

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

    enum class Type {
        Data,
        Loading
    }
}
