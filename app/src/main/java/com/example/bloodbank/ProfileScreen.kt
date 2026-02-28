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
import coil3.compose.AsyncImage
import com.example.bloodbank.repository.DonationRepoImpl
import com.example.bloodbank.repository.UserRepoImpl
import com.example.bloodbank.theme.BloodbankTheme
import com.example.bloodbank.view.EditProfileActivity
import com.example.bloodbank.view.LoginActivity
import com.example.bloodbank.viewmodel.DonationViewModel
import com.example.bloodbank.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ProfileScreen(userId: String) {
    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    )
    val donationViewModel: DonationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return DonationViewModel(DonationRepoImpl()) as T
            }
        }
    )
    
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userViewModel.getUserById(userId)
        donationViewModel.getDonationsByUserId(userId)
    }

    val user by userViewModel.user.collectAsState()
    val donations by donationViewModel.donations.collectAsState()
    val isLoadingUser by userViewModel.loading.collectAsState()
    val isLoadingDonations by donationViewModel.loading.collectAsState()
    val isLoading = isLoadingUser || isLoadingDonations

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

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        user?.let { userData ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                if (!userData.profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = userData.profileImageUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userData.fullName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )

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
                        
                        if (userData.lastDonationDate != null) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(userData.lastDonationDate)
                            ProfileInfoRow(label = "Last Donated", value = formattedDate)
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        // Display actual donation count from the database
                        ProfileInfoRow(label = "Total Donations", value = donations.size.toString())
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = { 
                            val intent = Intent(context, EditProfileActivity::class.java)
                            intent.putExtra("USER_ID", userId)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit Profile")
                    }
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete Profile")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout Button
                OutlinedButton(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Text("Logout")
                }

                Spacer(modifier = Modifier.height(24.dp))
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
