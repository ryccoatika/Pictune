package com.ryccoatika.core.ui

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecycleViewLoadMoreScroll : RecyclerView.OnScrollListener {
    var isLoading = false
    private var visibleThreshold = 1
    private var totalItem = 0
    private var lastVisibleItem = 0
    private var loadMoreListener: (() -> Unit)? = null
    var layoutManager: RecyclerView.LayoutManager? = null

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

        if (isLoading) return

        layoutManager?.let {
            totalItem = it.itemCount

            when (it) {
                is StaggeredGridLayoutManager -> {
                    val positions = it.findLastVisibleItemPositions(null)
                    lastVisibleItem = getLastVisibleItem(positions)
                }
                is GridLayoutManager -> {
                    lastVisibleItem = it.findLastVisibleItemPosition()
                }
                is LinearLayoutManager -> {
                    lastVisibleItem = it.findLastVisibleItemPosition()
                }
            }

            if (totalItem <= lastVisibleItem + visibleThreshold) {
                loadMoreListener?.invoke()
                isLoading = true
            }
        }
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int =
        lastVisibleItemPositions.maxOrNull() ?: 0

}