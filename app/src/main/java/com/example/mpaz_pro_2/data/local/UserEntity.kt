package com.example.mpaz_pro_2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val username: String,
    val name: String,
    val password: String,
    val role: String,
    val profileImageUri: String? = null
)