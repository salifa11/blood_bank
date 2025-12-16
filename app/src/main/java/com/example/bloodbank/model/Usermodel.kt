package com.example.bloodbank.model

/**
 * User data model representing a blood bank user
 */
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val isDonor: Boolean = false,
    val profileImageUrl: String? = null,
    val lastDonationDate: Long? = null,
    val totalDonations: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "bloodGroup" to bloodGroup,
            "isDonor" to isDonor,
            "profileImageUrl" to profileImageUrl,
            "lastDonationDate" to lastDonationDate,
            "totalDonations" to totalDonations,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}