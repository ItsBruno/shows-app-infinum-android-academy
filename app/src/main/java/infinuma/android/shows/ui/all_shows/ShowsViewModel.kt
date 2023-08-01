package infinuma.android.shows.ui.all_shows

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.db.ShowEntity
import infinuma.android.shows.db.ShowsDatabase
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.model.networking.response.Show
import infinuma.android.shows.model.networking.response.User
import infinuma.android.shows.util.saveImage
import java.io.File
import java.io.IOException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

const val PAGE_SIZE = 20

class ShowsViewModel(
    private val database: ShowsDatabase
) : ViewModel() {

    private var page = 0
    private var fetching = false
    private var imageDir: File? = null
    private var lastShowChangeFromDatabase = false

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLiveData: LiveData<List<Show>> = _showsLiveData

    private val _showsTopLiveData = MutableLiveData<List<Show>>()
    val showsTopLiveData: LiveData<List<Show>> = _showsTopLiveData

    private val _pfpUrlLiveData = MutableLiveData<Uri>()
    val pfpUrlLiveData: LiveData<Uri> = _pfpUrlLiveData

    private val _getShowsSuccessLiveData = MutableLiveData<Boolean>()
    val getShowsSuccessLiveData: LiveData<Boolean> = _getShowsSuccessLiveData

    private val _getTopShowsSuccessLiveData = MutableLiveData<Boolean>()
    val getTopShowsSuccessLiveData: LiveData<Boolean> = _getTopShowsSuccessLiveData

    private val _profilePictureUploadSuccessLiveData = MutableLiveData<Boolean>()
    val profilePictureUploadSuccessLiveData: LiveData<Boolean> = _profilePictureUploadSuccessLiveData

    private val _myProfileGetLiveData = MutableLiveData<Boolean>()
    val getMyProfileSuccessLiveData: LiveData<Boolean> = _myProfileGetLiveData

    fun getShows(networkAvailable: Boolean) {
        if (networkAvailable) {
            /*
              If the last live data change was from the database, then the live data should be reset.
              The reason for that is that if the user wanted to see the top rated shows, they would be saved to the database upon download,
              then if he lost internet connection, and opened normal shows, the top rated shows would be saved to live data
              and displayed after the eg. first page of all shows, then if he got the internet connection back, upon reaching the end of the shows in the
              current state of the live data new shows would be fetched and since the top rated shows were saved in the live data
              the top rated shows would be displayed twice
            */
            if(lastShowChangeFromDatabase) {
                page = 0
                _showsLiveData.value = listOf<Show>()
                lastShowChangeFromDatabase = false
            }
            //fetch from server
            if ((page < 5) and (!fetching)) {
                page++
                fetching = true
            } else {
                return
            }
            viewModelScope.launch {
                try {
                    val shows = sendGetShowsRequest()
                    if (_showsLiveData.value == null) {
                        _showsLiveData.value = listOf<Show>()
                    }
                    if (!shows.isNullOrEmpty()) {
                        val oldShows = _showsLiveData.value
                        _showsLiveData.value = oldShows!! + shows
                        saveToDatabase(shows, topRated = false)
                        _getShowsSuccessLiveData.value = true
                    }
                    fetching = false
                } catch (err: Exception) {
                    Log.e("EXCEPTION", err.toString())
                    _getShowsSuccessLiveData.value = false
                }
            }
        } else {
            getShowsFromDb(topRated = false)
        }
    }

    fun getTopRatedShows(networkAvailable: Boolean) {
        if (networkAvailable) {
            viewModelScope.launch {
                try {
                    val shows = sentGetTopShowsRequest()
                    if (shows != null) {
                        _showsTopLiveData.value = shows!!
                        _getTopShowsSuccessLiveData.value = true
                        saveToDatabase(shows, topRated = true)
                    }
                } catch (err: Exception) {
                    Log.e("EXCEPTION", err.toString())
                    _getTopShowsSuccessLiveData.value = false
                }
            }
        } else {
            getShowsFromDb(topRated = true)
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
                _profilePictureUploadSuccessLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _profilePictureUploadSuccessLiveData.value = false
            }

        }
    }

    private suspend fun putProfilePictureRequest(picture: File): User? {
        val response = ApiModule.retrofit.putProfilePicture(
            MultipartBody.Part.createFormData("image", picture.name, picture.asRequestBody("image/*".toMediaType()))
        )
        if (!response.isSuccessful) {
            throw IOException("Failed to upload profile picture")
        }
        return response.body()?.user
    }

    fun getMyProfile() {
        viewModelScope.launch {
            try {
                val user = sendMyInfoGetRequest()
                _pfpUrlLiveData.value = Uri.parse(user?.imageUrl)
                _myProfileGetLiveData.value = true
            } catch (err: Exception) {
                Log.e("EXCEPTION", err.toString())
                _myProfileGetLiveData.value = false
            }
        }
    }

    private suspend fun sendMyInfoGetRequest(): User = ApiModule.retrofit.getMe().user

    private fun saveToDatabase(shows: List<Show>, topRated: Boolean) {
        viewModelScope.launch {
            val deferredShows = shows.map { show ->
                async {
                    val localUri = saveImage(show.imageUrl, imageDir!!, show.id)
                    localUri?.let {
                        ShowEntity(show.id, show.averageRating, show.description, localUri.toString(), show.noOfReviews, show.title, topRated)
                    }
                }
            }
            val showEntities = deferredShows.awaitAll().mapNotNull { it }
            database.showDao().insertAllShows(showEntities)
        }
    }

    private fun getShowsFromDb(topRated: Boolean) {
        viewModelScope.launch {
            if (topRated) {
                val shows = database.showDao().getAllTopRatedShows().map { showEntity -> showEntityToShow(showEntity) }
                //these checks are so that the live data stays null if the database is empty and therefore the no shows state stays displayed
                if (shows.isNotEmpty()) _showsTopLiveData.value = shows

            } else {
                val shows = database.showDao().getAllShows().map { showEntity -> showEntityToShow(showEntity) }
                if (shows.isNotEmpty()) {
                    _showsLiveData.value = shows
                    lastShowChangeFromDatabase = true
                }
            }
        }
    }

    private fun showEntityToShow(showEntity: ShowEntity): Show {
        return Show(
            showEntity.id, showEntity.averageRating, showEntity.description, showEntity.imageUrl, showEntity.noOfReviews, showEntity.title
        )
    }
    fun setImageDir(imageDir: File?) {
        this.imageDir = imageDir
    }

}