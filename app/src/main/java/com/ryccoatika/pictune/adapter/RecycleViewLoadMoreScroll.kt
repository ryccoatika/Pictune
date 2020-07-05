package com.ryccoatika.pictune.adapter

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecycleViewLoadMoreScroll : RecyclerView.OnScrollListener {
    var isLoading = false
    private var visibleThreshold = 1
    private var totalItem = 0
    private var lastVisibleItem = 0
    private lateinit var loadMoreListener: () -> Unit
    private val layoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: GridLayoutManager) {
        this.layoutManager = layoutManager
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        this.layoutManager = layoutManager
    }

    constructor(layoutManager: LinearLayoutManager) {
        this.layoutManager = layoutManager
    }

    fun setOnLoadMoreListener(listener: () -> Unit) {
        this.loadMoreListener = listener
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0) return

        totalItem = layoutManager.itemCount

        when (layoutManager) {
            is StaggeredGridLayoutManager -> {
                val positions = layoutManager.findLastVisibleItemPositions(null)
                lastVisibleItem = getLastVisibleItem(positions)
            }
            is GridLayoutManager -> {
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisibleItem = layoutManager.findLastVisibleItemPosition()
            }
        }


        if (!isLoading && totalItem <= lastVisibleItem + visibleThreshold) {
            loadMoreListener()
            isLoading = true
        }
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int = lastVisibleItemPositions.max() ?: 0

}