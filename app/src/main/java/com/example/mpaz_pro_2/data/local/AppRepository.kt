package com.example.mpaz_pro_2.data.local
import com.example.mpaz_pro_2.UserDao
import com.example.mpaz_pro_2.UserEntity
class AppRepository(private val userDao: UserDao) {
    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }
    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAllUsers()
    }
}