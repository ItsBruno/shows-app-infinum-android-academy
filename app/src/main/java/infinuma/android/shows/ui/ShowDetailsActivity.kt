package infinuma.android.shows.ui

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityShowDetailsBinding
import infinuma.android.shows.model.ShowReview
import infinuma.android.shows.model.getAverageRating
import infinuma.android.shows.model.getNumberOfReviews
import infinuma.android.shows.model.reviews
import infinuma.android.shows.ui.ShowsActivity.Companion.SHOW_DESC
import infinuma.android.shows.ui.ShowsActivity.Companion.SHOW_SRC
import infinuma.android.shows.ui.ShowsActivity.Companion.SHOW_TITLE

class ShowDetailsActivity : AppCompatActivity() {

    private lateinit var adapter: ShowDetailsAdapter
    private lateinit var binding: ActivityShowDetailsBinding
    private var totalReviews: Int = 0
    private var rating: Float = 0F
    private lateinit var showTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showTitle = intent.getStringExtra(SHOW_TITLE)!!

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)
        binding.showTitle.text = showTitle
        binding.showImage.setImageResource(intent.getIntExtra(SHOW_SRC, 0))
        binding.description.text = intent.getStringExtra(SHOW_DESC)
        updateRating()

        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = ""

        initRecyclerView()

        binding.reviewButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Shows app")
                .setMessage("Are you sure you want to add a new review?")
                .setPositiveButton(R.string.add, DialogInterface.OnClickListener { _, _ -> addReview() })
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        setContentView(binding.root)
    }

    private fun addReview() {
        if (reviews[showTitle] == null) {
            reviews[showTitle] = mutableListOf<ShowReview>()
        }
        reviews[showTitle]!!.add(
            ShowReview(
                R.drawable.ic_profile_placeholder,
                "Biggie Cheese",
                4,
                "This show is bombastic, very fantastic!"
            )
        )
        initRecyclerView()
        adapter.notifyItemChanged(reviews[showTitle]!!.size - 1)
        updateRating()
    }

    private fun updateRating() {
        binding.reviewStats.text = getString(R.string.d_reviews_f_average, getNumberOfReviews(showTitle), getAverageRating(showTitle))
        binding.ratingBar.rating = getAverageRating(showTitle)
    }

    private fun initRecyclerView() {
        val showReviews = reviews[showTitle]

        if (showReviews != null) {
            adapter = ShowDetailsAdapter(showReviews)
            binding.reviewRecyclerView.adapter = adapter

            binding.reviewRecyclerView.visibility = View.VISIBLE
            binding.ratingBar.visibility = View.VISIBLE
            binding.reviewStats.visibility = View.VISIBLE
            binding.noReviewsMessage.visibility = View.GONE
        } else {
            binding.reviewRecyclerView.visibility = View.GONE
            binding.ratingBar.visibility = View.GONE
            binding.reviewStats.visibility = View.GONE
            binding.noReviewsMessage.visibility = View.VISIBLE
        }

    }

}

