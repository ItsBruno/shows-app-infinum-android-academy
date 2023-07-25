package infinuma.android.shows.ui.all_shows

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogProfileBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.ui.login.LoginFragment
import infinuma.android.shows.ui.login.PREFERENCES_NAME
import infinuma.android.shows.util.FileUtil
import java.io.File

const val PFP_URI_NAME_DECORATOR = "ProfilePicture"

class ShowsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var avatarUri: Uri
    private var file: File? = null
    private lateinit var renamedFile: File
    private lateinit var galleryPictureUri: Uri
    private lateinit var snapAnImage: ActivityResultLauncher<Uri>
    private lateinit var pickAnImage: ActivityResultLauncher<PickVisualMediaRequest>

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

    private fun createLiteral(email: String): String {
        return "${email}${PFP_URI_NAME_DECORATOR}"
    }

    private fun handleProfilePictureChange() {
        snapAnImageRegister()
        pickAnImageRegister()
    }

    private fun snapAnImageRegister() {

        snapAnImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { pictureSaved ->
            if (pictureSaved) {
                //resize and adjust the image
                FileUtil.getImageFile(requireContext())

                //rename the file so profile pictures for different emails can be saved
                renamedFile = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "${args.email.replace("@", "").replace(".", "")}${System.currentTimeMillis()}${PFP_URI_NAME_DECORATOR}.jpg"
                )
                //file is never null since the snapAnImage.launch() is only called if the file was created successfully
                file!!.renameTo(renamedFile)

                //remember the profile picture for the given email
                sharedPreferences.edit { putString(createLiteral(args.email), renamedFile.toUri().toString()) }

                setProfilePicture(binding.profile)

            } else {
                Log.e("SavePicture", "Picture not saved")
            }
        }
    }

    private fun pickAnImageRegister() {
        pickAnImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                sharedPreferences.edit { putString(createLiteral(args.email), uri.toString()) }

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
                showPictureChangeAlertDialog()
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
        val pictureUri = sharedPreferences.getString(createLiteral(args.email), null)?.toUri()
        if (pictureUri != null) {
            //.setImageURI could be used, but if the user changes photo by camera two times in a row the image will have the same uri
            // and the imageView will not recognize that the image has changed
            //imageView.setImageDrawable(Drawable.createFromPath(pictureUri.path))
            Glide.with(requireContext()).load(pictureUri).into(imageView)
        }
        else(imageView.setImageResource(R.drawable.ic_profile_placeholder))
    }

    private fun showPictureChangeAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.select_method)
            .setMessage(R.string.picture_change_instruction)
            .setPositiveButton(R.string.camera) { _: DialogInterface, _: Int -> takeANewProfilePicture() }
            .setNegativeButton(R.string.gallery) { _: DialogInterface, _: Int -> chooseProfilePictureFromGallery() }
            .show()
    }

    private fun chooseProfilePictureFromGallery() {
        pickAnImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun takeANewProfilePicture() {
        file = FileUtil.createImageFile(requireContext())
        //if file is null uri won't be initialized
        if (file != null) {
            avatarUri = FileProvider.getUriForFile(requireContext(), "${requireContext().applicationContext.packageName}.provider", file!!)
        }
        if (this::avatarUri.isInitialized) {
            snapAnImage.launch(avatarUri)
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
        }
        findNavController().navigate(ShowsFragmentDirections.actionShowDetailsFragmentToLoginFragment())
    }

    private fun toggleRecyclerView() {
        with(binding) {
            if (containsShows) {
                recyclerView.isVisible = false
                noShowsDisplay.isVisible = true
                noShows.text = getString(R.string.get_shows)
            } else {
                recyclerView.isVisible = true
                noShowsDisplay.isVisible = false
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