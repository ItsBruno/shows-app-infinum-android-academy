package infinuma.android.shows.ui.all_shows

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
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
import infinuma.android.shows.model.ProfilePictureMethod
import infinuma.android.shows.ui.login.LoginFragment
import infinuma.android.shows.ui.login.PREFERENCES_NAME
import infinuma.android.shows.util.FileUtil

const val PROFILE_PICTURE_CHANGED = "PROFILE_PICTURE_CHANGED"
const val PROFILE_PICTURE_METHOD = "PROFILE_PICTURE_METHOD"
const val PROFILE_PICTURE_URI = "PROFILE_PICTURE_URI"

class ShowsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var avatarUri: Uri
    private lateinit var galleryPictureUri: Uri
    private lateinit var snapAnImage: ActivityResultLauncher<Uri>
    private lateinit var pickAnImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var profilePictureMethod: ProfilePictureMethod

    private var containsShows = true
    private var profilePictureChanged = false

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
        setProfilePicture(binding.profile)

        initListeners()
        initShowsRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleProfilePictureChange() {
        profilePictureChanged = sharedPreferences.getBoolean(PROFILE_PICTURE_CHANGED, false)
        profilePictureMethod = ProfilePictureMethod.valueOf(
            sharedPreferences.getString(PROFILE_PICTURE_METHOD, ProfilePictureMethod.NONE.toString()) ?: "NONE"
        )
        galleryPictureUri = Uri.parse(sharedPreferences.getString(PROFILE_PICTURE_URI, ""))

        snapAnImageRegister()
        pickAnImageRegister()
    }

    private fun snapAnImageRegister() {
        val file = FileUtil.createImageFile(requireContext())

        //if file is null uri won't be initialized
        if (file != null) {
            avatarUri = FileProvider.getUriForFile(requireContext(), "${requireContext().applicationContext.packageName}.provider", file)
        }
        snapAnImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { pictureSaved ->
            if (pictureSaved) {
                sharedPreferences.edit { putBoolean(PROFILE_PICTURE_CHANGED, true) }
                sharedPreferences.edit { putString(PROFILE_PICTURE_METHOD, ProfilePictureMethod.CAMERA.toString()) }
                profilePictureChanged = true
                profilePictureMethod = ProfilePictureMethod.CAMERA
                setProfilePicture(binding.profile)
            } else {
                Log.e("SavePicture", "Picture not saved")
            }
        }
    }

    private fun pickAnImageRegister() {
        pickAnImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                sharedPreferences.edit { putBoolean(PROFILE_PICTURE_CHANGED, true) }
                sharedPreferences.edit { putString(PROFILE_PICTURE_METHOD, ProfilePictureMethod.GALLERY.toString()) }
                sharedPreferences.edit { putString(PROFILE_PICTURE_URI, uri.toString()) }
                galleryPictureUri = uri
                profilePictureChanged = true
                profilePictureMethod = ProfilePictureMethod.GALLERY
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                setProfilePicture(binding.profile)
            }
        }
    }

    private fun initListeners() {
        with(binding) {
            noShows.setOnClickListener {
                toggleRecyclerView()
            }

            profile.setOnClickListener {
                if (profileDialogClosed) {
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
                initPictureChangeAlertDialog()
                dialog.dismiss()
            }

            logoutButton.setOnClickListener {
                initLogoutAlertDialog()
                dialog.dismiss()
            }
            setProfilePicture(dialogProfileBinding.profilePicture)
        }

        dialog.setContentView(dialogProfileBinding.root)
        dialog.show()
    }

    private fun setProfilePicture(imageView: ImageView) {
        if (profilePictureChanged) {
            when (profilePictureMethod) {
                ProfilePictureMethod.CAMERA -> {
                    val file = FileUtil.getImageFile(requireContext())
                    imageView.setImageDrawable(Drawable.createFromPath(file?.path))
                }

                ProfilePictureMethod.GALLERY -> imageView.setImageURI(galleryPictureUri)
                else -> imageView.setImageResource(R.drawable.ic_profile_placeholder)
            }

        }
    }

    private fun initPictureChangeAlertDialog() {
        AlertDialog.Builder(requireContext()).setTitle(R.string.select_method).setMessage(R.string.picture_change_instruction)
            .setPositiveButton(R.string.camera) { _: DialogInterface, _: Int -> takeANewProfilePicture() }
            .setNegativeButton(R.string.gallery) { _: DialogInterface, _: Int -> chooseProfilePictureFromGallery() }.show()
    }

    private fun chooseProfilePictureFromGallery() {
        pickAnImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun takeANewProfilePicture() {
        if (this::avatarUri.isInitialized) {
            snapAnImage.launch(avatarUri)
        }
    }

    private fun initLogoutAlertDialog() {
        AlertDialog.Builder(requireContext()).setTitle(getString(R.string.confirm_logout)).setMessage(R.string.logout_confirmation_message)
            .setNegativeButton(R.string.no, null).setPositiveButton(R.string.yes, DialogInterface.OnClickListener { _, _ -> logoutUser() })
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
        viewModel.showsLivedData.observe(viewLifecycleOwner) { shows ->
            adapter = ShowsAdapter(shows) { show ->
                val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show.id)
                findNavController().navigate(direction)
            }
            binding.recyclerView.adapter = adapter
        }
    }
}