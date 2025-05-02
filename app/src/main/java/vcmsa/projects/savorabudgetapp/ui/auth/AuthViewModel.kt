package vcmsa.projects.savorabudgetapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import vcmsa.projects.savorabudgetapp.data.User
import vcmsa.projects.savorabudgetapp.data.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    fun register(
        username: String,
        password: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch{
            if (repository.isUsernameTaken(username)) {
                onError("Username already taken")
                return@launch
            }

            repository.register(User(
                username = username,
                password = password, // In production, hash this!
                email = email
            ))
            onSuccess()
        }
    }

    fun login(
        username: String,
        password: String,
        onSuccess: (User) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.login(username, password)
            if (user != null) {
                onSuccess(user)
            } else {
                onError("Invalid credentials")
            }
        }
    }
}