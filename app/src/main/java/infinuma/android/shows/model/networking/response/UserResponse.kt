package infinuma.android.shows.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UserResponse(
    @SerialName("user") val user: User
)