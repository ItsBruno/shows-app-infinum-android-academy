package infinuma.android.shows.ui

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")

    /*
    * 1. Put the app in background and move it back to foreground
      -> onPause() -> onStop() -> onRestart() -> onStart() -> onResume()

    * 2. Kill the app
     -> onPause() -> onStop() -> onDestroy()

    * 3. Lock the phones screen and unlock it
      -> onPause() -> onStop() -> onRestart() -> onStart() -> onResume()
    */

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.emailField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(!binding.emailField.text.toString().matches(emailRegex)) {
                    Snackbar.make(view, R.string.email_error_message, Snackbar.LENGTH_SHORT).show()
                    Log.d("snackbar", "should be shown")
                }
            }
            false
        }

        binding.emailField.addTextChangedListener {
            binding.loginButton.isEnabled =
                validateCredentialsStructure(binding.emailField.text.toString().trim(), binding.passwordField.text.toString().trim())
        }
        binding.passwordField.addTextChangedListener {
            binding.loginButton.isEnabled =
                validateCredentialsStructure(binding.emailField.text.toString().trim(), binding.passwordField.text.toString().trim())
        }

        Log.i("activityCreateCallback", "On create has been called")
    }

    override fun onStart() {
        super.onStart()
        Log.i("activityStartCallback", "On start has been called")
    }

    override fun onResume() {
        super.onResume()
        Log.i("activityResumeCallback", "On resume has been called")
    }

    override fun onPause() {
        Log.i("activityPauseCallback", "On pause has been called")
        super.onPause()
    }

    override fun onStop() {
        Log.i("activityStopCallback", "On stop has been called")
        super.onStop()
    }

    override fun onDestroy() {
        Log.i("activityDestroyCallback", "On destroy has been called")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.i("activityRestartCallback", "On restart has been called")
        super.onRestart()
    }

    private fun validateCredentialsStructure(email: String, password: String): Boolean {
        /*
                Password must contain one digit from 1 to 9, one lowercase letter, one uppercase letter, one special character, no space, and it must be at least 6 characters long.
        */
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{6,}\$")
        Log.d("email", email)
        Log.d("emailValidation", email.matches(emailRegex).toString())
        Log.d("password", password)
        Log.d("passwordValidation", password.matches(passwordRegex).toString())
        return email.matches(emailRegex) && password.matches(passwordRegex)
    }
}