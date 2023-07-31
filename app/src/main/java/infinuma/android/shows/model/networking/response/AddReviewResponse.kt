package infinuma.android.shows.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddReviewResponse(
    @SerialName("review") val review: Review
)