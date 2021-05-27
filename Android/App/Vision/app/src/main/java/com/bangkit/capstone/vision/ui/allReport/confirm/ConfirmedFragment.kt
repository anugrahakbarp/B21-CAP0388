package com.bangkit.capstone.vision.ui.allReport.confirm

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentConfirmedBinding
import com.bangkit.capstone.vision.ui.ReportAdapter
import com.bangkit.capstone.vision.ui.allReport.AllReportViewModel

class ConfirmedFragment : Fragment() {

    private lateinit var fragmentConfirmedBinding: FragmentConfirmedBinding

    private lateinit var viewModel: AllReportViewModel
    private lateinit var reportAdapter: ReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fragmentConfirmedBinding =
            FragmentConfirmedBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[AllReportViewModel::class.java]

        reportAdapter = ReportAdapter()

        fragmentConfirmedBinding.progressBar.visibility = View.VISIBLE
        with(fragmentConfirmedBinding.rvConfirmedReport) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
            setHasFixedSize(true)
        }

        viewModel.getAllConfirmedReportsDesc(fragmentConfirmedBinding)
            .observe(viewLifecycleOwner, { list ->
                reportAdapter.setReports(list)
                fragmentConfirmedBinding.progressBar.visibility = View.GONE
            })

        return fragmentConfirmedBinding.root
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
                fragmentConfirmedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getAllConfirmedReportsDesc(
                    fragmentConfirmedBinding
                ).observe(viewLifecycleOwner, { list ->
                    reportAdapter.setReports(list)
                    fragmentConfirmedBinding.progressBar.visibility = View.GONE
                })
            }
            R.id.sortOldest -> {
                fragmentConfirmedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getAllConfirmedReports(
                    fragmentConfirmedBinding
                ).observe(viewLifecycleOwner, { list ->
                    reportAdapter.setReports(list)
                    fragmentConfirmedBinding.progressBar.visibility = View.GONE
                })
            }
        }
        item.isChecked = true
        return super.onOptionsItemSelected(item)
    }
}