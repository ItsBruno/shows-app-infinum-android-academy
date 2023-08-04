package infinuma.android.shows

import android.app.Application
import infinuma.android.shows.db.ShowsDatabase
import infinuma.android.shows.networking.ApiModule.initRetrofit

class ShowsApplication : Application() {
    val database by lazy {
        ShowsDatabase.getDatabase(this)
    }
}