package infinuma.android.shows.ui.all_shows

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infinum.academy.playground2023.lecture.networking.ApiModule
import infinuma.android.shows.model.networking.Show
import infinuma.android.shows.model.networking.ListShowsResponse
import java.io.IOException
import kotlinx.coroutines.launch
import retrofit2.Response

class ShowsViewModel : ViewModel() {

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    private val _showsFetchSuccessfulLiveData = MutableLiveData<Boolean>()
    val showsFetchSuccessfulLiveData: LiveData<Boolean> = _showsFetchSuccessfulLiveData

    private val _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    fun getShows(accessToken: String, client: String, uid: String) {
        viewModelScope.launch {
            try {
                val response = sentGetShowsRequest(accessToken, client, uid)
                _showsFetchSuccessfulLiveData.value = true
                _showsLiveData.value = response.body()?.shows
            } catch(err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _showsFetchSuccessfulLiveData.value = false
            }
        }
    }

    private suspend fun sentGetShowsRequest(accessToken: String, client: String, uid: String): Response<ListShowsResponse> {
        val response = ApiModule.retrofit.getShows(accessToken, client, uid)
        if(!response.isSuccessful) {
            throw IOException("Failed to get shows")
        }
        return response
    }

}