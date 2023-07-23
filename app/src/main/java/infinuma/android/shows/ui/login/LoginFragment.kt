package infinuma.android.shows.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentLoginBinding

const val PREFERENCES_NAME = "Shows"

class LoginFragment : Fragment() {

    companion object {
        const val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
        const val PASSWORD_REGEX = "^.{6,}$"
        const val REMEMBER_USER = "REMEMBER_USER"
        const val USER_EMAIL = "USER_EMAIL"
        const val USER_PASSWORD = "USER_PASSWORD"
    }

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        checkUserRemembered()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun checkUserRemembered() {
        val userRemembered = sharedPreferences.getBoolean(REMEMBER_USER, false)
        if (userRemembered) navigateToShows(
            sharedPreferences.getString(USER_EMAIL, "Unknown")!!, sharedPreferences.getString(USER_PASSWORD, "")!!
        )
    }

    private fun initListeners() {
        with(binding) {

            emailField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updateEmailField()
                }
            }

            passwordFieldLayout.setErrorIconOnClickListener {
                binding.passwordFieldLayout.error = null
            }

            passwordField.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    updatePasswordField()
                }
            }

            /* clicking the login button does not make the password field lose focus
            so this method should still be present to update the password field if the user instead presses done*/
            passwordField.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updatePasswordField()
                }
                false
            }

            emailField.addTextChangedListener {
                binding.loginButton.isEnabled = validateCredentials()
            }
            passwordField.addTextChangedListener {
                binding.loginButton.isEnabled = validateCredentials()
            }

            loginButton.setOnClickListener {
                handleUserLoginMemorization()
                navigateToShows(binding.emailField.text.toString(), binding.passwordField.text.toString())
            }
        }
    }

    private fun navigateToShows(email: String, password: String) {
        val direction = LoginFragmentDirections.actionLoginFragmentToShowsFragment(email, password)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleUserLoginMemorization() {
        val isValidLogin = validateCredentials()
        if (binding.rememberMeCheckbox.isChecked and isValidLogin) {
            sharedPreferences.edit {
                putBoolean(REMEMBER_USER, true)
                putString(USER_EMAIL, binding.emailField.text.toString())
                putString(USER_PASSWORD, binding.passwordField.text.toString())
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        val emailRegex = Regex(EMAIL_REGEX)
        return email.matches(emailRegex)
    }

    private fun validatePassword(password: String): Boolean {/*Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and must be at least 6 characters long*/
        val passwordRegex = Regex(PASSWORD_REGEX)
        return password.matches(passwordRegex)
    }

    private fun validateCredentials(): Boolean {
        return validateEmail(binding.emailField.text.toString().trim()) && validatePassword(
            binding.passwordField.text.toString().trim()
        )
    }

    private fun updatePasswordField() {
        if (!validatePassword(binding.passwordField.text.toString().trim())) {

            binding.passwordFieldLayout.error = getString(R.string.password_error_message)
            binding.passwordFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)

            Log.d("snackbar", "should be shown")
        } else {
            binding.passwordFieldLayout.error = null
        }
    }

    private fun updateEmailField() {
        if (!validateEmail(binding.emailField.text.toString().trim())) {

            binding.emailFieldLayout.error = getString(R.string.email_error_message)
            binding.emailFieldLayout.setErrorTextAppearance(R.style.ErrorTextAppearance)

        } else {
            binding.emailFieldLayout.error = null
        }
    }
}