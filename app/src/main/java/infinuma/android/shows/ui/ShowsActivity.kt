package infinuma.android.shows.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import infinuma.android.shows.databinding.ActivityShowsBinding
import infinuma.android.shows.model.shows

class ShowsActivity : AppCompatActivity() {

    private var containsShows = true

    private lateinit var binding: ActivityShowsBinding

    private lateinit var adapter: ShowsAdapter

    companion object {
        const val SHOW_TITLE: String = "title"
        const val SHOW_SRC: String = "src"
        const val SHOW_DESC: String = "description"
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
        if (containsShows) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
            binding.message.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE
            binding.message.visibility = View.GONE
        }
        containsShows = !containsShows
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(shows) { show ->
            val intent = Intent(binding.root.context, ShowDetailsActivity::class.java)
            intent.putExtra(SHOW_TITLE, show.title)
            intent.putExtra(SHOW_SRC, show.imageResourceId)
            intent.putExtra(SHOW_DESC, show.description)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter


    }
}