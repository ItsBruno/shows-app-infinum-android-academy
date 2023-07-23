package infinuma.android.shows.ui.all_shows

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogProfileBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.ui.login.LoginFragment
import infinuma.android.shows.ui.login.PREFERENCES_NAME
import infinuma.android.shows.ui.show_details.ShowDetailsFragmentArgs
import infinuma.android.shows.util.FileUtil

const val PICTURE_TAKEN = "PICTURE_TAKEN"
class ShowsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var avatarUri: Uri
    private lateinit var getImage: ActivityResultLauncher<Uri>

    private var containsShows = true
    private var pictureTaken = false

    //used to make sure only a single dialog at a time is created
    private var profileDialogClosed = true

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private val viewModel by viewModels<ShowsViewModel>()
    private val args by navArgs<ShowsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        handleProfilePictureChange()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //if the user has taken a picture to change his profile photo display it, otherwise the placeholder is displayed
        if(pictureTaken) {
            val file = FileUtil.getImageFile(requireContext())
            binding.profile.setImageDrawable(Drawable.createFromPath(file?.path))
        }

        initListeners()
        initShowsRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun handleProfilePictureChange() {
        pictureTaken = sharedPreferences.getBoolean(PICTURE_TAKEN, false)

        var file = FileUtil.createImageFile(requireContext())

        //if file is null uri won't be initialized
        if(file != null) {
            avatarUri = FileProvider.getUriForFile(requireContext(), "${requireContext().applicationContext.packageName}.provider", file)
        }
        getImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { pictureSaved ->
            if (pictureSaved) {
                file = FileUtil.getImageFile(requireContext())
                binding.profile.setImageDrawable(Drawable.createFromPath(file?.path))
                sharedPreferences.edit { putBoolean(PICTURE_TAKEN, true)
                pictureTaken = true}
            } else {
                Log.e("SavePicture", "Picture not saved")
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            noShows.setOnClickListener {
                toggleRecyclerView()
            }

            profile.setOnClickListener {
                if(profileDialogClosed) {
                    profileDialogClosed = false
                    buildProfileBottomSheetDialog()
                }
            }
        }
    }

    private fun buildProfileBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogProfileBinding = DialogProfileBinding.inflate(layoutInflater)

        dialog.setOnDismissListener {
            profileDialogClosed = true
        }

        with(dialogProfileBinding) {
            userEmail.text = args.email
            changeProfilePictureButton.setOnClickListener {
                takeANewProfilePicture()
                dialog.dismiss()
            }

            logoutButton.setOnClickListener {
                initLogoutAlertDialog()
                dialog.dismiss()
            }
            if(pictureTaken) {
                val file = FileUtil.getImageFile(requireContext())
                profilePicture.setImageDrawable(Drawable.createFromPath(file?.path))
            }
        }

        dialog.setContentView(dialogProfileBinding.root)
        dialog.show()
    }

    private fun takeANewProfilePicture() {
        if(this::avatarUri.isInitialized) {
            getImage.launch(avatarUri)
        }
    }

    private fun initLogoutAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.confirm_logout))
            .setMessage(R.string.logout_confirmation_message)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { _, _ -> logoutUser() })
            .show()
    }

    private fun logoutUser() {
        sharedPreferences.edit {
            putBoolean(LoginFragment.REMEMBER_USER, false)
            putString(LoginFragment.USER_EMAIL, null)
            putString(LoginFragment.USER_PASSWORD, null)
        }
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
        viewModel.showsLivedData.observe(viewLifecycleOwner) {shows ->
            adapter = ShowsAdapter(shows) { show ->
                val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show.id)
                findNavController().navigate(direction)
            }
            binding.recyclerView.adapter = adapter
        }
    }
}