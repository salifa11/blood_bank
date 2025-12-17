package com.example.bloodbank.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import com.example.bloodbank.viewmodel.UserViewModel
import com.example.bloodbank.repository.UserRepoImpl
import com.example.bloodbank.model.User
import java.util.UUID
import com.example.bloodbank.view.LoginActivity


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            RegisterPageScreen()
        }
    }
}

fun isValidPassword(password: String): Boolean {
    val passwordRegex = Regex(
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{6,}$"
    )
    return passwordRegex.matches(password)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPageScreen() {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showBloodGroupMenu by remember { mutableStateOf(false) }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    val sharedPreference = context.getSharedPreferences(
        "User",
        Context.MODE_PRIVATE
    )

    val editor = sharedPreference.edit()


    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(UserRepoImpl()) as T
            }
        }
    )

    // Colors defined locally as requested
    val primaryRed = Color(0xFFD32F2F)
    val lightRedBackground = Color(0xFFFFEBEE)
    val darkText = Color(0xFF212121)
    val secondaryText = Color(0xFF757575)

    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Blood Drop",
                tint = primaryRed,
                modifier = Modifier.size(70.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = darkText
            )

            Text("Join us and save lives", color = secondaryText)

            Spacer(modifier = Modifier.height(30.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = lightRedBackground)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    InputLabel("Full Name", darkText)
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your full name") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Person, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel("Email", darkText)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Email, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel("Phone Number", darkText)
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your phone number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Phone, null) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel("Blood Group", darkText)
                    ExposedDropdownMenuBox(
                        expanded = showBloodGroupMenu,
                        onExpandedChange = { showBloodGroupMenu = !showBloodGroupMenu }
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Blood Group") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBloodGroupMenu)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            leadingIcon = {
                                Icon(Icons.Default.Bloodtype, contentDescription = null)
                            }
                        )

                        ExposedDropdownMenu(
                            expanded = showBloodGroupMenu,
                            onDismissRequest = { showBloodGroupMenu = false }
                        ) {
                            bloodGroups.forEach { group ->
                                DropdownMenuItem(
                                    text = { Text(group) },
                                    onClick = {
                                        bloodGroup = group
                                        showBloodGroupMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel("Password", darkText)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your password") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation =
                            if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            Icon(
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                modifier = Modifier.clickable {
                                    passwordVisible = !passwordVisible
                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InputLabel("Confirm Password", darkText)
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Confirm your password") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation =
                            if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        trailingIcon = {
                            Icon(
                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                null,
                                modifier = Modifier.clickable {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {

                            if (!isValidPassword(password)) {
                                Toast.makeText(
                                    context,
                                    "Password must be at least 6 characters and include a letter, number, and special character",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@Button
                            }

                            if (password != confirmPassword) {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                return@Button
                            }





                            // Generate userId (like Firebase UID)
                            val userId = UUID.randomUUID().toString()

                            // Save email/password locally
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.apply()

                            // Create User model (BloodBank)
                            val user = User(
                                uid = userId,
                                fullName = fullName,
                                email = email,
                                phone = phone,
                                bloodGroup = bloodGroup,
                                isDonor = true
                            )

                            // Call ViewModel → Repo → Firebase
                            userViewModel.createUser(user)

                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()

                            // Navigate to Login
                            context.startActivity(
                                Intent(context, LoginActivity::class.java)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryRed)
                    ) {
                        Text("Register")
                    }


                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        buildAnnotatedString {
                            append("Already have an account? ")
                            withStyle(
                                SpanStyle(
                                    color = primaryRed,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Sign In")
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clickable {
                                val intent = Intent(context, LoginActivity::class.java)
                                context.startActivity(intent)
                            },
                        color = secondaryText
                    )

                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun InputLabel(text: String, color: Color) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = color,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    // FIX 2: Removed the unresolved BloodbankTheme wrapper
    RegisterPageScreen()
}