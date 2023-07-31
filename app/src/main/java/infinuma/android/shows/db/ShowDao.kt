package infinuma.android.shows.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShowDao {

    @Query("SELECT * FROM show")
    suspend fun getAllShows(): List<ShowEntity>

    @Query("SELECT * FROM show WHERE show.top_rated = 1")
    suspend fun getAllTopRatedShows(): List<ShowEntity>

    @Query("SELECT * FROM show WHERE id IS :showId")
    suspend fun getShow(showId: String): ShowEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllShows(shows: List<ShowEntity>)
}
