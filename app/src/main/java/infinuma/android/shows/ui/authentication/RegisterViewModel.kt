package infinuma.android.shows.ui.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.model.networking.request.RegisterRequest
import java.io.IOException
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val _registrationResultLiveData = MutableLiveData<Boolean>()
    val registrationResultLiveData: LiveData<Boolean> = _registrationResultLiveData

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                postRegisterRequest(email, password)
            } catch (err: Exception) {
                Log.e("Exception", err.toString())
                _registrationResultLiveData.value = false
            }
        }
    }

    private suspend fun postRegisterRequest(email: String, password: String) {
        val response = ApiModule.retrofit.register(
            request = RegisterRequest(
                email = email,
                password = password,
                passwordConfirmation = password
            )
        )
        if (!response.isSuccessful) {
            throw IOException("Unsuccessful registration")
        }
        _registrationResultLiveData.value = true
    }
}