package infinuma.android.shows.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import infinuma.android.shows.databinding.ActivityShowsBinding
import infinuma.android.shows.model.shows

class ShowsActivity : AppCompatActivity() {

    private var contains_shows = true

    private lateinit var mainLayout: View
    private lateinit var alternateLayout: View

    private lateinit var binding: ActivityShowsBinding

    private lateinit var adapter: ShowsAdapter

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
        if (contains_shows) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
            binding.message.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE
            binding.message.visibility = View.GONE
        }
        contains_shows = !contains_shows
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) /*{show ->

        }*/
        binding.recyclerView.adapter = adapter


    }
}