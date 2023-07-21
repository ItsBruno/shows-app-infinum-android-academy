package infinuma.android.shows.ui.all_shows

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogProfileBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.model.shows
import infinuma.android.shows.ui.login.LoginFragment
import infinuma.android.shows.ui.login.LoginFragment.Companion.USER_EMAIL
import infinuma.android.shows.ui.login.PREFERENCES_NAME

class ShowsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private var containsShows = true

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initShowsRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListeners() {
        with(binding) {
            noShows.setOnClickListener {
                toggleRecyclerView()
            }

            profile.setOnClickListener {
                buildProfileBottomSheetDialog()
            }
        }
    }

    private fun buildProfileBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogProfileBinding = DialogProfileBinding.inflate(layoutInflater)
        dialog.setContentView(dialogProfileBinding.root)

        with(dialogProfileBinding) {
            userEmail.text = sharedPreferences.getString(USER_EMAIL, "")
            changeProfilePictureButton.setOnClickListener {
                //TODO add open camera and select from gallery functionality
            }

            logoutButton.setOnClickListener {
                initLogoutAlertDialog(dialog)
            }
        }
        dialog.show()
    }

    private fun initLogoutAlertDialog(dialog: BottomSheetDialog) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_logout))
            .setMessage(R.string.logout_confirmation_message)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { _, _ -> logoutUser(dialog) })
            .show()
    }

    private fun logoutUser(dialog: BottomSheetDialog) {
        sharedPreferences.edit {
            putBoolean(LoginFragment.REMEMBER_USER, false)
            putString(LoginFragment.USER_EMAIL, null)
            putString(LoginFragment.USER_PASSWORD, null)
        }
        dialog.dismiss()
        findNavController().navigate(ShowsFragmentDirections.actionShowDetailsFragmentToLoginFragment())
    }

    private fun toggleRecyclerView() {
        with(binding) {
            if (containsShows) {
                recyclerView.visibility = View.GONE
                noShowsDisplay.visibility = View.VISIBLE
                noShows.text = getString(R.string.get_shows)
            } else {
                recyclerView.visibility = View.VISIBLE
                noShowsDisplay.visibility = View.GONE
                noShows.text = getString(R.string.no_shows)
            }
        }
        containsShows = !containsShows
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show.id)
            findNavController().navigate(direction)
        }
        binding.recyclerView.adapter = adapter
    }
}