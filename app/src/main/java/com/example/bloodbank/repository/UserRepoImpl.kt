package com.example.bloodbank.repository

import android.util.Log
import com.example.bloodbank.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class UserRepoImpl : UserRepo {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")

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
        auth.currentUser?.takeIf { it.uid == uid }?.delete()?.await()
        database.child(uid).removeValue().await()
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
}
