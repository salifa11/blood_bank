package com.example.bloodbank.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloodbank.model.Donation
import com.example.bloodbank.repository.DonationRepoImpl
import com.example.bloodbank.theme.BloodbankTheme
import com.example.bloodbank.viewmodel.DonationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class EditDonationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val donationJson = intent.getStringExtra("DONATION")
        val donation = Gson().fromJson(donationJson, Donation::class.java)

        setContent {
            BloodbankTheme {
                if (donation != null) {
                    EditDonationScreen(donation = donation, onSaveSuccess = { finish() })
                } else {
                    // Handle error: No donation data provided
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDonationScreen(donation: Donation, onSaveSuccess: () -> Unit) {
    val context = LocalContext.current
    val donationViewModel: DonationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DonationViewModel(DonationRepoImpl()) as T
            }
        }
    )

    var location by remember { mutableStateOf(donation.location) }
    var selectedDateMillis by remember { mutableStateOf(donation.date) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isLoading by donationViewModel.loading.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Donation") },
                navigationIcon = {
                    IconButton(onClick = { onSaveSuccess() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val updatedDonation = donation.copy(
                            location = location,
                            date = selectedDateMillis
                        )
                        if (currentUserId != null) {
                            donationViewModel.updateDonation(updatedDonation, currentUserId)
                        }
                        Toast.makeText(context, "Donation Updated", Toast.LENGTH_SHORT).show()
                        onSaveSuccess()
                    }) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Donation Location") },
                modifier = Modifier.fillMaxWidth()
            )
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
