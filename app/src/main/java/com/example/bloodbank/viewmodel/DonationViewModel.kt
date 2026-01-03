package com.example.bloodbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloodbank.model.Donation
import com.example.bloodbank.repository.DonationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DonationViewModel(
    private val donationRepo: DonationRepo
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _donationAdded = MutableStateFlow(false)
    val donationAdded: StateFlow<Boolean> = _donationAdded

    private val _donations = MutableStateFlow<List<Donation>>(emptyList())
    val donations: StateFlow<List<Donation>> = _donations

    fun addDonation(donation: Donation) {
        viewModelScope.launch {
            _loading.value = true
            try {
                donationRepo.addDonation(donation)
                _donationAdded.value = true
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun getDonationsByUserId(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _donations.value = donationRepo.getDonationsByUserId(userId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteDonation(donationId: String, userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                donationRepo.deleteDonation(donationId)
                // Refresh the list after deletion
                _donations.value = donationRepo.getDonationsByUserId(userId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateDonation(donation: Donation, userId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                donationRepo.updateDonation(donation)
                // Refresh the list after update
                _donations.value = donationRepo.getDonationsByUserId(userId)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun resetDonationAddedState() {
        _donationAdded.value = false
    }
}
