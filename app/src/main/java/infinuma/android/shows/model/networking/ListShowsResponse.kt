package infinuma.android.shows.model.networking

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ListShowsResponse(
    @SerialName("shows") val shows: List<Show>
)

@kotlinx.serialization.Serializable
data class Show(
    @SerialName("id") val id: String,
    @SerialName("average_rating") val averageRating: Float,
    @SerialName("description") val description: String,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("no_of_reviews") val noOfReviews: Int,
    @SerialName("title") val title: String
)
