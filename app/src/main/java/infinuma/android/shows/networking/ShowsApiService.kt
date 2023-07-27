package infinuma.android.shows.networking

import infinuma.android.shows.model.networking.AddReviewRequest
import infinuma.android.shows.model.networking.AuthResponse
import infinuma.android.shows.model.networking.ListReviewsResponse
import infinuma.android.shows.model.networking.ListShowsResponse
import infinuma.android.shows.model.networking.LoginRequest
import infinuma.android.shows.model.networking.RegisterRequest
import infinuma.android.shows.model.networking.Review
import infinuma.android.shows.model.networking.ShowInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ShowsApiService {

    @POST("/users")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/users/sign_in")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("/shows")
    suspend fun getShows(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("page") page: Int = 1,
        @Query("items") items: Int = 20
    ): Response<ListShowsResponse>

    @GET("/shows/{id}")
    suspend fun getShowInfo(
        @Path("id") showId: String,
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String
    ): Response<ShowInfoResponse>

    @GET("/shows/top_rated")
    suspend fun getTopRatedShows(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("page") page: Int = 1,
        @Query("items") items: Int = 20
    ): Response<ListShowsResponse>

    @GET("/shows/{id}/reviews")
    suspend fun getReviews(
        @Path("id") showId: String,
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Query("page") page: Int = 1,
        @Query("items") items: Int = 20
    ): Response<ListReviewsResponse>

    @POST("/reviews")
    suspend fun addReview(
        @Header("access-token") accessToken: String,
        @Header("client") client: String,
        @Header("uid") uid: String,
        @Body request: AddReviewRequest
    ): Response<Review>
}