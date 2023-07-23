package infinuma.android.shows.ui.all_shows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import infinuma.android.shows.R
import infinuma.android.shows.model.Show

class ShowsViewModel : ViewModel() {

    private val shows = listOf(
        Show(
            "Show1",
            R.drawable.ic_office,
            "The Office",
            "The Office is an American mockumentary sitcom television series that depicts the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons."
        ), Show(
            "Show2",
            R.drawable.ic_stranger_things,
            "Stranger Things",
            "Set in the 1980s, the series centers around the residents of the fictional small town of Hawkins, Indiana, as they are plagued by a hostile alternate dimension known as the Upside Down, after a nearby human experimentation facility opens a gateway between it and the normal world."
        ), Show(
            "Show3",
            R.drawable.krv_nije_voda,
            "Krv Nije Voda",
            "Serija je nadahnuta svakodnevnim životnim pričama koje pogađaju mnoge obitelji, poput nestanka člana obitelji, upadanja u zamku nagomilanih dugova, iznenadnog kraha braka zbog varanja supružnika, borbe oko skrbništva nad djecom, ovisnosti o kockanju ili problema s nestašnom djecom."
        )
    )

    private val _showsLiveData = MutableLiveData<List<Show>>()
    val showsLivedData: LiveData<List<Show>> = _showsLiveData

    private val _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    init {
        _showsLiveData.value = shows
    }

    fun getShow(showId: String): LiveData<Show> {
        for (show in shows) {
            if (show.id == showId) {
                _showLiveData.value = show
                break
            }
        }
        return showLiveData
    }
}