package infinuma.android.shows.model.networking

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ShowInfoResponse(
    @SerialName("show") val show: Show
)
