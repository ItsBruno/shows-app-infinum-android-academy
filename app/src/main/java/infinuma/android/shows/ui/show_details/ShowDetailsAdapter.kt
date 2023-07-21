package infinuma.android.shows.ui.show_details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import infinuma.android.shows.databinding.ReviewCardBinding
import infinuma.android.shows.model.ShowReview

class ShowDetailsAdapter(private var reviewList: MutableList<ShowReview>) :
    RecyclerView.Adapter<ShowDetailsAdapter.ShowReviewsViewHolder>() {

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

        //this is broken, if the items width is set to match_parent, the items width will be compressed which is unexpected, as the width
        //should be the same as the parents or the recycler views, the method above fixes the issue
        //val reviewBinding = ReviewCardBinding.inflate(LayoutInflater.from(parent.context))

        return ShowReviewsViewHolder(reviewBinding)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    override fun onBindViewHolder(holder: ShowReviewsViewHolder, position: Int) {
        holder.bind(reviewList[position])
    }
}