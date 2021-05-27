package com.bangkit.capstone.vision.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.bangkit.capstone.vision.R
import com.bangkit.capstone.vision.databinding.FragmentProfileBinding
import com.bangkit.capstone.vision.ui.UserPreference
import com.bangkit.capstone.vision.ui.auth.AuthenticationActivity

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var fragmentProfileBinding: FragmentProfileBinding

    private lateinit var mUserPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        mUserPreference = UserPreference(requireActivity())

        val mUserModel = mUserPreference.getUser()
        val username = mUserModel.username!!

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        )[ProfileViewModel::class.java]

        fragmentProfileBinding =
            FragmentProfileBinding.inflate(layoutInflater, container, false)

        fragmentProfileBinding.tvUsernameProfile.text = username

        val transitionInflater = TransitionInflater.from(requireContext())
        exitTransition = transitionInflater.inflateTransition(R.transition.slide_up)


        viewModel.getUserReports(username, fragmentProfileBinding.progressBar)
            .observe(viewLifecycleOwner, {
                if (it != null) {
                    fragmentProfileBinding.tvCountReportUploaded.text = it.toString()
                    fragmentProfileBinding.progressBar.visibility = View.GONE
                }
            })

        return fragmentProfileBinding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val logout = menu.findItem(R.id.logout)
        logout.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                mUserPreference.removeUser()
                val intent = Intent(activity, AuthenticationActivity::class.java)
                startActivity(intent)
                activity?.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}