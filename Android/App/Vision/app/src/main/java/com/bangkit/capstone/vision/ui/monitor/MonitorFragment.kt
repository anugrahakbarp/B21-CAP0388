package com.bangkit.capstone.vision.ui.monitor

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentMonitorBinding
import com.bangkit.capstone.vision.ui.UserPreference
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class MonitorFragment : Fragment() {

    private lateinit var fragmentMonitorBinding: FragmentMonitorBinding

    private lateinit var viewModel: MonitorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMonitorBinding =
            FragmentMonitorBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[MonitorViewModel::class.java]

        val mUserPreference = UserPreference(requireContext())
        val mUserModel = mUserPreference.getUser()
        val username = mUserModel.username.toString()

        val allChart = fragmentMonitorBinding.allChart
        val myChart = fragmentMonitorBinding.myChart

        setAllChart(allChart)
        setMyChart(myChart, username)

        return fragmentMonitorBinding.root
    }

    private fun setAllChart(chart: BarChart) {
        val confirmList = ArrayList<BarEntry>()
        val finishList = ArrayList<BarEntry>()

        viewModel.getAllMonitorReports(fragmentMonitorBinding)
            .observe(viewLifecycleOwner, {

                val legend = chart.legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)

                confirmList.add(BarEntry(1f, it[0].toFloat()))
                finishList.add(BarEntry(2f, it[1].toFloat()))

                val confirmDataSet = BarDataSet(confirmList, getString(R.string.title_confirm))
                confirmDataSet.color = ContextCompat.getColor(requireContext(), R.color.red)
                confirmDataSet.valueTextColor = Color.BLACK

                val finishDataSet = BarDataSet(finishList, getString(R.string.title_finish))
                finishDataSet.color = ContextCompat.getColor(requireContext(), R.color.green)
                finishDataSet.valueTextColor = Color.BLACK

                val barData = BarData(confirmDataSet, finishDataSet)
                chart.data = barData
                chart.axisLeft.granularity = 1.0f
                chart.axisLeft.isGranularityEnabled = true
                chart.xAxis.setDrawLabels(false)
                chart.xAxis.setDrawGridLines(false)
                chart.axisRight.setDrawLabels(false)
                chart.axisRight.setDrawGridLines(false)
                chart.description.isEnabled = false
                chart.animateX(1000)

                fragmentMonitorBinding.progressBar.visibility = View.GONE
            })
    }

    private fun setMyChart(chart: BarChart, username: String) {
        val confirmList = ArrayList<BarEntry>()
        val finishList = ArrayList<BarEntry>()
        val pendingList = ArrayList<BarEntry>()
        val rejectList = ArrayList<BarEntry>()

        viewModel.getMyMonitorReports(fragmentMonitorBinding, username)
            .observe(viewLifecycleOwner, {

                val legend = chart.legend
                legend.isEnabled = true
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                legend.orientation = Legend.LegendOrientation.HORIZONTAL
                legend.setDrawInside(false)

                confirmList.add(BarEntry(1f, it[0].toFloat()))
                finishList.add(BarEntry(2f, it[1].toFloat()))
                pendingList.add(BarEntry(3f, it[2].toFloat()))
                rejectList.add(BarEntry(4f, it[3].toFloat()))

                val confirmDataSet = BarDataSet(confirmList, getString(R.string.title_confirm))
                confirmDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)
                confirmDataSet.valueTextColor = Color.BLACK

                val finishDataSet = BarDataSet(finishList, getString(R.string.title_finish))
                finishDataSet.color = ContextCompat.getColor(requireContext(), R.color.green)
                finishDataSet.valueTextColor = Color.BLACK

                val pendingDataSet = BarDataSet(pendingList, getString(R.string.title_pending))
                pendingDataSet.color = ContextCompat.getColor(requireContext(), R.color.red)
                pendingDataSet.valueTextColor = Color.BLACK

                val rejectDataSet = BarDataSet(rejectList, getString(R.string.title_reject))
                rejectDataSet.color = ContextCompat.getColor(requireContext(), R.color.label)
                rejectDataSet.valueTextColor = Color.BLACK

                val barData = BarData(confirmDataSet, finishDataSet, pendingDataSet, rejectDataSet)
                chart.data = barData
                chart.axisLeft.granularity = 1.0f
                chart.axisLeft.isGranularityEnabled = true
                chart.xAxis.setDrawLabels(false)
                chart.xAxis.setDrawGridLines(false)
                chart.axisRight.setDrawLabels(false)
                chart.axisRight.setDrawGridLines(false)
                chart.description.isEnabled = false
                chart.animateX(1000)

                fragmentMonitorBinding.progressBar.visibility = View.GONE
            })
    }
}