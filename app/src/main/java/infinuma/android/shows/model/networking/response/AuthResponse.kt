package infinuma.android.shows.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AuthResponse(
    @SerialName("user") val user: User
)

@kotlinx.serialization.Serializable
data class User(
    @SerialName("id") val id: String,
    @SerialName("email") val email: String,
    @SerialName("image_url") val imageUrl: String? = null,
)