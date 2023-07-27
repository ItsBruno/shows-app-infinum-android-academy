package infinuma.android.shows.ui.show_details

import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.infinum.academy.playground2023.lecture.networking.ApiModule
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.model.networking.Review
import infinuma.android.shows.model.networking.Show

class ShowDetailsFragment : Fragment() {

    private lateinit var adapter: ShowDetailsAdapter

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val showDetailsViewModel by viewModels<ShowDetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDetailsViewModel.setSessionInfo(args.accessToken, args.client, args.uid)
        handleApiRequests()
    }

    private fun handleApiRequests() {
        ApiModule.initRetrofit(requireContext())
        showDetailsViewModel.getShowInfo(args.showId)
        showDetailsViewModel.getReviews(args.showId, 0)
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
    }

    private fun setShowDisplayValues(show: Show) {
        with(binding) {
            description.text = show.description
            showTitle.text = show.title
            ratingBar.rating = show.averageRating?: 0f
            reviewStats.text = getString(R.string.d_reviews_f_average, show.noOfReviews, show.averageRating?: 0f)
            Glide
                .with(requireContext())
                .load(show.imageUrl)
                .placeholder(R.drawable.white_background)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingSpinner.isVisible = false
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingSpinner.isVisible = false
                        return false
                    }
                })
                .into(showImage)
        }
    }

    private fun displayReviews() {
        with(binding) {
            adapter = ShowDetailsAdapter(emptyList())
            reviewRecyclerView.adapter = adapter
            showDetailsViewModel.reviewsLiveData.observe(viewLifecycleOwner) { reviews ->
                adapter.updateData(reviews)
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
            showDetailsViewModel.getReviews(args.showId, 1000)
            dialog.dismiss()
        }
        return dialog
    }

}