package com.bangkit.capstone.vision.ui.auth

import android.app.AlertDialog
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
import com.bangkit.capstone.vision.model.UserModel
import com.bangkit.capstone.vision.ui.MainActivity
import com.bangkit.capstone.vision.ui.UserPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class LoginFragment : Fragment() {

    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var mUserPreference: UserPreference
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    companion object {
        private var SNACKBAR_SUCCESS_CODE = "snackbar_success"
        private var SNACKBAR_ERROR_CODE = "snackbar_error"
        const val RC_SIGN_IN = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLoginBinding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        val dbSetting = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        db = FirebaseFirestore.getInstance()
        db.firestoreSettings = dbSetting

        mUserPreference = UserPreference(requireContext())
        mUserPreference.clearUser()

        fragmentLoginBinding.btnLogin.setOnClickListener {
            validateForm()
        }

        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        fragmentLoginBinding.btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }

        return fragmentLoginBinding.root
    }

    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
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
        val account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        } else {
            val progress = progress()
            progress.show()
            progress.window!!.setLayout(500, 500)
            db.collection("users").whereEqualTo("username", username).get()
                .addOnSuccessListener { it ->
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
                                val userModel = UserModel(username)
                                mUserPreference.setUser(userModel)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_SIGN_IN -> {
                val progress = progress()
                progress.show()
                progress.window!!.setLayout(500, 500)
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    val userModel = UserModel(account.email)
                    mUserPreference.setUser(userModel)
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                    progress().dismiss()
                } catch (e: ApiException) {
                    progress().dismiss()
                    snackBar(
                        fragmentLoginBinding.root,
                        getString(R.string.login_failed),
                        SNACKBAR_ERROR_CODE
                    ).show()
                    Log.w("Google Login Failed", "signInResult:failed code=" + e.statusCode)
                }
            }
        }
    }

}
