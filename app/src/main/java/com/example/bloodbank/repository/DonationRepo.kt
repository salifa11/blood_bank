package com.example.bloodbank.repository

import com.example.bloodbank.model.Donation

interface DonationRepo {
    suspend fun addDonation(donation: Donation)
    suspend fun getDonationsByUserId(userId: String): List<Donation>
    suspend fun deleteDonation(donationId: String)
    suspend fun updateDonation(donation: Donation)
}
