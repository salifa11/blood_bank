package com.example.bloodbank.repository

import android.content.Context
import android.net.Uri
import com.example.bloodbank.model.User

interface UserRepo {
    suspend fun createUser(user: User)
    suspend fun getUserById(uid: String): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(uid: String)
    suspend fun getAllDonors(): List<User>
    suspend fun getDonorsByBloodGroup(bloodGroup: String): List<User>
    suspend fun resetPassword(email: String)
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit)

    // Cloudinary Image Upload Functions
    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)
    fun getFileNameFromUri(context: Context, uri: Uri): String?
}
