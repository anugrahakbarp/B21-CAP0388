package com.bangkit.capstone.vision.ui.auth

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentLoginBinding
import com.bangkit.capstone.vision.ui.MainActivity
import com.bangkit.capstone.vision.ui.UserPreference
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class LoginFragment : Fragment() {

    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var db: FirebaseFirestore

    companion object {
        private var SNACKBAR_SUCCESS_CODE = "snackbar_success"
        private var SNACKBAR_ERROR_CODE = "snackbar_error"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()
        fragmentLoginBinding.btnLogin.setOnClickListener {
            validateForm()
        }
    }

    private fun validateForm() {
        val username = fragmentLoginBinding.edtUsername.text.toString().trim()
        val password = fragmentLoginBinding.edtPassword.text.toString().trim()
        if (username.isEmpty()) {
            fragmentLoginBinding.edtUsername.error = getString(R.string.required)
        }
        if (password.isEmpty()) {
            fragmentLoginBinding.edtPassword.error = getString(R.string.required)
        }
        if (username.isNotEmpty() && password.isNotEmpty()) {
            validateUser(username, password, activity)
        }
    }

    private fun validateUser(username: String, password: String, activity: FragmentActivity?) {
        val progress = progress()
        progress.show()
        progress.window!!.setLayout(500, 500)
        db.collection("users").whereEqualTo("username", username).get().addOnSuccessListener { it ->
            if (it.isEmpty) {
                snackBar(
                    fragmentLoginBinding.root,
                    getString(R.string.user_not_found),
                    SNACKBAR_ERROR_CODE
                ).show()
                progress.dismiss()
            } else {
                it.forEach {
                    if (username == it.data["username"] && password == it.data["password"] && "user" == it.data["level"]) {
                        val preferences = context?.getSharedPreferences(
                            UserPreference.PREFS_NAME,
                            Context.MODE_PRIVATE
                        )
                        val editor = preferences?.edit()
                        editor?.putString(UserPreference.USERNAME, username)
                        editor?.apply()
                        progress.dismiss()
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        snackBar(
                            fragmentLoginBinding.root,
                            getString(R.string.login_incorrect),
                            SNACKBAR_ERROR_CODE
                        ).show()
                        progress.dismiss()
                    }
                }
            }
        }.addOnFailureListener {
            snackBar(
                fragmentLoginBinding.root,
                getString(R.string.login_failed),
                SNACKBAR_ERROR_CODE
            ).show()
            Log.d("Failure", it.cause.toString())
            progress.dismiss()
        }
    }

    private fun snackBar(view: View, message: String, status: String): Snackbar {
        val layout = fragmentLoginBinding.clLoginValid
        val snackBar = Snackbar.make(
            layout, message, Snackbar.LENGTH_LONG
        )
        val viewSnackBar = snackBar.view
        val params: ViewPager.LayoutParams =
            view.layoutParams as ViewPager.LayoutParams
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        view.layoutParams = params
        layout.bringToFront()
        if (status == SNACKBAR_SUCCESS_CODE) {
            viewSnackBar.setBackgroundColor(
                Color.TRANSPARENT
            )
            layout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.all_corner_ten_radius_green, null)
        } else if (status == SNACKBAR_ERROR_CODE) {
            viewSnackBar.setBackgroundColor(
                Color.TRANSPARENT
            )
            layout.background =
                ResourcesCompat.getDrawable(resources, R.drawable.all_corner_ten_radius_red, null)
        }
        return snackBar
    }

    private fun progress(): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        builder.setView(R.layout.progress_login)
        builder.setCancelable(false)
        return builder.create()
    }

}