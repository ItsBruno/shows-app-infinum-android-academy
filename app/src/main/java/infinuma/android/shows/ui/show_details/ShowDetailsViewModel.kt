package infinuma.android.shows.ui.show_details

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.R
import infinuma.android.shows.db.ReviewEntity
import infinuma.android.shows.db.ShowsDatabase
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.model.networking.request.AddReviewRequest
import infinuma.android.shows.model.networking.response.Review
import infinuma.android.shows.model.networking.response.Show
import infinuma.android.shows.model.networking.response.User
import infinuma.android.shows.util.saveImage
import java.io.File
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class ShowDetailsViewModel(
    private val database: ShowsDatabase
) : ViewModel() {

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private val _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    private val _reviewAddedLiveData = MutableLiveData<Boolean>()
    val reviewAddedLiveData: LiveData<Boolean> = _reviewAddedLiveData

    private val _showFetchSuccessLiveData = MutableLiveData<Boolean>()
    val showFetchSuccessLiveData: LiveData<Boolean> = _showFetchSuccessLiveData

    private var imageDir: File? = null
    fun getShowInfo(showId: String, networkAvailable: Boolean) {
        if(networkAvailable) {
            viewModelScope.launch {
                try {
                    _showLiveData.value = fetchShowInfo(showId)
                    _showFetchSuccessLiveData.value = true
                } catch (err: Exception) {
                    Log.e("EXCEPTION", err.toString())
                    _showFetchSuccessLiveData.value = false
                }
            }
        }
        else {
            getShowInfoFromDatabase(showId)
        }
    }

    private suspend fun fetchShowInfo(showId: String): Show = ApiModule.retrofit.getShowInfo(showId = showId).show

    fun getReviews(showId: String, networkAvailable: Boolean) {
        if(networkAvailable) {
            viewModelScope.launch {
                try {
                    val reviews = fetchReviews(showId)
                    _reviewsLiveData.value = reviews
                    saveReviewsToDatabase(showId, reviews)
                } catch (err: Exception) {
                    Log.e("EXCEPTION", err.toString())
                }
            }
        }
        else{
            getReviewsFromDatabase(showId)
        }
    }

    private suspend fun fetchReviews(showId: String): List<Review> {
        val result = ApiModule.retrofit.getReviews(showId = showId)
        if (!result.isSuccessful) {
            throw IOException("Unable to get reviews")
        }
        return result.body()!!.reviews
    }

    fun addReview(showId: String, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                _reviewAddedLiveData.value = postReview(rating, comment, showId)

            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _reviewAddedLiveData.value = false
            }
        }
    }

    private suspend fun postReview(
        rating: Int, comment: String, showId: String
    ): Boolean {
        val response = ApiModule.retrofit.addReview(
           request = AddReviewRequest(rating = rating, comment = comment, showId = showId)
        )
        if(!response.isSuccessful) {
            throw IOException("Cannot add review")
        }
        //since the response was successful the network must be available
        getReviews(showId, true)
        getShowInfo(showId, true)
        return true
    }

    private fun getShowInfoFromDatabase(showId: String) {
        viewModelScope.launch {
            val show = database.showDao().getShow(showId)
            _showLiveData.value = Show(show.id, show.averageRating, show.description, show.imageUrl, show.noOfReviews, show.title)
        }
    }

    private fun getReviewsFromDatabase(showId: String) {
        viewModelScope.launch {
            val reviews = database.reviewDao().getAllReviewsForShow(showId).map {review->
                Review(review.id, review.comment,review.rating, review.showId, User(review.userId, review.email, review.imageUrl))
            }
            if(reviews.isNotEmpty()) _reviewsLiveData.value = reviews
        }
    }

    private fun saveReviewsToDatabase(showId: String, reviews: List<Review>) {
        viewModelScope.launch {
            val deferredReviews = reviews.map {review ->
                async {
                    if(review.user.imageUrl != null){
                        val localUri = saveImage(review.user.imageUrl, imageDir!!, review.id)
                        localUri?.let {
                            ReviewEntity(review.id, review.comment, review.rating, review.showId, review.user.id, review.user.email, localUri.toString())
                        }
                    } else {
                        ReviewEntity(review.id, review.comment, review.rating, review.showId, review.user.id, review.user.email, Uri.parse(R.drawable.ic_profile_placeholder.toString()).toString())
                    }
                }
            }
            val reviewEntities = deferredReviews.awaitAll().mapNotNull { it }
            database.reviewDao().insertAllReviews(reviewEntities)
        }
    }

    fun setImageDir(imageDir: File?) {
        this.imageDir = imageDir
    }
}