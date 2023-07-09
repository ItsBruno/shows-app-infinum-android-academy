package infinuma.android.shows.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.welcomeText.text = getString(R.string.welcome_text, intent.getStringExtra("email"))
    }
}