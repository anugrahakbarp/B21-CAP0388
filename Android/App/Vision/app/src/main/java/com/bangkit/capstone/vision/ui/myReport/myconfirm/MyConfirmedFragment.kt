package com.bangkit.capstone.vision.ui.myReport.myconfirm

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentConfirmedBinding
import com.bangkit.capstone.vision.ui.ReportAdapter
import com.bangkit.capstone.vision.ui.UserPreference
import com.bangkit.capstone.vision.ui.myReport.MyReportViewModel

class MyConfirmedFragment : Fragment() {

    private lateinit var fragmentConfirmedBinding: FragmentConfirmedBinding

    private lateinit var viewModel: MyReportViewModel

    private lateinit var reportAdapter: ReportAdapter

    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        fragmentConfirmedBinding =
            FragmentConfirmedBinding.inflate(layoutInflater, container, false)

        val mUserPreference = UserPreference(requireActivity())
        val mUserModel = mUserPreference.getUser()
        username = mUserModel.username!!

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MyReportViewModel::class.java]

        reportAdapter = ReportAdapter()

        fragmentConfirmedBinding.progressBar.visibility = View.VISIBLE
        with(fragmentConfirmedBinding.rvConfirmedReport) {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
            setHasFixedSize(true)
        }

        viewModel.getMyConfirmedReportsDesc(username, fragmentConfirmedBinding)
            .observe(viewLifecycleOwner, {
                reportAdapter.setReports(it)
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
                viewModel.getMyConfirmedReportsDesc(
                    username,
                    fragmentConfirmedBinding
                ).observe(viewLifecycleOwner, { list ->
                    reportAdapter.setReports(list)
                    fragmentConfirmedBinding.progressBar.visibility = View.GONE
                })
            }
            R.id.sortOldest -> {
                fragmentConfirmedBinding.progressBar.visibility = View.VISIBLE
                viewModel.getMyConfirmedReports(
                    username,
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