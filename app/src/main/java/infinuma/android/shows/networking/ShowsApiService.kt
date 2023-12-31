package infinuma.android.shows.networking

import infinuma.android.shows.model.networking.request.AddReviewRequest
import infinuma.android.shows.model.networking.response.AuthResponse
import infinuma.android.shows.model.networking.response.ListReviewsResponse
import infinuma.android.shows.model.networking.response.ListShowsResponse
import infinuma.android.shows.model.networking.request.LoginRequest
import infinuma.android.shows.model.networking.request.RegisterRequest
import infinuma.android.shows.model.networking.response.AddReviewResponse
import infinuma.android.shows.model.networking.response.UserResponse
import infinuma.android.shows.model.networking.response.ShowInfoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

const val PAGES = 1
const val ITEMS = 20

interface ShowsApiService {

    @POST("/users")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/users/sign_in")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("/shows")
    suspend fun getShows(
        @Query("page") page: Int = PAGES,
        @Query("items") items: Int = ITEMS
    ): Response<ListShowsResponse>

    @GET("/shows/{id}")
    suspend fun getShowInfo(
        @Path("id") showId: String,
    ): ShowInfoResponse

    @GET("/shows/top_rated")
    suspend fun getTopRatedShows(
        @Query("page") page: Int = PAGES,
        @Query("items") items: Int = ITEMS
    ): Response<ListShowsResponse>

    @GET("/shows/{id}/reviews")
    suspend fun getReviews(
        @Path("id") showId: String,
        @Query("page") page: Int = PAGES,
        @Query("items") items: Int = ITEMS
    ): Response<ListReviewsResponse>

    @POST("/reviews")
    suspend fun addReview(
        @Body request: AddReviewRequest
    ): Response<AddReviewResponse>

    @Multipart
    @PUT("/users")
    suspend fun putProfilePicture(
        @Part image: MultipartBody.Part
    ): Response<UserResponse>

    @GET("/users/me")
    suspend fun getMe(): UserResponse
}