package com.bangkit.capstone.vision.ui.myReport.myReject

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentRejectedBinding
import com.bangkit.capstone.vision.ui.ReportAdapter
import com.bangkit.capstone.vision.ui.UserPreference
import com.bangkit.capstone.vision.ui.myReport.MyReportViewModel

class MyRejectedFragment : Fragment() {

    private lateinit var fragmentRejectedBinding: FragmentRejectedBinding

    private lateinit var viewModel: MyReportViewModel

    private lateinit var reportAdapter: ReportAdapter

    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fragmentRejectedBinding = FragmentRejectedBinding.inflate(layoutInflater, container, false)

        val mUserPreference = UserPreference(requireActivity())
        val mUserModel = mUserPreference.getUser()
        username = mUserModel.username!!

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MyReportViewModel::class.java]

        reportAdapter = ReportAdapter()
        reportAdapter.notifyDataSetChanged()
        with(fragmentRejectedBinding.rvRejectedReport) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
            setHasFixedSize(true)
        }

        fragmentRejectedBinding.progressBar.visibility = View.VISIBLE
        viewModel.getMyRejectedReportsDesc(
            username,
            fragmentRejectedBinding
        ).observe(viewLifecycleOwner, { list ->
            reportAdapter.setReports(list)
            fragmentRejectedBinding.progressBar.visibility = View.GONE
        })

        return fragmentRejectedBinding.root
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
                fragmentRejectedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getMyRejectedReportsDesc(
                    username,
                    fragmentRejectedBinding
                ).observe(viewLifecycleOwner, { list ->
                    reportAdapter.setReports(list)
                    fragmentRejectedBinding.progressBar.visibility = View.GONE
                })
            }
            R.id.sortOldest -> {
                fragmentRejectedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getMyRejectedReports(
                    username,
                    fragmentRejectedBinding
                ).observe(viewLifecycleOwner, { list ->
                    reportAdapter.setReports(list)
                    fragmentRejectedBinding.progressBar.visibility = View.GONE
                })
            }
        }
        item.isChecked = true
        return super.onOptionsItemSelected(item)
    }
}