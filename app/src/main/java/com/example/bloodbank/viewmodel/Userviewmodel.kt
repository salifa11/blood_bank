package com.example.bloodbank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bloodbank.model.User
import com.example.bloodbank.repository.UserRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepo: UserRepo
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _donors = MutableStateFlow<List<User>>(emptyList())
    val donors: StateFlow<List<User>> = _donors

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createUser(user: User) {
        viewModelScope.launch {
            _loading.value = true
            try {
                userRepo.createUser(user)
                _user.value = user
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun getUserById(uid: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _user.value = userRepo.getUserById(uid)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _loading.value = true
            try {
                userRepo.updateUser(user)
                _user.value = user
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                userRepo.deleteUser(uid)
                _user.value = null
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadAllDonors() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _donors.value = userRepo.getAllDonors()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _loading.value = false
            }
        }
    }

    fun loadDonorsByBloodGroup(bloodGroup: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _donors.value = userRepo.getDonorsByBloodGroup(bloodGroup)
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
}
