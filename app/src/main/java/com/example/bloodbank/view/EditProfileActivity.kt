package com.example.bloodbank.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.bloodbank.R
import com.example.bloodbank.repository.UserRepoImpl
import com.example.bloodbank.theme.BloodbankTheme
import com.example.bloodbank.utils.ImageUtils
import com.example.bloodbank.viewmodel.UserViewModel

class EditProfileActivity : ComponentActivity() {
    private lateinit var imageUtils: ImageUtils
    // Callback to update the Composable state
    private var onImagePicked: ((Uri?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageUtils = ImageUtils(this, this)
        imageUtils.registerLaunchers { uri ->
            onImagePicked?.invoke(uri)
        }

        val userId = intent.getStringExtra("USER_ID")

        setContent {
            BloodbankTheme {
                if (userId != null) {
                    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
                    
                    // Set the activity-level callback to update the local Composable state
                    onImagePicked = { uri -> selectedImageUri = uri }

                    EditProfileScreen(
                        userId = userId,
                        selectedImageUri = selectedImageUri,
                        onPickImage = { imageUtils.launchImagePicker() },
                        onSaveSuccess = { finish() }
                    )
                } else {
                    finish()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: String,
    selectedImageUri: Uri?,
    onPickImage: () -> Unit,
    onSaveSuccess: () -> Unit
) {
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

    var fullName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        userViewModel.getUserById(userId)
    }

    LaunchedEffect(userState) {
        userState?.let {
            fullName = it.fullName
            location = it.location ?: ""
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
                        if (selectedImageUri != null) {
                            userViewModel.uploadImage(context, selectedImageUri) { url ->
                                if (url != null) {
                                    val updatedUser = userState?.copy(
                                        fullName = fullName,
                                        location = location,
                                        age = age.toIntOrNull(),
                                        profileImageUrl = url,
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    updatedUser?.let { userViewModel.updateUser(it) }
                                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                                    onSaveSuccess()
                                } else {
                                    Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            val updatedUser = userState?.copy(
                                fullName = fullName,
                                location = location,
                                age = age.toIntOrNull(),
                                updatedAt = System.currentTimeMillis()
                            )
                            updatedUser?.let { userViewModel.updateUser(it) }
                            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                            onSaveSuccess()
                        }
                    }) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { onPickImage() },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "New Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (!userState?.profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = userState?.profileImageUrl,
                        contentDescription = "Existing Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "Placeholder",
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Text("Change Profile Photo", style = MaterialTheme.typography.labelSmall)

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
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
