package com.bangkit.capstone.vision.ui.allReport

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.ui.allReport.confirm.ConfirmedFragment
import com.bangkit.capstone.vision.ui.allReport.finish.FinishedFragment

class AllReportPagerAdapter(private val mContext: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.title_confirm, R.string.title_finish)
    }

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> ConfirmedFragment()
            1 -> FinishedFragment()
            else -> Fragment()
        }

    override fun getPageTitle(position: Int): CharSequence =
        mContext.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = 2

}