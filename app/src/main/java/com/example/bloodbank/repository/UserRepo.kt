package com.example.bloodbank.repository

import com.example.bloodbank.model.User

interface UserRepo {
    suspend fun createUser(user: User)
    suspend fun getUserById(uid: String): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(uid: String)
    suspend fun getAllDonors(): List<User>
    suspend fun getDonorsByBloodGroup(bloodGroup: String): List<User>
    suspend fun resetPassword(email: String)
}
