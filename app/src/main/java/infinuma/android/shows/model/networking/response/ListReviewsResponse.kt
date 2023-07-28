package infinuma.android.shows.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ListReviewsResponse(
    @SerialName("reviews") val reviews: List<Review>
)

@kotlinx.serialization.Serializable
data class Review(
    @SerialName("id") val id: String,
    @SerialName("comment") val comment: String,
    @SerialName("rating") val rating: Int,
    @SerialName("show_id") val showId: Int,
    @SerialName("user") val user: User
)