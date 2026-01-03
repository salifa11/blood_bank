package com.example.bloodbank.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
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

// Data class for navigation items
data class NavItem(val icon: Int, val label: String)

// Unified DashboardBody composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardBody(
    isPreview: Boolean = false // Flag to handle preview-safe code
) {
    // Safe activity reference
    val activity = if (!isPreview) LocalContext.current as? Activity else null

    var selectedIndex by remember { mutableStateOf(0) }

    // Hold the current user ID in a state that triggers recomposition
    var currentUserId by remember { mutableStateOf( FirebaseAuth.getInstance().currentUser?.uid) }

    // Listen for changes in auth state
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

    val topBarBackgroundColor = Color.Red
    val topBarContentColor = Color.White

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bloodlink") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarBackgroundColor,
                    titleContentColor = topBarContentColor,
                    navigationIconContentColor = topBarContentColor,
                    actionIconContentColor = topBarContentColor
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { activity?.finish() } // no-op in preview
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_settings_24),
                            contentDescription = "Settings"
                        )
                    }
                },
                actions = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "Location Dropdown",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.baseline_remove_red_eye_24),
                        contentDescription = "View Toggle"
                    )
                }
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
