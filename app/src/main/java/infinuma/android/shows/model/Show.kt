package infinuma.android.shows.model

import androidx.annotation.DrawableRes

data class Show(
    val id: String, @DrawableRes val imageResourceId: Int, val title: String, val description: String
) {

}