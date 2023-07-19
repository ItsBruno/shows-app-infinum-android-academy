package infinuma.android.shows.ui.all_shows

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentShowsBinding
import infinuma.android.shows.model.shows

class ShowsFragment : Fragment() {

    private var containsShows = true

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
        initShowsRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListeners() {
        with(binding) {
            noShows.setOnClickListener {
                toggleRecyclerView()
            }

            logoutButton.setOnClickListener {
                findNavController().navigate(ShowsFragmentDirections.actionShowDetailsFragmentToLoginFragment())
            }
        }
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
            val direction = ShowsFragmentDirections.actionShowsFragmentToShowDetailsFragment(show.id)
            findNavController().navigate(direction)
        }
        binding.recyclerView.adapter = adapter
    }
}