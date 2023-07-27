package infinuma.android.shows.ui.show_details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infinum.academy.playground2023.lecture.networking.ApiModule
import infinuma.android.shows.R
import infinuma.android.shows.model.ShowReview
import infinuma.android.shows.model.networking.Review
import infinuma.android.shows.model.networking.Show
import infinuma.android.shows.model.networking.ShowInfoResponse
import java.io.IOException
import kotlinx.coroutines.launch
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private val _reviewsLiveData = MutableLiveData<List<Review>>()
    val reviewsLiveData: LiveData<List<Review>> = _reviewsLiveData

    private val _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    fun getShowInfo(showId: String, accessToken: String, client: String, uid:String) {
        viewModelScope.launch {
            try {
                _showLiveData.value = fetchShowInfo(showId, accessToken, client, uid)
            } catch(err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun fetchShowInfo(showId: String, accessToken: String, client: String, uid:String): Show {
        val response = ApiModule.retrofit.getShowInfo(showId = showId, accessToken = accessToken, client = client, uid = uid)
        if(!response.isSuccessful) {
            throw IOException("Shows fetch failed")
        }
        return response.body()!!.show
    }

    fun getReviews(showId: String, accessToken: String, client: String, uid:String) {
        viewModelScope.launch {
            try {
                _reviewsLiveData.value = fetchReviews(showId, accessToken, client, uid)
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun fetchReviews(showId: String, accessToken: String, client: String, uid:String): List<Review> {
        val result = ApiModule.retrofit.getReviews(showId = showId, accessToken = accessToken, client = client, uid = uid, page = 2)
        if(!result.isSuccessful) {
            throw IOException("Unable to get reviews")
        }
        return result.body()!!.reviews
    }
}