package infinuma.android.shows.ui.all_shows

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ShowCardBinding
import infinuma.android.shows.model.networking.Show

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

                Glide
                    .with(itemView.context)
                    .load(show.imageUrl)
                    .placeholder(R.drawable.white_background)
                    .listener(object: RequestListener<Drawable> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loadingSpinner.isVisible = false
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            loadingSpinner.isVisible = false
                            return false
                        }
                    })
                    .into(image)
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

    fun updateData(newShows: List<Show>) {
        shows = newShows
        notifyItemRangeChanged(shows.size - newShows.size, shows.size - 1)
    }
}