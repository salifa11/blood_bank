package com.example.bloodbank.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloodbank.repository.UserRepoImpl
import com.example.bloodbank.theme.BloodbankTheme
import com.example.bloodbank.viewmodel.UserViewModel

class EditProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userId = intent.getStringExtra("USER_ID")

        setContent {
            BloodbankTheme {
                if (userId != null) {
                    EditProfileScreen(userId = userId, onSaveSuccess = { finish() })
                } else {
                    // Handle error: No user ID provided
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(userId: String, onSaveSuccess: () -> Unit) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    )

    val userState by userViewModel.user.collectAsState()
    val isLoading by userViewModel.loading.collectAsState()

    var location by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    // Fetch user data and populate fields when the screen is first shown
    LaunchedEffect(Unit) {
        userViewModel.getUserById(userId)
    }

    // Update local state once user data is loaded
    LaunchedEffect(userState) {
        userState?.let {
            location = it.location ?: ""
            dateOfBirth = it.dateOfBirth ?: ""
            age = it.age?.toString() ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { onSaveSuccess() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val updatedUser = userState?.copy(
                            location = location,
                            dateOfBirth = dateOfBirth,
                            age = age.toIntOrNull(),
                            updatedAt = System.currentTimeMillis()
                        )
                        if (updatedUser != null) {
                            userViewModel.updateUser(updatedUser)
                            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                            onSaveSuccess()
                        } else {
                            Toast.makeText(context, "Error updating profile", Toast.LENGTH_SHORT).show()
                        }
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
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                label = { Text("Date of Birth (e.g., YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
