package infinuma.android.shows.model

import infinuma.android.shows.R

val reviews = mutableMapOf<String, MutableList<ShowReview>>(
    "The Office" to mutableListOf<ShowReview>(
        ShowReview(R.drawable.ic_profile_placeholder, "Groot", 5, "I am Groot"),
        ShowReview(R.drawable.ic_profile_placeholder, "Mr. White", 3, "Breaking bad is a much better show."),
        ShowReview(R.drawable.ic_profile_placeholder, "Jesse", 2, "I almost fell asleep")
    ),
    "Stranger Things" to mutableListOf<ShowReview>(ShowReview(R.drawable.ic_profile_placeholder, "Jimmy Joe", 5, "This is a cool show")),
)

fun getNumberOfReviews(showTitle: String): Int {
    return (reviews[showTitle]?.size) ?: 0
}

fun getAverageRating(showTitle: String): Float {
    val numOfReviews = getNumberOfReviews(showTitle)
    return if (numOfReviews != 0) (1.0 * ((reviews[showTitle]?.sumOf { it.rating }) ?: 0) / numOfReviews).toFloat()
    else 0.toFloat()
}