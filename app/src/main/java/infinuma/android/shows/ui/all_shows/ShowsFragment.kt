package infinuma.android.shows.ui.all_shows

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.R
import infinuma.android.shows.ShowsApplication
import infinuma.android.shows.databinding.DialogProfileBinding
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.ui.ShowsViewModelFactory
import infinuma.android.shows.ui.authentication.LoginFragment
import infinuma.android.shows.ui.authentication.LoginFragment.Companion.ACCESS_TOKEN
import infinuma.android.shows.ui.authentication.LoginFragment.Companion.CLIENT
import infinuma.android.shows.ui.authentication.LoginFragment.Companion.UID
import infinuma.android.shows.ui.authentication.PREFERENCES_NAME
import infinuma.android.shows.util.FileUtil
import infinuma.android.shows.util.getRealPathFromURI
import infinuma.android.shows.util.isInternetAvailable
import java.io.File
import okio.Path.Companion.toPath

const val PFP_URI_NAME_DECORATOR = "ProfilePicture"

class ShowsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var avatarUri: Uri
    private var file: File? = null
    private lateinit var renamedFile: File
    private lateinit var snapAnImage: ActivityResultLauncher<Uri>
    private lateinit var pickAnImage: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var accessToken: String
    private lateinit var client: String
    private lateinit var uid: String

    private var containsShows = false

    //used to make sure only a single dialog at a time is created
    private var profileDialogClosed = true

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter
    private lateinit var topRatedShowsAdapter: ShowsAdapter

    private val viewModel: ShowsViewModel by viewModels{
        ShowsViewModelFactory((requireContext().applicationContext as ShowsApplication).database)
    }
    private val args by navArgs<ShowsFragmentArgs>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        getSessionInfo()
        handeApiCalls()
        handleProfilePictureChange()
        viewModel.setImageDir(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    private fun getSessionInfo() {
        accessToken = sharedPreferences.getString(ACCESS_TOKEN, "")!!
        client = sharedPreferences.getString(CLIENT, "")!!
        uid = sharedPreferences.getString(UID, "")!!
    }

    private fun handeApiCalls() {
        ApiModule.initRetrofit(requireContext(), accessToken, client, uid)
        val internetAvailable = isInternetAvailable(requireContext())
        Log.i("NETWORK", "Internet available: $internetAvailable")
        viewModel.getShows(internetAvailable)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setProfilePicture(binding.profile)
        viewModel.getMyProfile()

        initListeners()
        initShowsRecyclerAdapter()
        initTopRatedShowsRecyclerAdapter()
        displayShows()
        displayTopRatedShows()
        handleChip()
        setApiResultsObservers()
    }

    private fun handleChip() {
        with(binding) {

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    viewModel.getTopRatedShows(isInternetAvailable(requireContext()))
                    recyclerView.adapter = topRatedShowsAdapter
                    containsShows = !topRatedShowsAdapter.isEmpty()

                } else {
                    viewModel.getShows(isInternetAvailable(requireContext()))
                    recyclerView.adapter = adapter
                    containsShows = !adapter.isEmpty()
                }
                toggleRecyclerView()
            }
        }
    }

    override fun onDestroyView() {
        containsShows = false
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
                sharedPreferences.edit { putString(createLiteral(args.email), renamedFile.path.toString()) }

                uploadProfilePicture()
            } else {
                Log.e("SavePicture", "Picture not saved")
            }
        }
    }

    private fun pickAnImageRegister() {
        pickAnImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val path = getRealPathFromURI(uri, requireContext())
                Log.i("PATH", path.toString())
                sharedPreferences.edit { putString(createLiteral(args.email), path) }
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                requireContext().contentResolver.takePersistableUriPermission(uri, flag)
                uploadProfilePicture()
            }
        }
    }

    private fun uploadProfilePicture() {
        val picturePath = sharedPreferences.getString(createLiteral(args.email), null)?.toPath()
        if (picturePath != null) {
            viewModel.uploadProfilePicture(picturePath.toFile())
        }
    }

    private fun initListeners() {
        with(binding) {
            profile.setOnClickListener {
                if (profileDialogClosed) {
                    profileDialogClosed = false
                    buildProfileBottomSheetDialog()
                }
            }

            val layoutManager = LinearLayoutManager(context)
            recyclerView.layoutManager = layoutManager
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        if(isInternetAvailable(requireContext())) {
                            viewModel.getShows(isInternetAvailable(requireContext()))
                        }
                    }
                }
            })
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
        viewModel.pfpUrlLiveData.observe(viewLifecycleOwner) { pictureUrl ->
            if (pictureUrl != null) {
                Glide.with(requireContext()).load(pictureUrl).placeholder(R.drawable.ic_profile_placeholder).into(imageView)
            }
        }
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
            putString(LoginFragment.ACCESS_TOKEN, null)
            putString(LoginFragment.CLIENT, null)
            putString(LoginFragment.UID, null)
        }
        val direction = ShowsFragmentDirections.actionShowDetailsFragmentToLoginFragment(navFromRegister = false)
        findNavController().navigate(direction)
    }

    private fun toggleRecyclerView() {
        with(binding) {
            if (containsShows) {
                recyclerView.isVisible = true
                noShowsDisplay.isVisible = false
            } else {
                recyclerView.isVisible = false
                noShowsDisplay.isVisible = true
            }
        }
    }

    private fun initShowsRecyclerAdapter() {
        adapter = ShowsAdapter(emptyList()) { show ->
            val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show.id)
            findNavController().navigate(direction)
        }
        binding.recyclerView.adapter = adapter
    }

    private fun initTopRatedShowsRecyclerAdapter() {
        topRatedShowsAdapter = ShowsAdapter(emptyList()) { show ->
            val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show.id)
            findNavController().navigate(direction)
        }
    }

    private fun displayShows() {
        viewModel.showsLiveData.observe(viewLifecycleOwner) { shows ->
            if (shows != null) {
                adapter.updateData(shows)
                containsShows = true
                toggleRecyclerView()
            } else {
                containsShows = false
                toggleRecyclerView()
            }
        }
    }

    private fun displayTopRatedShows() {
        viewModel.showsTopLiveData.observe(viewLifecycleOwner) { shows ->
            if (shows != null) {
                topRatedShowsAdapter.updateData(shows)
                containsShows = true
                toggleRecyclerView()
            } else {
                containsShows = false
                toggleRecyclerView()
            }
        }
    }

    private fun setApiResultsObservers() {
        viewModel.getShowsSuccessLiveData.observe(viewLifecycleOwner) {success ->
            if(!success) {
                makeSnackbar(R.string.get_shows_failed)
            }
        }
        viewModel.getTopShowsSuccessLiveData.observe(viewLifecycleOwner) {success ->
            if(!success) {
                makeSnackbar(R.string.get_top_shows_failed)
            }
        }
        viewModel.getMyProfileSuccessLiveData.observe(viewLifecycleOwner) {success ->
            if(!success) {
                makeSnackbar(R.string.get_profile_failed)
            }
        }
        viewModel.profilePictureUploadSuccessLiveData.observe(viewLifecycleOwner) {success ->
            if(!success) {
                makeSnackbar(R.string.upload_picture_failed)
            }
        }
    }
    private fun makeSnackbar(message: Int) = Snackbar.make(binding.root, getString(message), Snackbar.LENGTH_SHORT).show()
}