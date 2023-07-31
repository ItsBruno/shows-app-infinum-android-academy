package infinuma.android.shows.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import infinuma.android.shows.model.networking.response.User

@Entity(tableName = "review",
        foreignKeys = [ForeignKey(
            entity = ShowEntity::class,
            parentColumns = ["id"],
            childColumns = ["show_id"],
            onDelete = ForeignKey.CASCADE
        )])
data class ReviewEntity (
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "show_id") val showId: Int,
    @ColumnInfo(name = "user_id") val userId: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null
    )