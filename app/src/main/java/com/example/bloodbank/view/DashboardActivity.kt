package com.example.bloodbank.view

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.bloodbank.Donate
import com.example.bloodbank.HomeScreen
import com.example.bloodbank.ProfileScreen
import com.example.bloodbank.R
import com.example.bloodbank.SearchDonors
import com.example.bloodbank.theme.ui.theme.BloodbankTheme
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodbankTheme {
                DashboardBody()
            }
        }
    }
}

data class NavItem(val icon: Int, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(
    isPreview: Boolean = false 
) {
    val context = LocalContext.current
    val activity = if (!isPreview) context as? Activity else null

    var selectedIndex by remember { mutableStateOf(0) }
    var currentUserId by remember { mutableStateOf(if (isPreview) "dummy-id" else FirebaseAuth.getInstance().currentUser?.uid) }

    if (!isPreview) {
        DisposableEffect(FirebaseAuth.getInstance()) {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                currentUserId = auth.currentUser?.uid
            }
            FirebaseAuth.getInstance().addAuthStateListener(listener)
            onDispose {
                FirebaseAuth.getInstance().removeAuthStateListener(listener)
            }
        }
    }

    val navList = listOf(
        NavItem(R.drawable.baseline_home_24, "Home"),
        NavItem(R.drawable.baseline_search_24, "Search"),
        NavItem(R.drawable.heart, "Donate"),
        NavItem(R.drawable.baseline_person_24, "Profile")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bloodlink") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                navList.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(item.icon), contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> SearchDonors()
                2 -> Donate()
                3 -> {
                    if (currentUserId != null) {
                        ProfileScreen(userId = currentUserId!!)
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                            Text("You need to be logged in to see your profile")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    BloodbankTheme {
        DashboardBody(isPreview = true)
    }
}
