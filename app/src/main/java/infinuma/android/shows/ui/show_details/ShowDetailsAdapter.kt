package infinuma.android.shows.ui.show_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ReviewCardBinding
import infinuma.android.shows.model.networking.response.Review

class ShowDetailsAdapter(private var reviewList: List<Review>) : RecyclerView.Adapter<ShowDetailsAdapter.ShowReviewsViewHolder>() {

    inner class ShowReviewsViewHolder(private var binding: ReviewCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            with(binding) {
                profileName.text = review.user.email.substring(0, review.user.email.indexOf("@"))
                rating.text = review.rating.toString()
                reviewComment.text = review.comment
                Glide
                    .with(itemView.context)
                    .load(review.user.imageUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(profilePicture)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowReviewsViewHolder {
        val reviewBinding: ReviewCardBinding = ReviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ShowReviewsViewHolder(reviewBinding)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: ShowReviewsViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }

    fun updateData(newRevies: List<Review>) {
        reviewList = newRevies
        notifyDataSetChanged()
    }
}