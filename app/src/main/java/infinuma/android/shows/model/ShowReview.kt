package infinuma.android.shows.model

import androidx.annotation.DrawableRes

data class ShowReview(
    @DrawableRes val profilePictureResourceId: Int, var name: String, var rating: Int, var comment: String = ""
)