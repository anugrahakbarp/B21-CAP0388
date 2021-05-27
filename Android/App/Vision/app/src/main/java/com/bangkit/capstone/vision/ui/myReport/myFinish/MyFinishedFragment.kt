package com.bangkit.capstone.vision.ui.myReport.myFinish

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentFinishedBinding
import com.bangkit.capstone.vision.ui.ReportAdapter
import com.bangkit.capstone.vision.ui.UserPreference
import com.bangkit.capstone.vision.ui.myReport.MyReportViewModel

class MyFinishedFragment : Fragment() {

    private lateinit var fragmentFinishedBinding: FragmentFinishedBinding

    private lateinit var viewModel: MyReportViewModel

    private lateinit var reportAdapter: ReportAdapter

    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fragmentFinishedBinding =
            FragmentFinishedBinding.inflate(layoutInflater, container, false)

        val mUserPreference = UserPreference(requireActivity())
        val mUserModel = mUserPreference.getUser()
        username = mUserModel.username!!

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MyReportViewModel::class.java]

        reportAdapter = ReportAdapter()
        reportAdapter.notifyDataSetChanged()
        with(fragmentFinishedBinding.rvFinishedReport) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
            setHasFixedSize(true)
        }

        fragmentFinishedBinding.progressBar.visibility = View.VISIBLE
        viewModel.getMyFinishedReportsDesc(username, fragmentFinishedBinding)
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
                viewModel.getMyFinishedReportsDesc(
                    username,
                    fragmentFinishedBinding
                ).observe(viewLifecycleOwner, { list ->
                    reportAdapter.setReports(list)
                    fragmentFinishedBinding.progressBar.visibility = View.GONE
                })
            }
            R.id.sortOldest -> {
                fragmentFinishedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getMyFinishedReports(username, fragmentFinishedBinding)
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