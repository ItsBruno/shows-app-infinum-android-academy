package infinuma.android.shows.ui.show_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.model.networking.request.AddReviewRequest
import infinuma.android.shows.model.networking.response.Review
import infinuma.android.shows.model.networking.response.Show
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ShowDetailsViewModel : ViewModel() {

    private lateinit var accessToken: String
    private lateinit var client: String
    private lateinit var uid: String

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private val _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    private val _reviewAddedLiveData = MutableLiveData<Boolean>()
    val reviewAddedLiveData: LiveData<Boolean> = _reviewAddedLiveData

    private val _showFetchSuccessLiveData = MutableLiveData<Boolean>()
    val showFetchSuccessLiveData: LiveData<Boolean> = _showFetchSuccessLiveData

    fun getShowInfo(showId: String) {
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

    private suspend fun fetchShowInfo(showId: String): Show {
        val response = ApiModule.retrofit.getShowInfo(showId = showId)
        return response.show
    }

    fun getReviews(showId: String) {
        viewModelScope.launch {
            try {
                _reviewsLiveData.value = fetchReviews(showId)
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
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
        getReviews(showId)
        getShowInfo(showId)
        return true
    }
}