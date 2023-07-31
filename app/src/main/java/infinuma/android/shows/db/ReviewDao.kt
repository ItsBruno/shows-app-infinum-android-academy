package infinuma.android.shows.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReviewDao {
    @Query("SELECT review.*  FROM review INNER JOIN show ON review.show_id = show.id AND show.id = :showId")
    suspend fun getAllReviewsForShow(showId: String): List<ReviewEntity>

    @Query("SELECT * FROM review WHERE id IS :reviewId")
    suspend fun getReview(reviewId: String): ReviewEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReviews(reviews: List<ReviewEntity>)
}