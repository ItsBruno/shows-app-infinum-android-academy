package infinuma.android.shows.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.snackbar.Snackbar
import infinuma.android.shows.R
import infinuma.android.shows.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

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
                if (!validateEmail(binding.emailField.text.toString().trim())) {
                    Log.d("emailHeight", "Email height before error: ${binding.emailFieldLayout.height}")
                    //required for the error message to be displayed
                    binding.emailFieldLayout.layoutParams.height = WRAP_CONTENT

                    binding.emailFieldLayout.error = getString(R.string.email_error_message)
                    binding.emailFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)

                    Snackbar.make(view, R.string.email_error_message, Snackbar.LENGTH_SHORT).show()

                    Log.d("snackbar", "should be shown")
                } else {
                    Log.d("emailHeight", "Email height after error resolution: ${binding.emailFieldLayout.height}")
                    //sets the UI back to the default look when the email error message disappears and leaves empty space
                    binding.emailFieldLayout.layoutParams.height = 168
                    binding.emailFieldLayout.error = null
                }
            }
            false
        }

        binding.passwordFieldLayout.setErrorIconOnClickListener {
            binding.passwordFieldLayout.error = null
        }

        binding.passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!validatePassword(binding.passwordField.text.toString().trim())) {

                    binding.passwordFieldLayout.error = getString(R.string.password_error_message)
                    binding.passwordFieldLayout.setErrorTextAppearance(R.style.PasswordErrorTextAppearance)

                    Log.d("snackbar", "should be shown")
                } else {
                    binding.passwordFieldLayout.error = null
                }
            }
            false
        }


        binding.emailField.addTextChangedListener {
            binding.loginButton.isEnabled =
                validateEmail(binding.emailField.text.toString().trim()) && validatePassword(binding.passwordField.text.toString().trim())
        }
        binding.passwordField.addTextChangedListener {
            binding.loginButton.isEnabled =
                validateEmail(binding.emailField.text.toString().trim()) && validatePassword(binding.passwordField.text.toString().trim())
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailField.text

            //explicit intent
            val intent = Intent(this, WelcomeActivity::class.java)

            //implicit intent
            /*val intent = Intent()
            intent.action = "LaunchWelcome"
            intent.addCategory("android.intent.category.DEFAULT")*/

            intent.putExtra("email", email?.substring(0, email.indexOf('@')))

            startActivity(intent)
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

    private fun validateEmail(email: String): Boolean {
        val emailRegex = Regex("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$")
        return email.matches(emailRegex)
    }

    private fun validatePassword(password: String): Boolean {
        /*Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and must be at least 6 characters long*/
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{6,}\$")
        return password.matches(passwordRegex)
    }
}