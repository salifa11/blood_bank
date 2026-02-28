package com.example.bloodbank.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.bloodbank.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import java.util.concurrent.Executors

class UserRepoImpl : UserRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dsgquvmlc",
            "api_key" to "975889222798434",
            "api_secret" to "fdLqGUe4UiyRHm3lmMFJA8HZmWs"
        )
    )

    override suspend fun createUser(user: User) {
        database.child(user.uid).setValue(user).await()
    }

    override suspend fun getUserById(uid: String): User? {
        return database.child(uid).get().await().getValue(User::class.java)
    }

    override suspend fun updateUser(user: User) {
        database.child(user.uid).setValue(user).await()
    }

    override suspend fun deleteUser(uid: String) {
        // It is critical to delete the auth user first.
        auth.currentUser?.takeIf { it.uid == auth.currentUser?.uid }?.delete()?.await()
        database.child(auth.currentUser?.uid ?: "").removeValue().await()
    }

    override suspend fun getAllDonors(): List<User> {
        val snapshot = database.orderByChild("donor").equalTo(true).get().await()
        return snapshot.children.mapNotNull { it.getValue(User::class.java) }
    }

    override suspend fun getDonorsByBloodGroup(bloodGroup: String): List<User> {
        val snapshot = database.orderByChild("bloodGroup").equalTo(bloodGroup).get().await()
        return snapshot.children.mapNotNull { it.getValue(User::class.java) }
    }

    override suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login successful")
                } else {
                    callback(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?

                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}
