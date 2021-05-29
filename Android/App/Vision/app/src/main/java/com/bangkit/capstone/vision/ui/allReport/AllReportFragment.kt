package com.bangkit.capstone.vision.ui.allReport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bangkit.capstone.vision.databinding.FragmentAllReportBinding

class AllReportFragment : Fragment() {

    private lateinit var fragmentAllReportBinding: FragmentAllReportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAllReportBinding =
            FragmentAllReportBinding.inflate(layoutInflater, container, false)

        val pagerAdapter = AllReportPagerAdapter(requireContext(), childFragmentManager)

        fragmentAllReportBinding.pagerAllReport.adapter = pagerAdapter
        fragmentAllReportBinding.tabStatusAllReport.setupWithViewPager(fragmentAllReportBinding.pagerAllReport)

        return fragmentAllReportBinding.root
    }
}