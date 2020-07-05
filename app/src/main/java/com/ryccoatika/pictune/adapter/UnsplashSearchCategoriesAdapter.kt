package com.ryccoatika.pictune.adapter

import android.content.Intent
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.search.category.UnsplashCategoryActivity
import kotlinx.android.synthetic.main.unsplash_search_category_grid_item.view.*

class UnsplashSearchCategoriesAdapter(
    private val titleCategories: Array<String>,
    private val photoCategories: TypedArray) : RecyclerView.Adapter<UnsplashSearchCategoriesAdapter.CategoriesViewHolder>() {

    class CategoriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindData(title: String, photo: Int) {
            with(itemView) {
                unsplash_search_category_tv_title.text = title
                unsplash_search_category_image.setBackgroundResource(photo)
                itemView.setOnClickListener {
                    val openCategory = Intent(context, UnsplashCategoryActivity::class.java)
                    openCategory.putExtra(UnsplashCategoryActivity.EXTRA_CATEGORY, title)
                    context.startActivity(openCategory)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.unsplash_search_category_grid_item, parent, false))
    }

    override fun getItemCount(): Int = titleCategories.count()

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bindData(titleCategories[position], photoCategories.getResourceId(position, -1))
    }
}