package infinuma.android.shows.ui.show_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.databinding.ReviewCardBinding
import infinuma.android.shows.model.ShowReview

class ShowDetailsAdapter(private var reviewList: List<ShowReview>) : RecyclerView.Adapter<ShowDetailsAdapter.ShowReviewsViewHolder>() {

    inner class ShowReviewsViewHolder(private var binding: ReviewCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: ShowReview) {
            with(binding) {
                profileName.text = review.name
                rating.text = review.rating.toString()
                reviewComment.text = review.comment
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
}