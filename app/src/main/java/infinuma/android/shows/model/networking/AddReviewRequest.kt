package infinuma.android.shows.model.networking

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddReviewRequest(
    @SerialName("rating") val rating: Int,
    @SerialName("comment") val comment: String,
    @SerialName("show_id") val showId: String
    )