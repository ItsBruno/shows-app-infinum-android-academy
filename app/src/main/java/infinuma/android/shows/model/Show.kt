package infinuma.android.shows.model

import androidx.annotation.DrawableRes

data class Show(
    @DrawableRes val imageResourceId: Int, val title: String, val description: String, val id: String = generateId()
) {
    companion object {
        private var counter = 0
        private const val ID_TEXT = "SHOW"

        private fun generateId(): String {
            return "${ID_TEXT}${++counter}"
        }
    }

}