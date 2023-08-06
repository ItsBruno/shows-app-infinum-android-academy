package infinuma.android.shows

import android.app.Application
import infinuma.android.shows.db.ShowsDatabase

class ShowsApplication : Application() {
    val database by lazy {
        ShowsDatabase.getDatabase(this)
    }
}