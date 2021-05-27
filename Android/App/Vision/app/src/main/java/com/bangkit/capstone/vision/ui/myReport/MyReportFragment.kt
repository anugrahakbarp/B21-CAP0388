package com.bangkit.capstone.vision.ui.myReport

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bangkit.capstone.vision.databinding.FragmentMyReportBinding


class MyReportFragment : Fragment() {

    private lateinit var fragmentMyReportBinding: FragmentMyReportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMyReportBinding =
            FragmentMyReportBinding.inflate(layoutInflater, container, false)

        val pagerAdapter = MyReportPagerAdapter(requireContext(), childFragmentManager)

        fragmentMyReportBinding.pagerMyReport.adapter = pagerAdapter
        fragmentMyReportBinding.tabStatusMyReport.setupWithViewPager(fragmentMyReportBinding.pagerMyReport)

        return fragmentMyReportBinding.root
    }
}