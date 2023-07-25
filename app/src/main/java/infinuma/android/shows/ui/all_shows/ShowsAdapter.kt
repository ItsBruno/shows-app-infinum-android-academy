package infinuma.android.shows.ui.all_shows

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.databinding.ShowCardBinding
import infinuma.android.shows.model.Show

class ShowsAdapter(
    private var shows: List<Show>, private val onShowClickCallback: (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    inner class ShowViewHolder(private val binding: ShowCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(show: Show) {
            with(binding) {
                cardContainer.setOnClickListener {
                    onShowClickCallback.invoke(show)
                }
                showTitle.text = show.title
                showDescription.text = show.description

                image.setImageResource(show.imageResourceId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ShowCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return shows.size
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(shows[position])
    }

}