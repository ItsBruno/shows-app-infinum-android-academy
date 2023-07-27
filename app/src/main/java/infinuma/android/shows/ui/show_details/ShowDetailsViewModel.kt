package infinuma.android.shows.ui.show_details

import android.media.Rating
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infinum.academy.playground2023.lecture.networking.ApiModule
import infinuma.android.shows.R
import infinuma.android.shows.model.ShowReview
import infinuma.android.shows.model.networking.AddReviewRequest
import infinuma.android.shows.model.networking.Review
import infinuma.android.shows.model.networking.Show
import infinuma.android.shows.model.networking.ShowInfoResponse
import java.io.IOException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response

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

    fun setSessionInfo(accessToken: String, client: String, uid: String) {
        this.accessToken = accessToken
        this.client = client
        this.uid = uid
    }
    fun getShowInfo(showId: String) {
        viewModelScope.launch {
            try {
                _showLiveData.value = fetchShowInfo(showId)
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun fetchShowInfo(showId: String): Show {
        val response = ApiModule.retrofit.getShowInfo(showId = showId, accessToken = accessToken, client = client, uid = uid)
        if (!response.isSuccessful) {
            throw IOException("Shows fetch failed")
        }
        return response.body()!!.show
    }

    fun getReviews(showId: String, delay: Long) {
        viewModelScope.launch {
            //The delay is required for displaying the review after it is added.
            delay(delay)
            try {
                _reviewsLiveData.value = fetchReviews(showId)
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun fetchReviews(showId: String): List<Review> {
        val result = ApiModule.retrofit.getReviews(showId = showId, accessToken = accessToken, client = client, uid = uid)
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
            accessToken, client, uid, request = AddReviewRequest(rating = rating, comment = comment, showId = showId)
        )
        if(!response.isSuccessful) {
            throw IOException("Cannot add review")
        }
        return true
    }
}