package infinuma.android.shows.ui.show_details

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.model.Show
import infinuma.android.shows.model.ShowReview
import infinuma.android.shows.ui.all_shows.ShowsViewModel

class ShowDetailsFragment : Fragment() {

    private lateinit var adapter: ShowDetailsAdapter

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private lateinit var showId: String

    private val showsViewModel by viewModels<ShowsViewModel>()
    private val showDetailsViewModel by viewModels<ShowDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        showId = args.showId
        with(requireActivity() as AppCompatActivity){
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
        setRatingObservers()

    }

    private fun displayShowDetails() {
        showsViewModel.getShow(showId).observe(viewLifecycleOwner) {show ->
            setShowDisplayValues(show)
        }
    }
    private fun setShowDisplayValues(show: Show) {
        with(binding) {
            description.text = show.description
            showTitle.text = show.title
            showImage.setImageResource(show.imageResourceId)
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
            showDetailsViewModel.addReview(showId, ShowReview(R.drawable.ic_profile_placeholder, getString(R.string.unknown), rating, review))
            dialog.dismiss()
        }
        return dialog
    }

    private fun setRatingObservers() {
        showDetailsViewModel.getReviews(showId).observe(viewLifecycleOwner) {showReviews ->
            initRecyclerView(showReviews)
            if (showReviews.size <= 1) toggleShowsDisplay(showReviews)

            adapter.notifyItemChanged(showReviews.size - 1)
            showDetailsViewModel.getAverageRating(showId).observe(viewLifecycleOwner){averageRating ->
                showDetailsViewModel.getNumberOfReviews(showId).observe(viewLifecycleOwner) {numOfReviews ->
                    binding.reviewStats.text = getString(R.string.d_reviews_f_average, numOfReviews, averageRating)
                }
                binding.ratingBar.rating = averageRating
            }
        }
    }

    private fun toggleShowsDisplay(showReviews: List<ShowReview>) {
        with(binding) {
            if (showReviews.isNotEmpty()) {
                reviewsPresentDisplay.visibility = View.VISIBLE
                noReviewsMessage.visibility = View.GONE
            } else {
                reviewsPresentDisplay.visibility = View.GONE
                noReviewsMessage.visibility = View.VISIBLE
            }
        }
    }
    private fun initRecyclerView(showReviews: List<ShowReview>) {
        with(binding) {
            adapter = ShowDetailsAdapter(showReviews)
            reviewRecyclerView.adapter = adapter
        }
    }
}