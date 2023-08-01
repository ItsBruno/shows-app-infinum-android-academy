package infinuma.android.shows.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import infinuma.android.shows.db.ShowsDatabase
import infinuma.android.shows.ui.all_shows.ShowsViewModel
import infinuma.android.shows.ui.show_details.ShowDetailsViewModel
import java.lang.IllegalArgumentException

class ShowsViewModelFactory(
    private val database: ShowsDatabase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShowsViewModel::class.java)) {
            return ShowsViewModel(database = database) as T
        } else if (modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)) {
            return ShowDetailsViewModel(database = database) as T
        }
        throw IllegalArgumentException("Sorry, can't work with this")
    }
}