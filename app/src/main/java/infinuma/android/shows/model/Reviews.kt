package infinuma.android.shows.model

import infinuma.android.shows.R

val reviews = mutableMapOf<String, MutableList<ShowReview>>(
    "Show1" to mutableListOf<ShowReview>(
        ShowReview(R.drawable.ic_profile_placeholder, "Groot", 5, "I am Groot"),
        ShowReview(R.drawable.ic_profile_placeholder, "Mr. White", 3, "Breaking bad is a much better show."),
        ShowReview(R.drawable.ic_profile_placeholder, "Jesse", 2, "I almost fell asleep")
    ),
    "Show2" to mutableListOf<ShowReview>(ShowReview(R.drawable.ic_profile_placeholder, "Jimmy Joe", 5, "This is a cool show")),
)

fun getNumberOfReviews(showId: String): Int = (reviews[showId]?.size) ?: 0


fun getAverageRating(showId: String): Float {
    val numOfReviews = getNumberOfReviews(showId)
    return if (numOfReviews != 0) (1.0 * ((reviews[showId]?.sumOf { it.rating }) ?: 0) / numOfReviews).toFloat()
    else 0.toFloat()
}