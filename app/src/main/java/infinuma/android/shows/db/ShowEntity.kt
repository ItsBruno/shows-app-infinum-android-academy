package infinuma.android.shows.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "show")
data class ShowEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "average_rating") val averageRating: Float?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    @ColumnInfo(name = "no_of_reviews") val noOfReviews: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "top_rated") val topRated: Boolean
)