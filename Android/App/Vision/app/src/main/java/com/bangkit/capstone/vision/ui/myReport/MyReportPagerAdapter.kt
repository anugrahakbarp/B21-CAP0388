package com.bangkit.capstone.vision.ui.myReport

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.ui.myReport.myFinish.MyFinishedFragment
import com.bangkit.capstone.vision.ui.myReport.myPending.MyPendingFragment
import com.bangkit.capstone.vision.ui.myReport.myReject.MyRejectedFragment
import com.bangkit.capstone.vision.ui.myReport.myconfirm.MyConfirmedFragment

class MyReportPagerAdapter(private val mContext: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        @StringRes
        private val TAB_TITLES =
            intArrayOf(
                R.string.title_pending,
                R.string.title_confirm,
                R.string.title_finish,
                R.string.title_reject
            )
    }

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> MyPendingFragment()
            1 -> MyConfirmedFragment()
            2 -> MyFinishedFragment()
            3 -> MyRejectedFragment()
            else -> Fragment()
        }

    override fun getPageTitle(position: Int): CharSequence =
        mContext.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = 4

}