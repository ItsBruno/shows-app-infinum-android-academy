package infinuma.android.shows.show_details

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityShowDetailsBinding
import infinuma.android.shows.model.Show
import infinuma.android.shows.model.ShowReview
import infinuma.android.shows.model.getAverageRating
import infinuma.android.shows.model.getNumberOfReviews
import infinuma.android.shows.model.reviews
import infinuma.android.shows.model.shows
import infinuma.android.shows.ui.ShowsActivity.Companion.SHOW_ID


class ShowDetailsActivity : AppCompatActivity() {

    private lateinit var adapter: ShowDetailsAdapter
    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var showsTitle: String
    private lateinit var showId: String
    private lateinit var show: Show

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()

        setContentView(binding.root)
    }

    private fun init() {
        showId = intent.getStringExtra(SHOW_ID)!!
        binding = ActivityShowDetailsBinding.inflate(layoutInflater)

        for (show_iter in shows) {
            if (show_iter.id == showId) {
                show = show_iter
                break
            }
        }

        showsTitle = show.title
        val showDesc = show.description
        val showSrc = show.imageResourceId

        with(binding) {
            showTitle.text = showsTitle
            showImage.setImageResource(showSrc)
            description.text = showDesc

            reviewButton.setOnClickListener {
                initDialog()
            }
        }
        updateRating()

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        attemptInitRecyclerView()

    }

    private fun initDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.shows_app))
            .setMessage(R.string.add_review_message)
            .setPositiveButton(R.string.add, DialogInterface.OnClickListener { _, _ -> addReview() })
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addReview() {
        if (reviews[showId] == null) {
            reviews[showId] = mutableListOf<ShowReview>()
        }
        reviews[showId]!!.add(
            ShowReview(
                R.drawable.ic_profile_placeholder,
                "Biggie Cheese",
                4,
                "This show is bombastic, very fantastic!"
            )
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