package vcmsa.projects.savorabudgetapp.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import vcmsa.projects.savorabudgetapp.databinding.ActivityRegisterBinding
import vcmsa.projects.savorabudgetapp.ui.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (validateInput(username, email, password, confirmPassword)) {
                viewModel.register(
                    username = username,
                    password = password,
                    email = email,
                    onSuccess = {
                        Snackbar.make(binding.root, "Registration successful!", Snackbar.LENGTH_LONG).show()
                        finish() // Go back to login
                    },
                    onError = { error ->
                        showError(error)
                    }
                )
            }
        }
    }

    private fun validateInput(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        return when {
            username.isEmpty() -> {
                binding.etUsername.error = "Username required"
                false
            }
            email.isEmpty() -> {
                binding.etEmail.error = "Email required"
                false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Password required"
                false
            }
            password != confirmPassword -> {
                binding.etConfirmPassword.error = "Passwords don't match"
                false
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password too short (min 6 chars)"
                false
            }
            else -> true
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}