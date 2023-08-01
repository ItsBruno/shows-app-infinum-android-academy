package infinuma.android.shows.ui.show_details

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import infinuma.android.shows.R
import infinuma.android.shows.databinding.CompoundToolbarBinding

class CompoundToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0
) : MaterialToolbar(context, attrs, defStyleAttrs) {
    private var binding: CompoundToolbarBinding

    init {
        binding = CompoundToolbarBinding.inflate(LayoutInflater.from(context), this)

    }

    fun setProfileIconUri(imageUri: Uri) {
        Glide.with(context).load(imageUri).placeholder(R.drawable.ic_profile_placeholder).into(binding.profile)
    }

    fun setOnProfilePictureClickListener(callback: (View) -> Unit) {
        binding.profile.setOnClickListener(callback)
    }
}