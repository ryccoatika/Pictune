package com.ryccoatika.pictune.search.activity.users

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.google.android.material.textview.MaterialTextView
import com.ryccoatika.core.ui.RecycleViewLoadMoreScroll
import com.ryccoatika.core.ui.UserAdapter
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.user.UserActivity
import kotlinx.android.synthetic.main.fragment_user.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchUserFragment(
    private val query: String
) : Fragment() {

    private val userAdapter: UserAdapter = UserAdapter()
    private val viewModel: SearchUserViewModel by viewModel()

    private var currentPage = 1
    private val scrollListener: RecycleViewLoadMoreScroll by lazy {
        RecycleViewLoadMoreScroll(rv_user.layoutManager as LinearLayoutManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (activity != null) {
            // setting staggered grid layout to not blinking and change position

            userAdapter.setOnClickListener {
                val toDetail = Intent(requireContext(), UserActivity::class.java)
                toDetail.putExtra(UserActivity.EXTRA_USER_NAME, it.username)
                startActivity(toDetail)
            }

            with(rv_user) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                addOnScrollListener(scrollListener)
                addItemDecoration(DividerItemDecoration(requireContext(), VERTICAL))
                adapter = userAdapter
            }

            viewModel.viewState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is SearchUserViewState.Error -> {
                        swipe_refresh.isRefreshing = false
                        rv_user.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = true
                        view_error.findViewById<MaterialTextView>(R.id.tv_error).text =
                            state.message ?: getString(R.string.something_went_wrong)
                    }
                    // discover
                    is SearchUserViewState.Loading -> {
                        currentPage = 1
                        scrollListener.isLoading = true
                        if (!swipe_refresh.isRefreshing)
                            view_loading.isVisible = true
                        rv_user.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                    }
                    is SearchUserViewState.Empty -> {
                        swipe_refresh.isRefreshing = false
                        rv_user.isVisible = false
                        view_loading.isVisible = false
                        view_empty.isVisible = true
                        view_error.isVisible = false
                    }
                    is SearchUserViewState.Success -> {
                        swipe_refresh.isRefreshing = false
                        rv_user.isVisible = true
                        view_loading.isVisible = false
                        view_empty.isVisible = false
                        view_error.isVisible = false
                        userAdapter.setUsers(state.data)
                        currentPage++
                        scrollListener.isLoading = false
                    }
                    // loadmore
                    is SearchUserViewState.LoadMoreLoading -> {
                        scrollListener.isLoading = true
                        userAdapter.showLoading()
                    }
                    is SearchUserViewState.LoadMoreEmpty -> {
                        userAdapter.hideLoading()
                    }
                    is SearchUserViewState.LoadMoreSuccess -> {
                        userAdapter.hideLoading()
                        userAdapter.insertUsers(state.data)
                        scrollListener.isLoading = false
                        currentPage++
                    }
                }
            }

            scrollListener.setOnLoadMoreListener {
                viewModel.loadMoreUser(
                    query = query,
                    page = currentPage
                )
            }

            swipe_refresh.setOnRefreshListener {
                viewModel.searchUser(query)
            }
            viewModel.searchUser(query)
        }
    }
}