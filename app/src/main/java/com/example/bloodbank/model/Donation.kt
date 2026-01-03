package com.example.bloodbank.model

/**
 * Data model for a single blood donation event.
 */
data class Donation(
    val id: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val location: String = "",
    val bloodGroup: String = ""
)
