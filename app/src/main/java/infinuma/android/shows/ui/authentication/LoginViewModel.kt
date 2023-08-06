package infinuma.android.shows.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.model.networking.response.AuthResponse
import infinuma.android.shows.model.networking.request.LoginRequest
import java.io.IOException
import kotlinx.coroutines.launch
import retrofit2.Response

const val ACCESS_HEADER = "access-token"
const val CLIENT_HEADER = "client"
const val UID_HEADER = "uid"

class LoginViewModel : ViewModel() {

    data class sessionData(
        val accessToken: String,
        val client: String,
        val uid: String
    )

    private val _loginSuccessfulLiveData = MutableLiveData<Boolean>()
    val loginSuccessfulLiveData: LiveData<Boolean> = _loginSuccessfulLiveData

    private val _sessionLiveData = MutableLiveData<sessionData>()
    val sessionLiveData: LiveData<sessionData> = _sessionLiveData

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = postLoginRequest(email, password)
                _loginSuccessfulLiveData.value = true
                _sessionLiveData.value = sessionData(
                    accessToken = response.headers()[ACCESS_HEADER] ?: "",
                    client = response.headers()[CLIENT_HEADER] ?: "",
                    uid = response.headers()[UID_HEADER] ?: ""
                )

            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _loginSuccessfulLiveData.value = false
            }
        }
    }

    private suspend fun postLoginRequest(email: String, password: String): Response<AuthResponse> {
        val response = ApiModule.retrofit.login(
            request = LoginRequest(
                email = email,
                password = password
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Login unsuccessful")
        }
        return response
    }
}