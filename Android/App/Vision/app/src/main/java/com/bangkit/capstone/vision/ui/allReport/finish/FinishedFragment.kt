package com.bangkit.capstone.vision.ui.allReport.finish

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentFinishedBinding
import com.bangkit.capstone.vision.ui.ReportAdapter
import com.bangkit.capstone.vision.ui.allReport.AllReportViewModel

class FinishedFragment : Fragment() {

    private lateinit var fragmentFinishedBinding: FragmentFinishedBinding

    private lateinit var viewModel: AllReportViewModel

    private lateinit var reportAdapter: ReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fragmentFinishedBinding =
            FragmentFinishedBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[AllReportViewModel::class.java]

        reportAdapter = ReportAdapter()
        reportAdapter.notifyDataSetChanged()
        with(fragmentFinishedBinding.rvFinishedReport) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
            setHasFixedSize(true)
        }

        fragmentFinishedBinding.progressBar.visibility = View.VISIBLE
        viewModel.getAllFinishedReportsDesc(fragmentFinishedBinding)
            .observe(viewLifecycleOwner, { list ->
                reportAdapter.setReports(list)
                fragmentFinishedBinding.progressBar.visibility = View.GONE
            })

        return fragmentFinishedBinding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val newest = menu.findItem(R.id.sortNewest)
        val oldest = menu.findItem(R.id.sortOldest)
        newest.isVisible = true
        oldest.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sortNewest -> {
                fragmentFinishedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getAllFinishedReportsDesc(
                    fragmentFinishedBinding
                ).observe(viewLifecycleOwner, { list ->
                    reportAdapter.setReports(list)
                    fragmentFinishedBinding.progressBar.visibility = View.GONE
                })
            }
            R.id.sortOldest -> {
                fragmentFinishedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getAllFinishedReports(fragmentFinishedBinding)
                    .observe(viewLifecycleOwner, { list ->
                        reportAdapter.setReports(list)
                        fragmentFinishedBinding.progressBar.visibility = View.GONE
                    })
            }
        }
        item.isChecked = true
        return super.onOptionsItemSelected(item)
    }
}