package infinuma.android.shows.ui

import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.databinding.ShowCardBinding
import infinuma.android.shows.model.Show

class ShowsAdapter(
    private var shows: List<Show>
    //private val onShowClickCallback: (Show) -> Unit
) : RecyclerView.Adapter<ShowsAdapter.ShowViewHolder>() {

    inner class ShowViewHolder(private val binding: ShowCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(show: Show) {
            /*binding.cardContainer.setOnClickListener {
                onShowClickCallback.invoke(show)
            }*/
            binding.showTitle.text = show.title
            binding.showDescription.text = show.description
            /*binding.showDescription.setOnClickListener {
                binding.showDescription.requestFocus()
            }*/
            binding.image.setImageResource(show.imageResourceId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ShowCardBinding.inflate(LayoutInflater.from(parent.context))
        return ShowViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return shows.size
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(shows[position])
    }

}