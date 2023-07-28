package infinuma.android.shows.model.networking.response

import infinuma.android.shows.model.networking.response.Show
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ShowInfoResponse(
    @SerialName("show") val show: Show
)
