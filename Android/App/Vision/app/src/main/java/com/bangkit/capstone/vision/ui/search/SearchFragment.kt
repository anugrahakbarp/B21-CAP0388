package com.bangkit.capstone.vision.ui.search

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentSearchBinding
import com.bangkit.capstone.vision.ui.ReportAdapter

class SearchFragment : Fragment() {

    private lateinit var fragmentSearchBinding: FragmentSearchBinding

    private lateinit var viewModel: SearchViewModel

    private lateinit var reportAdapter: ReportAdapter

    private lateinit var address: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        fragmentSearchBinding =
            FragmentSearchBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[SearchViewModel::class.java]

        reportAdapter = ReportAdapter()

        with(fragmentSearchBinding.rvSearchReport) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
            setHasFixedSize(true)
        }

        viewModel.getSearchReports().observe(viewLifecycleOwner, {
            if (it != null) {
                reportAdapter.setReports(it)
                fragmentSearchBinding.imgNoData.visibility = View.GONE
            }
        })

        return fragmentSearchBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.queryHint = resources.getString(R.string.search_address)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                address = query

                fragmentSearchBinding.progressBar.visibility = View.VISIBLE
                viewModel.setSearchReports(
                    address,
                    requireContext(),
                    fragmentSearchBinding
                )
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                return false
            }
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val search = menu.findItem(R.id.search)
        search.isVisible = true
        val searchView = search.actionView as SearchView
        val closeBtn = searchView.findViewById<View>(androidx.appcompat.R.id.search_close_btn)
        closeBtn.setOnClickListener {
            searchView.setQuery("", false)
            reportAdapter.setReports(emptyList())
            fragmentSearchBinding.imgNoData.visibility = View.VISIBLE
        }
    }
}