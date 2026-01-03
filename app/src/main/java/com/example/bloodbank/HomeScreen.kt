package com.example.bloodbank

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bloodbank.model.Donation
import com.example.bloodbank.repository.DonationRepoImpl
import com.example.bloodbank.repository.UserRepoImpl
import com.example.bloodbank.view.EditDonationActivity
import com.example.bloodbank.viewmodel.DonationViewModel
import com.example.bloodbank.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
    }
}

@Composable
fun HomeScreen() {
    val context = LocalContext.current
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

    val currentUser by userViewModel.user.collectAsState()
    val donations by donationViewModel.donations.collectAsState()
    val isUserLoading by userViewModel.loading.collectAsState()
    val isDonationsLoading by donationViewModel.loading.collectAsState()
    val isLoading = isUserLoading || isDonationsLoading

    var showDeleteDialog by remember { mutableStateOf<Donation?>(null) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            userViewModel.getUserById(currentUserId)
            donationViewModel.getDonationsByUserId(currentUserId)
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Donation") },
            text = { Text("Are you sure you want to delete this donation record?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentUserId != null) {
                            donationViewModel.deleteDonation(showDeleteDialog!!.id, currentUserId)
                        }
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {

        //  Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFD32F2F))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Welcome Back,",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = currentUser?.fullName ?: "User",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoCard(title = currentUser?.bloodGroup ?: "N/A", subtitle = "Your Blood Type")
                    InfoCard(title = donations.size.toString(), subtitle = "Times Donated")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ⚡ Quick Actions
        SectionTitle("Quick Actions")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCard("Find donors", Icons.Default.Search)
            ActionCard("Blood Banks", Icons.Default.Store)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🩸 Services
        SectionTitle("Services")

        ServiceItem(
            icon = Icons.Default.Search,
            title = "Search Donors",
            desc = "Find blood donors by blood type and location"
        )

        ServiceItem(
            icon = Icons.Default.Store,
            title = "Search Blood Banks",
            desc = "Locate nearby blood banks and check availability"
        )

        ServiceItem(
            icon = Icons.Default.Favorite,
            title = "Become a donor",
            desc = "Register as donor and save lives"
        )

        ServiceItem(
            icon = Icons.Default.LocationOn,
            title = "Nearby Camps",
            desc = "Find donation camps near you"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 📜 Donation History
        SectionTitle("Donation History")

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (donations.isEmpty()) {
            Text(
                text = "No donation history yet.",
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                color = Color.Gray
            )
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                donations.forEach { donation ->
                    DonationHistoryItem(
                        donation = donation, 
                        onDelete = { showDeleteDialog = it },
                        onEdit = { 
                            val intent = Intent(context, EditDonationActivity::class.java)
                            intent.putExtra("DONATION", Gson().toJson(it))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DonationHistoryItem(donation: Donation, onDelete: (Donation) -> Unit, onEdit: (Donation) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val formattedDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(donation.date))
            Text(text = formattedDate, fontWeight = FontWeight.SemiBold)
            Text(text = donation.location, color = Color.Gray)
            Row {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Donation",
                    modifier = Modifier.clickable { onEdit(donation) }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Donation",
                    tint = Color.Red,
                    modifier = Modifier.clickable { onDelete(donation) }
                )
            }
        }
    }
}

@Composable
fun InfoCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEF5350)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ActionCard(title: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun ServiceItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F1)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFFD32F2F))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(desc, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
