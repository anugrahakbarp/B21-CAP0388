package com.bangkit.capstone.vision.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.vision.databinding.ItemsReportBinding
import com.bangkit.capstone.vision.model.ReportEntity
import com.bangkit.capstone.vision.ui.detail.DetailReportActivity
import com.bangkit.capstone.vision.utils.DateUtils

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.LiveViewHolder>() {

    private var listReports = ArrayList<ReportEntity>()

    fun setReports(reports: List<ReportEntity>) {
        this.listReports.clear()
        this.listReports.addAll(reports)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveViewHolder {
        val itemsReportBinding =
            ItemsReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LiveViewHolder(itemsReportBinding)
    }

    override fun onBindViewHolder(holder: LiveViewHolder, position: Int) {
        val report = listReports[position]
        holder.bind(report)
    }

    override fun getItemCount(): Int = listReports.size

    class LiveViewHolder(private val binding: ItemsReportBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reports: ReportEntity) {
            with(binding) {
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailReportActivity::class.java)
                    intent.putExtra(DetailReportActivity.EXTRA_REPORT, reports)
                    itemView.context.startActivity(intent)
                }
                tvItemReport.text = reports.address
                tvItemDistance.text = "${reports.distance} from your location"
                tvItemDate.text = DateUtils.getFromDate(reports.time)
            }
        }
    }
}