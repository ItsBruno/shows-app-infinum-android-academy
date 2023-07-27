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

const val PAGE_SIZE = 20
class ShowsViewModel : ViewModel() {

    private var page = 0
    private var fetching = false

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    private val _showsTopLiveData = MutableLiveData<List<Show>>()
    val showsTopLiveData: LiveData<List<Show>> = _showsTopLiveData

    fun getShows(accessToken: String, client: String, uid: String) {
        if((page < 5) and (!fetching)) {
            page++
            fetching = true
        }
        else return
        viewModelScope.launch {
            try {
                val shows = sendGetShowsRequest(accessToken, client, uid)
                if(_showsLiveData.value == null) {
                    _showsLiveData.value = listOf<Show>()
                }
                if(!shows.isNullOrEmpty()){
                    val oldShows = _showsLiveData.value
                    _showsLiveData.value = oldShows!! + shows
                }
                fetching = false
            } catch(err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    fun getTopRatedShows(accessToken: String, client: String, uid: String) {
        viewModelScope.launch {
            try {
                _showsTopLiveData.value = sentGetTopShowsRequest(accessToken, client, uid)
            } catch(err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun sentGetTopShowsRequest(accessToken: String, client: String, uid: String): List<Show>? {
        val response = ApiModule.retrofit.getTopRatedShows(accessToken, client, uid, items = PAGE_SIZE)
        if(!response.isSuccessful) {
            throw IOException("Failed to get top rated shows")
        }
        return response.body()?.shows
    }

    private suspend fun sendGetShowsRequest(accessToken: String, client: String, uid: String): List<Show>? {
        val response = ApiModule.retrofit.getShows(accessToken, client, uid, page = page, items = PAGE_SIZE)
        if(!response.isSuccessful) {
            throw IOException("Failed to get shows")
        }
        return response.body()?.shows
    }

}