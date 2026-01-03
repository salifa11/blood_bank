package com.example.bloodbank

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloodbank.repository.UserRepoImpl
import com.example.bloodbank.theme.BloodbankTheme
import com.example.bloodbank.view.EditProfileActivity
import com.example.bloodbank.view.LoginActivity
import com.example.bloodbank.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userId: String) {
    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    )
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Log.d("NEWDashboardActivity", "currentUserId: $userId")

    LaunchedEffect(userId) {
        userViewModel.getUserById(userId)
    }

    val user by userViewModel.user.collectAsState()
    Log.d("usertestt", "currentUserId: $user")

    val isLoading by userViewModel.loading.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Profile") },
            text = { Text("Are you sure you want to delete your profile? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.deleteUser(userId)
                        showDeleteDialog = false
                        // Navigate to login screen after deletion
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        val intent = Intent(context, EditProfileActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        context.startActivity(intent)
                     }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Profile")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Profile", tint = Color.Red)
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            user?.let { userData ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with a profile picture
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = userData.fullName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = userData.email,
                        fontSize = 16.sp,
                        color = Color(0xFF757575)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            ProfileInfoRow(label = "Blood Group", value = userData.bloodGroup)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            ProfileInfoRow(label = "Phone", value = userData.phone)
                            userData.location?.let {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                                ProfileInfoRow(label = "Location", value = it)
                            }
                            userData.dateOfBirth?.let {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                                ProfileInfoRow(label = "Date of Birth", value = it)
                            }
                            userData.age?.let {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                                ProfileInfoRow(label = "Age", value = it.toString())
                            }
                            if(userData.lastDonationDate != null){
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                                val formattedDate = userData.lastDonationDate.let {
                                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                    sdf.format(it)
                                }
                                ProfileInfoRow(label = "Last Donated", value = formattedDate)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            ProfileInfoRow(label = "Total Donations", value = userData.totalDonations.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Medium, color = Color(0xFF212121))
        Text(text = value, color = Color(0xFF757575))
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    BloodbankTheme {
        // ProfileScreen(userId = "dummy-id")
    }
}
