package infinuma.android.shows.ui.show_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import infinuma.android.shows.R
import infinuma.android.shows.model.ShowReview

class ShowDetailsViewModel : ViewModel() {
    private val reviews = mutableMapOf<String, MutableList<ShowReview>>(
        "Show1" to mutableListOf<ShowReview>(
            ShowReview(R.drawable.ic_profile_placeholder, "Groot", 5, "I am Groot"),
            ShowReview(R.drawable.ic_profile_placeholder, "Mr. White", 3, "Breaking bad is a much better show."),
            ShowReview(R.drawable.ic_profile_placeholder, "Jesse", 2, "I almost fell asleep")
        ),
        "Show2" to mutableListOf<ShowReview>(ShowReview(R.drawable.ic_profile_placeholder, "Jimmy Joe", 5, "This is a cool show")),
    )

    private val _reviewsLiveData = MutableLiveData<List<ShowReview>>()
    val reviewsLiveData: LiveData<List<ShowReview>> = _reviewsLiveData

    private val _numOfReviewLiveData = MutableLiveData<Int>()
    val numOfReviewLiveData: LiveData<Int> = _numOfReviewLiveData

    private val _averageRatingLiveData = MutableLiveData<Float>()
    val averageRatingLiveData: LiveData<Float> = _averageRatingLiveData

    fun getReviews(showId: String): LiveData<List<ShowReview>> {
        if (reviews[showId] == null) {
            reviews[showId] = mutableListOf<ShowReview>()
        }
        _reviewsLiveData.value = reviews[showId]
        return reviewsLiveData
    }

    fun getNumberOfReviews(showId: String): LiveData<Int> {
        _numOfReviewLiveData.value = (reviews[showId]?.size) ?: 0
        return numOfReviewLiveData
    }

    fun getAverageRating(showId: String): LiveData<Float> {
        _averageRatingLiveData.value = when {
            reviews.isNotEmpty() -> (1.0 * ((reviews[showId]?.sumOf { it.rating }) ?: 0) / ((reviews[showId]?.size) ?: 1)).toFloat()
            else -> 0.toFloat()
        }
        return averageRatingLiveData
    }

    fun addReview(showId: String, review: ShowReview) {
        if (reviews[showId] == null) {
            reviews[showId] = mutableListOf<ShowReview>()
        }
        reviews[showId]!!.add(
            review
        )
        updateLiveData(showId)
    }

    private fun updateLiveData(showId: String) {
        _reviewsLiveData.value = reviews[showId]
        _numOfReviewLiveData.value = (reviews[showId]?.size) ?: 0
        _averageRatingLiveData.value = (1.0 * ((reviews[showId]?.sumOf { it.rating }) ?: 0) / ((reviews[showId]?.size) ?: 1)).toFloat()
    }
}