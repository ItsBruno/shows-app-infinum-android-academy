package infinuma.android.shows.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.infinum.academy.playground2023.lecture.networking.ApiModule
import infinuma.android.shows.model.networking.AuthResponse
import infinuma.android.shows.model.networking.LoginRequest
import java.io.IOException
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel: ViewModel() {

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
                    accessToken = response.headers()["access-token"]?: "",
                    client = response.headers()["client"]?: "",
                    uid = response.headers()["uid"]?: ""
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
        if(!response.isSuccessful) {
            throw IOException("Login unsuccessful")
        }
        return response
    }
}