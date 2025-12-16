package com.example.bloodbank.repository

import com.example.bloodbank.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserRepoImpl : UserRepo {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("users")

    override suspend fun createUser(user: User) {
        ref.child(user.uid).setValue(user)
            .addOnFailureListener { throw it }
    }

    override suspend fun getUserById(uid: String): User? =
        suspendCancellableCoroutine { cont ->
            ref.child(uid).get()
                .addOnSuccessListener { snapshot ->
                    cont.resume(snapshot.getValue(User::class.java))
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }

    override suspend fun updateUser(user: User) {
        ref.child(user.uid).updateChildren(user.toMap())
            .addOnFailureListener { throw it }
    }

    override suspend fun deleteUser(uid: String) {
        ref.child(uid).removeValue()
            .addOnFailureListener { throw it }
    }

    override suspend fun getAllDonors(): List<User> =
        suspendCancellableCoroutine { cont ->
            ref.get()
                .addOnSuccessListener { snapshot ->
                    val donors = mutableListOf<User>()
                    for (child in snapshot.children) {
                        val user = child.getValue(User::class.java)
                        if (user != null && user.isDonor) {
                            donors.add(user)
                        }
                    }
                    cont.resume(donors)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }

    override suspend fun getDonorsByBloodGroup(bloodGroup: String): List<User> =
        suspendCancellableCoroutine { cont ->
            ref.get()
                .addOnSuccessListener { snapshot ->
                    val donors = mutableListOf<User>()
                    for (child in snapshot.children) {
                        val user = child.getValue(User::class.java)
                        if (user != null &&
                            user.isDonor &&
                            user.bloodGroup == bloodGroup
                        ) {
                            donors.add(user)
                        }
                    }
                    cont.resume(donors)
                }
                .addOnFailureListener {
                    cont.resumeWithException(it)
                }
        }
}
