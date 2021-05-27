package com.bangkit.capstone.vision.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.capstone.vision.databinding.ActivityAuthenticationBinding
import com.bangkit.capstone.vision.model.UserModel
import com.bangkit.capstone.vision.ui.MainActivity
import com.bangkit.capstone.vision.ui.UserPreference

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var mUserPreference: UserPreference
    private lateinit var mUserModel: UserModel

    private lateinit var activityAuthenticationBinding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAuthenticationBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(activityAuthenticationBinding.root)
        supportActionBar?.hide()

        mUserPreference = UserPreference(this)
        showExistingPreference()

        val sectionsPagerAdapter = AuthenticationPagerAdapter(this, supportFragmentManager)

        activityAuthenticationBinding.viewPager.adapter = sectionsPagerAdapter
        activityAuthenticationBinding.tabs.setupWithViewPager(activityAuthenticationBinding.viewPager)
    }

    private fun showExistingPreference() {
        mUserModel = mUserPreference.getUser()
        if (mUserModel.username?.isNotEmpty() == true) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}