package infinuma.android.shows.ui.show_details

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import infinuma.android.shows.databinding.FragmentShowDetailsBinding
import infinuma.android.shows.model.getAverageRating
import infinuma.android.shows.model.getNumberOfReviews
import infinuma.android.shows.model.reviews
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.DialogAddReviewBinding
import infinuma.android.shows.model.Show
import infinuma.android.shows.model.ShowReview
import infinuma.android.shows.model.shows

class ShowDetailsFragment : Fragment() {

    private lateinit var adapter: ShowDetailsAdapter

    private var _binding: FragmentShowDetailsBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private lateinit var showsTitle: String
    private lateinit var showId: String
    private lateinit var show: Show

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

    private fun init() {
        showId = args.showId

        for (show_iter in shows) {
            if (show_iter.id == showId) {
                show = show_iter
                break
            }
        }

        showsTitle = show.title
        val showDesc = show.description
        val showSrc = show.imageResourceId

        val activity = requireActivity() as AppCompatActivity

        with(binding) {
            description.text = showDesc
            showTitle.text = showsTitle
            showImage.setImageResource(showSrc)

            reviewButton.setOnClickListener {
                buildDialog()
            }
            activity.setSupportActionBar(toolAppBar)

        }

        NavigationUI.setupActionBarWithNavController(activity, findNavController())
        activity.supportActionBar?.title = ""

        updateRating()

        attemptInitRecyclerView()
    }

    private fun buildDialog() {
        val dialog = BottomSheetDialog(requireContext())
        val dialogAddReviewBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(dialogAddReviewBinding.root)

        //rating is required so it can never be less than 1
        dialogAddReviewBinding.ratingBar.setOnRatingBarChangeListener { _, _, _ ->
            if(dialogAddReviewBinding.ratingBar.rating < 1) dialogAddReviewBinding.ratingBar.rating = 1f
        }

        dialogAddReviewBinding.closeButton.setOnClickListener{
            dialog.dismiss()
        }

        dialogAddReviewBinding.submitButton.setOnClickListener {
            val rating = dialogAddReviewBinding.ratingBar.rating.toInt()
            val review = dialogAddReviewBinding.reviewInput.text.toString().trim()
            addReview(ShowReview(R.drawable.ic_profile_placeholder, "Unknown", rating, review))
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun addReview(review: ShowReview) {
        if (reviews[showId] == null) {
            reviews[showId] = mutableListOf<ShowReview>()
        }
        reviews[showId]!!.add(
            review
        )
        if (reviews[showId]!!.size == 1) attemptInitRecyclerView()

        adapter.notifyItemChanged(reviews[showId]!!.size - 1)
        updateRating()
    }

    private fun updateRating() {
        binding.reviewStats.text = getString(R.string.d_reviews_f_average, getNumberOfReviews(showId), getAverageRating(showId))
        binding.ratingBar.rating = getAverageRating(showId)
    }

    private fun attemptInitRecyclerView() {
        val showReviews = reviews[showId]

        with(binding) {
            if (showReviews != null) {
                adapter = ShowDetailsAdapter(showReviews)
                reviewRecyclerView.adapter = adapter

                reviewsPresentDisplay.visibility = View.VISIBLE
                noReviewsMessage.visibility = View.GONE
            } else {
                reviewsPresentDisplay.visibility = View.GONE
                noReviewsMessage.visibility = View.VISIBLE
            }
        }
    }
}