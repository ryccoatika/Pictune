package com.ryccoatika.pictune.search

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.core.ui.TopicAdapter
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.search.activity.SearchActivity
import com.ryccoatika.pictune.search.topic.TopicActivity
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val topicAdapter = TopicAdapter()
    private val viewModel: SearchViewModel by viewModel()

    private var currentPage = 1
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(rv_topic.layoutManager as StaggeredGridLayoutManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            setHasOptionsMenu(true)
            (requireActivity() as AppCompatActivity).supportActionBar?.let {
                it.show()
                it.title = getString(R.string.search)
            }

            topicAdapter.setHasStableIds(true)
            topicAdapter.setOnClickListener {
                val toTopicDetail = Intent(context, TopicActivity::class.java)
                toTopicDetail.putExtra(TopicActivity.EXTRA_TOPIC_ID, it.id)
                toTopicDetail.putExtra(TopicActivity.EXTRA_TOPIC_TITLE, it.title)
                startActivity(toTopicDetail)
            }

            with(rv_topic) {
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                addOnScrollListener(scrollListener)
                adapter = topicAdapter
            }

            viewModel.viewState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is SearchViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        rv_topic.isVisible = false
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }

                    // discover topic
                    is SearchViewState.Loading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        rv_topic.isVisible = false
                    }
                    is SearchViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                        rv_topic.isVisible = false
                    }
                    is SearchViewState.Success -> {
                        scrollListener.isLoading = false
                        swipe_refresh.isRefreshing = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        rv_topic.isVisible = true
                        topicAdapter.setTopics(state.data)
                        currentPage++
                    }

                    // load more topic
                    is SearchViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        topicAdapter.showLoading()
                    }
                    is SearchViewState.LoadMoreEmpty -> {
                        topicAdapter.hideLoading()
                    }
                    is SearchViewState.LoadMoreSuccess -> {
                        currentPage++
                        topicAdapter.hideLoading()
                        scrollListener.isLoading = false
                        topicAdapter.insertTopics(state.data)
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMoreTopic(page = currentPage)
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.discoverTopic()
            }

            viewModel.discoverTopic()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.clear()
        activity?.menuInflater?.inflate(R.menu.search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                startActivity(Intent(requireContext(), SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
