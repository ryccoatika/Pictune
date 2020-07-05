package com.ryccoatika.pictune.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ryccoatika.pictune.R
import com.ryccoatika.pictune.adapter.UnsplashSearchCategoriesAdapter
import com.ryccoatika.pictune.search.activity.UnsplashSearchActivity
import kotlinx.android.synthetic.main.fragment_unsplash_search.*

/**
 * A simple [Fragment] subclass.
 */
class UnsplashSearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_unsplash_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()

        val titleCategories = resources.getStringArray(R.array.category_entries)
        val photoCategories = resources.obtainTypedArray(R.array.category_values)
        val adapter = UnsplashSearchCategoriesAdapter(titleCategories, photoCategories)
        unsplash_search_category_rv.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("190401", "search:oncreateoptionsmenu")
        menu.clear()
        inflater.inflate(R.menu.unsplash_search_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.unsplash_search_button -> {
                startActivity(Intent(requireContext(), UnsplashSearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
