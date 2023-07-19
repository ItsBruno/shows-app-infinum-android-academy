package infinuma.android.shows.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    companion object {
        const val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
        const val PASSWORD_REGEX = "^.{6,}$"
    }

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

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
                binding.loginButton.isEnabled =
                    validateEmail(binding.emailField.text.toString().trim()) && validatePassword(
                        binding.passwordField.text.toString().trim()
                    )
            }
            passwordField.addTextChangedListener {
                binding.loginButton.isEnabled =
                    validateEmail(binding.emailField.text.toString().trim()) && validatePassword(
                        binding.passwordField.text.toString().trim()
                    )
            }

            loginButton.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToShowsFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateEmail(email: String): Boolean {
        val emailRegex = Regex(EMAIL_REGEX)
        return email.matches(emailRegex)
    }

    private fun validatePassword(password: String): Boolean {
        /*Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character and must be at least 6 characters long*/
        val passwordRegex = Regex(PASSWORD_REGEX)
        return password.matches(passwordRegex)
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