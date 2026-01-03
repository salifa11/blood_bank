package com.example.bloodbank

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloodbank.model.Donation
import com.example.bloodbank.repository.DonationRepoImpl
import com.example.bloodbank.repository.UserRepoImpl
import com.example.bloodbank.viewmodel.DonationViewModel
import com.example.bloodbank.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Donate() {
    val context = LocalContext.current

    // ViewModels
    val donationViewModel: DonationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DonationViewModel(DonationRepoImpl()) as T
            }
        }
    )
    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    )

    // State for the form fields
    var location by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    // State from ViewModels
    val isLoading by donationViewModel.loading.collectAsState()
    val donationAdded by donationViewModel.donationAdded.collectAsState()
    val currentUser by userViewModel.user.collectAsState()

    // Get current user's ID
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Fetch user details when the screen is shown
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            userViewModel.getUserById(currentUserId)
        }
    }

    // Show a message when donation is added successfully
    LaunchedEffect(donationAdded) {
        if (donationAdded) {
            Toast.makeText(context, "Donation record added!", Toast.LENGTH_SHORT).show()
            location = "" // Clear the form
            selectedDateMillis = System.currentTimeMillis()
            donationViewModel.resetDonationAddedState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Record a New Donation",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Display user info if available
        currentUser?.let {
            InfoRow("Name", it.fullName)
            InfoRow("Blood Group", it.bloodGroup)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            label = { Text("Donation Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.clickable { showDatePicker = true }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Donation Location") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (location.isBlank()) {
                    Toast.makeText(context, "Please enter a location", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (currentUser == null) {
                    Toast.makeText(context, "Could not verify user.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val newDonation = Donation(
                    id = UUID.randomUUID().toString(),
                    userId = currentUser!!.uid,
                    location = location,
                    bloodGroup = currentUser!!.bloodGroup,
                    date = selectedDateMillis
                )

                donationViewModel.addDonation(newDonation)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && currentUser != null
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            } else {
                Text("Add Donation Record")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold)
        Text(text = value)
    }
}
