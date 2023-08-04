package infinuma.android.shows.ui.show_details

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.R
import infinuma.android.shows.ShowsApplication
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.model.networking.response.Show
import infinuma.android.shows.ui.ShowsViewModelFactory
import infinuma.android.shows.ui.authentication.LoginFragment
import infinuma.android.shows.ui.authentication.PREFERENCES_NAME
import infinuma.android.shows.util.MyRequestListener
import infinuma.android.shows.util.isInternetAvailable

class ShowDetailsFragment : Fragment() {

    private lateinit var adapter: ShowDetailsAdapter

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val showDetailsViewModel: ShowDetailsViewModel by viewModels {
        ShowsViewModelFactory((requireContext().applicationContext as ShowsApplication).database)
    }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var accessToken: String
    private lateinit var client: String
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        getSessionInfo()
        handleApiRequests()
        showDetailsViewModel.setImageDir(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
    }

    private fun getSessionInfo() {
        accessToken = sharedPreferences.getString(LoginFragment.ACCESS_TOKEN, "")!!
        client = sharedPreferences.getString(LoginFragment.CLIENT, "")!!
        uid = sharedPreferences.getString(LoginFragment.UID, "")!!
    }

    private fun handleApiRequests() {
        showDetailsViewModel.getShowInfo(args.showId, isInternetAvailable(requireContext()))
        showDetailsViewModel.getReviews(args.showId, isInternetAvailable(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun init() {
        with(requireActivity() as AppCompatActivity) {
            with(binding) {

                //build the dialog only once so multiple instances can't be created at the same time
                val dialog = buildDialog()
                reviewButton.setOnClickListener {
                    dialog.show()
                }
                setSupportActionBar(toolAppBar)

                toolAppBar.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = ""
        }
        displayShowDetails()
        displayReviews()
    }

    private fun displayShowDetails() {
        showDetailsViewModel.showLiveData.observe(viewLifecycleOwner) { show ->
            if (show != null) { //in case live data value is not set yet
                setShowDisplayValues(show)
            }
        }
        showDetailsViewModel.showFetchSuccessLiveData.observe(viewLifecycleOwner) { fetchSuccess ->
            if (!fetchSuccess) {
                Snackbar.make(binding.root, R.string.shows_fetch_fail, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setShowDisplayValues(show: Show) {
        with(binding) {
            description.text = show.description
            showTitle.text = show.title
            ratingBar.rating = show.averageRating ?: 0f
            reviewStats.text = getString(R.string.d_reviews_f_average, show.noOfReviews, show.averageRating ?: 0f)
            Glide
                .with(requireContext())
                .load(show.imageUrl)
                .placeholder(R.drawable.white_background)
                .listener(MyRequestListener(loadingSpinner))
                .into(showImage)
        }
    }

    private fun displayReviews() {
        with(binding) {
            adapter = ShowDetailsAdapter(emptyList())
            reviewRecyclerView.adapter = adapter
            showDetailsViewModel.reviewsLiveData.observe(viewLifecycleOwner) { reviews ->
                if (!reviews.isNullOrEmpty()) {
                    adapter.updateData(reviews)
                    if (noReviewsMessage.isVisible) {
                        reviewsPresentDisplay.isVisible = true
                        noReviewsMessage.isVisible = false
                    }
                } else {
                    reviewsPresentDisplay.isVisible = false
                    noReviewsMessage.isVisible = true
                }
            }
            showDetailsViewModel.reviewAddedLiveData.observe(viewLifecycleOwner) { reviewAdded ->
                if (!reviewAdded) {
                    Snackbar.make(binding.root, R.string.review_add_fail, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun buildDialog(): BottomSheetDialog {
        val dialog = BottomSheetDialog(requireContext())
        val dialogAddReviewBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogAddReviewBinding.root)

        //rating is required so it can never be less than 1
        dialogAddReviewBinding.ratingBar.setOnRatingBarChangeListener { _, _, _ ->
            if (dialogAddReviewBinding.ratingBar.rating < 1) dialogAddReviewBinding.ratingBar.rating = 1f
        }

        dialogAddReviewBinding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogAddReviewBinding.submitButton.setOnClickListener {
            val rating = dialogAddReviewBinding.ratingBar.rating.toInt()
            val review = dialogAddReviewBinding.reviewInput.text.toString().trim()
            showDetailsViewModel.addReview(args.showId, rating, review)
            dialog.dismiss()
        }
        return dialog
    }

}