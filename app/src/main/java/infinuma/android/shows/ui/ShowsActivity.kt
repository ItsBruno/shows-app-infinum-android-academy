package infinuma.android.shows.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityShowsBinding
import infinuma.android.shows.model.shows
import infinuma.android.shows.show_details.ShowDetailsActivity

class ShowsActivity : AppCompatActivity() {

    private var containsShows = true

    private lateinit var binding: ActivityShowsBinding

    private lateinit var adapter: ShowsAdapter

    companion object {
        const val SHOW_ID: String = "show_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowsBinding.inflate(layoutInflater)

        binding.noShows.setOnClickListener {
            toggleRecyclerView()
        }

        initShowsRecycler()
        setContentView(binding.root)
    }

    private fun toggleRecyclerView() {
        with(binding) {
            if (containsShows) {
                recyclerView.visibility = View.GONE
                noShowsDisplay.visibility = View.VISIBLE
                noShows.text = getString(R.string.get_shows)
            } else {
                recyclerView.visibility = View.VISIBLE
                noShowsDisplay.visibility = View.GONE
                noShows.text = getString(R.string.no_shows)
            }
        }
        containsShows = !containsShows
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            val intent = Intent(binding.root.context, ShowDetailsActivity::class.java)
            intent.putExtra(SHOW_ID, show.id)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter


    }
}