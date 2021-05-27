package com.bangkit.capstone.vision.ui.auth

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.ActivityAuthenticationBinding
import com.bangkit.capstone.vision.databinding.FragmentRegisterBinding
import com.bangkit.capstone.vision.utils.DateUtils
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private lateinit var activityAuthenticationBinding: ActivityAuthenticationBinding
    private lateinit var fragmentRegisterBinding: FragmentRegisterBinding
    private lateinit var db: FirebaseFirestore

    companion object {
        private var SNACKBAR_SUCCESS_CODE = "snackbar_success"
        private var SNACKBAR_ERROR_CODE = "snackbar_error"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activityAuthenticationBinding = ActivityAuthenticationBinding.inflate(layoutInflater)
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return fragmentRegisterBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = FirebaseFirestore.getInstance()

        fragmentRegisterBinding.btnRegister.setOnClickListener {
            validateForm()
        }
    }

    private fun validateForm() {
        val username = fragmentRegisterBinding.edtUsername.text.toString().trim()
        val password = fragmentRegisterBinding.edtPassword.text.toString().trim()
        val confirmPassword = fragmentRegisterBinding.edtConfirmPassword.text.toString().trim()
        if (username.isEmpty()) {
            fragmentRegisterBinding.edtUsername.error = getString(R.string.required)
        }
        if (password.isEmpty()) {
            fragmentRegisterBinding.edtPassword.error = getString(R.string.required)
        }
        if (confirmPassword.isEmpty()) {
            fragmentRegisterBinding.edtConfirmPassword.error = getString(R.string.required)
        }
        if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password == confirmPassword) {
                validateUser(username, password)
            } else {
                fragmentRegisterBinding.edtPassword.error =
                    getString(R.string.password_confirm_same)
                fragmentRegisterBinding.edtConfirmPassword.error =
                    getString(R.string.password_confirm_same)
            }
        }
    }

    private fun validateUser(username: String, password: String) {
        val progress = progress()
        progress.show()
        progress.window!!.setLayout(500, 500)
        val userRef = db.collection("users")
        userRef.whereEqualTo("username", username).get().addOnSuccessListener { it ->
            if (it.documents.size > 0) {
                snackBar(
                    fragmentRegisterBinding.root,
                    getString(R.string.username_exist),
                    SNACKBAR_ERROR_CODE
                ).show()
            } else {
                val user = HashMap<String, Any>()
                user["username"] = username
                user["password"] = password
                user["email"] = ""
                user["level"] = "user"
                user["status"] = "active"
                user["last_update"] = DateUtils.getNowDate()
                userRef.add(user).addOnSuccessListener {
                    snackBar(
                        fragmentRegisterBinding.root,
                        getString(R.string.register_success),
                        SNACKBAR_SUCCESS_CODE
                    ).show()
                    clearForm(fragmentRegisterBinding, activityAuthenticationBinding)
                    progress.dismiss()
                }.addOnFailureListener {
                    snackBar(
                        fragmentRegisterBinding.root,
                        getString(R.string.register_failed),
                        SNACKBAR_ERROR_CODE
                    ).show()
                    Log.d("Failure", it.cause.toString())
                    progress.dismiss()
                }
            }
        }
    }

    private fun snackBar(view: View, message: String, status: String): Snackbar {
        val layout = fragmentRegisterBinding.clRegisterValid
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
        builder.setView(R.layout.progress_register)
        builder.setCancelable(false)
        return builder.create()
    }

    private fun clearForm(
        fragmentRegisterBinding: FragmentRegisterBinding,
        activityAuthenticationBinding: ActivityAuthenticationBinding
    ) {
        fragmentRegisterBinding.edtUsername.text.clear()
        fragmentRegisterBinding.edtPassword.text.clear()
        fragmentRegisterBinding.edtConfirmPassword.text.clear()
        activityAuthenticationBinding.viewPager.currentItem = 0
    }
}