package infinuma.android.shows.networking

import infinuma.android.shows.model.RegisterResponse
import infinuma.android.shows.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ShowsApiService {

    @POST("/users")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}