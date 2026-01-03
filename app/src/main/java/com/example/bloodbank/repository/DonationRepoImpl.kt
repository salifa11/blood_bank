package com.example.bloodbank.repository

import com.example.bloodbank.model.Donation
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class DonationRepoImpl : DonationRepo {

    private val database = FirebaseDatabase.getInstance().getReference("donations")

    override suspend fun addDonation(donation: Donation) {
        database.child(donation.id).setValue(donation).await()
    }

    override suspend fun getDonationsByUserId(userId: String): List<Donation> {
        val snapshot = database.orderByChild("userId").equalTo(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(Donation::class.java) }
    }

    override suspend fun deleteDonation(donationId: String) {
        database.child(donationId).removeValue().await()
    }

    override suspend fun updateDonation(donation: Donation) {
        database.child(donation.id).setValue(donation).await()
    }
}
