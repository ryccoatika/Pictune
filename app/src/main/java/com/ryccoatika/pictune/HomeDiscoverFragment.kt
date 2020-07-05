package com.ryccoatika.pictune

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_home_discover.*

class HomeDiscoverFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_discover, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()

        val pagerAdapter = HomeDiscoverPagerAdapter(requireContext(), requireActivity().supportFragmentManager)
        home_discover_view_pager.adapter = pagerAdapter
        home_discover_tab_layout.setupWithViewPager(home_discover_view_pager)
        home_discover_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        when (home_discover_view_pager.currentItem) {
            0 -> inflater.inflate(R.menu.discover_photos_filter, menu)
            1 -> inflater.inflate(R.menu.discover_collections_filter, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}
