package infinuma.android.shows.ui.authentication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.EditorInfo
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import infinuma.android.shows.networking.ApiModule
import infinuma.android.shows.R
import infinuma.android.shows.databinding.FragmentLoginBinding

const val PREFERENCES_NAME = "Shows"

class LoginFragment : Fragment() {

    companion object {
        const val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
        const val PASSWORD_REGEX = "^.{6,}$"
        const val REMEMBER_USER = "REMEMBER_USER"
        const val USER_EMAIL = "USER_EMAIL"
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
        const val CLIENT = "CLIENT"
        const val UID = "UID"
    }

    private var _binding: FragmentLoginBinding? = null

    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private val args by navArgs<LoginFragmentArgs>()
    private val viewModel by viewModels<LoginViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        ApiModule.initRetrofit(requireContext())
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
        toggleRegisteredDisplay()
        setLoginResultAction()
        initListeners()
        animateIconAndTitle()
    }

    private fun setLoginResultAction() {
        viewModel.loginSuccessfulLiveData.observe(viewLifecycleOwner) { loginSuccessful ->
            if (loginSuccessful) {
                viewModel.sessionLiveData.observe(viewLifecycleOwner) { sessionData ->
                    handleUserLoginMemorization(sessionData.accessToken, sessionData.client, sessionData.uid)
                    navigateToShows(
                        binding.emailField.text.toString()
                    )
                }
            } else {
                Snackbar.make(binding.root, getString(R.string.login_unsuccessful_message), Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun toggleRegisteredDisplay() {
        if (args.navFromRegister) {
            with(binding) {
                loginText.text = getString(R.string.registration_successful)
                registerButton.isVisible = false
            }
        }
    }

    private fun checkUserRemembered() {
        val userRemembered = sharedPreferences.getBoolean(REMEMBER_USER, false)
        if (userRemembered) {
            navigateToShows(
                sharedPreferences.getString(USER_EMAIL, "Unknown")!!
            )
        }

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
                with(binding) {
                    viewModel.loginUser(emailField.text.toString().trim(), passwordField.text.toString().trim())
                }
            }

            registerButton.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }
        }
    }

    private fun navigateToShows(email: String) {
        val direction = LoginFragmentDirections.actionLoginFragmentToShowsFragment(email)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleUserLoginMemorization(accessToken: String, client: String, uid: String) {
        sharedPreferences.edit {
            if (binding.rememberMeCheckbox.isChecked) {
                putBoolean(REMEMBER_USER, true)
                putString(USER_EMAIL, binding.emailField.text.toString())
            }
            putString(ACCESS_TOKEN, accessToken)
            putString(CLIENT, client)
            putString(UID, uid)
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

    private fun animateIconAndTitle() {
        binding.appName.scaleX = 0f
        binding.appName.scaleY = 0f

        val iconAnimation = ObjectAnimator.ofFloat(binding.triangleIllustration, "translationY", -500f, 0f)
        iconAnimation.duration = 1000
        iconAnimation.interpolator = BounceInterpolator()

        val titleAnimationX = ObjectAnimator.ofFloat(binding.appName, "scaleX", 1f)
        titleAnimationX.duration = 1000
        titleAnimationX.interpolator = OvershootInterpolator()

        val titleAnimationY = ObjectAnimator.ofFloat(binding.appName, "scaleY", 1f)
        titleAnimationY.duration = 1000
        titleAnimationY.interpolator = OvershootInterpolator()


        AnimatorSet().apply {
            play(iconAnimation)
            play(titleAnimationX).with(titleAnimationY)
            play(titleAnimationX).after(iconAnimation)
        }.start()

    }
}