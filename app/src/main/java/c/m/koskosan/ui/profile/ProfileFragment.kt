package c.m.koskosan.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import c.m.koskosan.R
import c.m.koskosan.data.model.UserResponse
import c.m.koskosan.databinding.FragmentProfileBinding
import c.m.koskosan.ui.about.AboutActivity
import c.m.koskosan.ui.form.update.user.profile.UpdateUserProfileActivity
import c.m.koskosan.ui.login.LoginActivity
import c.m.koskosan.util.gone
import c.m.koskosan.util.invisible
import c.m.koskosan.util.visible
import c.m.koskosan.vo.ResponseState
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {
    private val profileViewModel: ProfileViewModel by viewModel()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // appbar title setup
        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbarProfile)
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.profile)
        }
        setHasOptionsMenu(true)

        // show user profile data to view
        initializeGetUserProfileData()

        // swipe refresh function
        binding.profileSwipeRefreshView.setOnRefreshListener {
            binding.profileSwipeRefreshView.isRefreshing = false
            initializeGetUserProfileData()
        }

        // button for open about this application activity
        binding.btnInfoApplication.setOnClickListener {
            val intentAboutActivity = Intent(requireContext(), AboutActivity::class.java)
            startActivity(intentAboutActivity)
        }

        return view
    }

    // function for get user profile data and observing the state of data
    private fun initializeGetUserProfileData() {
        profileViewModel.getUserProfile().observe(viewLifecycleOwner, { response ->
            if (response != null) {
                when (response) {
                    is ResponseState.Error -> showErrorStateView() // show error state view
                    is ResponseState.Loading -> showLoadingStateView() // show loading state view
                    is ResponseState.Success -> {
                        // show success load data state view
                        showSuccessStateView()

                        // set response data to view
                        initializeDataToView(response)
                    }
                }
            } else {
                // show empty data state view
                showEmptyStateView()
            }
        })
    }

    // parsing data to widget view
    @SuppressLint("SetTextI18n")
    private fun initializeDataToView(response: ResponseState<UserResponse>) {
        Glide.with(binding.imgProfile).load(response.data?.imageProfile)
            .placeholder(R.drawable.ic_baseline_person_pin)
            .error(R.drawable.ic_broken_image)
            .into(binding.imgProfile)
        binding.tvName.text = response.data?.name
        binding.tvEmail.text = response.data?.email
        binding.tvAddress.text = getString(R.string.address) + ": " + response.data?.address
        binding.tvPhone.text = getString(R.string.phone) + ": " + response.data?.phoneNumber
    }

    // handle success state of view
    private fun showSuccessStateView() {
        binding.animLoading.gone()
        binding.animError.gone()
        binding.profileLayout.visible()
    }

    // handle error state of view
    private fun showErrorStateView() {
        binding.animError.visible()
        binding.animLoading.gone()
        binding.profileLayout.invisible()
    }

    // handle loading state of view
    private fun showLoadingStateView() {
        binding.profileLayout.invisible()
        binding.animLoading.visible()
        binding.animError.gone()
    }

    // handle empty data state of view
    private fun showEmptyStateView() {
        binding.profileLayout.invisible()
        binding.animLoading.gone()
        binding.animError.gone()
        binding.animEmpty.visible()
    }

    // initialize option menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
    }

    // give a action for menu items
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit_profile -> {
                val intentUpdateUserProfileActivity =
                    Intent(requireContext(), UpdateUserProfileActivity::class.java)
                startActivity(intentUpdateUserProfileActivity)
                true
            }
            R.id.log_out -> {
                // user Log Out
                AuthUI.getInstance()
                    .signOut(requireContext())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Finish this activity
                            requireActivity().finish()

                            // return to login in activity
                            val intentLoginActivity =
                                Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intentLoginActivity)
                        }
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}