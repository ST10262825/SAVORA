package vcmsa.projects.savorabudgetapp.data.repository

import vcmsa.projects.savorabudgetapp.data.UserDao
import vcmsa.projects.savorabudgetapp.data.User
import javax.inject.Inject

class UserRepository @Inject constructor(private val userDao: UserDao) {
    suspend fun register(user: User) = userDao.insert(user)

    suspend fun login(username: String, password: String) =
        userDao.login(username, password)

    suspend fun isUsernameTaken(username: String) =
        userDao.getUserByUsername(username) != null
}