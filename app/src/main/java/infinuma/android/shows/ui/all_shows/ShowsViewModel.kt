package infinuma.android.shows.ui.all_shows

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.model.networking.response.Show
import infinuma.android.shows.model.networking.response.User
import java.io.File
import java.io.IOException
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.Path

const val PAGE_SIZE = 20

class ShowsViewModel : ViewModel() {

    private var page = 0
    private var fetching = false

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    private val _showsTopLiveData = MutableLiveData<List<Show>>()
    val showsTopLiveData: LiveData<List<Show>> = _showsTopLiveData

    private val _pfpUrlLiveData = MutableLiveData<Uri>()
    val pfpUrlLiveData: LiveData<Uri> = _pfpUrlLiveData

    fun getShows() {
        if ((page < 5) and (!fetching)) {
            page++
            fetching = true
        } else return
        viewModelScope.launch {
            try {
                val shows = sendGetShowsRequest()
                if (_showsLiveData.value == null) {
                    _showsLiveData.value = listOf<Show>()
                }
                if (!shows.isNullOrEmpty()) {
                    val oldShows = _showsLiveData.value
                    _showsLiveData.value = oldShows!! + shows
                }
                fetching = false
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    fun getTopRatedShows() {
        viewModelScope.launch {
            try {
                _showsTopLiveData.value = sentGetTopShowsRequest()
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun sentGetTopShowsRequest(): List<Show>? {
        val response = ApiModule.retrofit.getTopRatedShows(items = PAGE_SIZE)
        if (!response.isSuccessful) {
            throw IOException("Failed to get top rated shows")
        }
        return response.body()?.shows
    }

    private suspend fun sendGetShowsRequest(): List<Show>? {
        val response = ApiModule.retrofit.getShows(page = page, items = PAGE_SIZE)
        if (!response.isSuccessful) {
            throw IOException("Failed to get shows")
        }
        return response.body()?.shows
    }

    fun uploadProfilePicture(picture: File) {
        viewModelScope.launch {
            try {
                val user = putProfilePictureRequest(picture)
                _pfpUrlLiveData.value = Uri.parse(user?.imageUrl)
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }

        }
    }

    private suspend fun putProfilePictureRequest(picture: File): User? {
        val response = ApiModule.retrofit.putProfilePicture(
            MultipartBody.Part.createFormData("image",picture.name, picture.asRequestBody("image/*".toMediaType()))
        )
        if (!response.isSuccessful) {
            throw IOException("Failed to upload profile picture")
        }
        return response.body()?.user
    }

    fun getMyInfo() {
        viewModelScope.launch {
            try {
                val user = sendMyInfoGetRequest()
                _pfpUrlLiveData.value = Uri.parse(user?.imageUrl)
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
            }
        }
    }

    private suspend fun sendMyInfoGetRequest(): User? {
        val response = ApiModule.retrofit.getMe()

        return response.user
    }


}