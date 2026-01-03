package com.example.bloodbank.view


import android.app.Activity
import android.widget.Toast

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

// --- COLOR DEFINITIONS (Replaces Theme Constants) ---
val PrimaryRed = Color(0xFFD32F2F)      // Red for primary actions
val DarkText = Color(0xFF1E1E1E)        // Dark color for headings and input labels
val SecondaryText = Color(0xFF666666)   // Gray color for secondary text
val LightRedBackground = Color(0xFFFFEDED) // Light red/pink for card background
// ----------------------------------------------------



class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Theme wrapper removed as requested
            LoginPageScreen()
        }
    }
}


@Composable
fun LoginPageScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()


    Scaffold(containerColor = Color.White) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(60.dp))

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Blood Drop",
                tint = PrimaryRed,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Text(
                text = "Sign in to continue",
                color = SecondaryText
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightRedBackground)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    InputLabel("Email")
                    EmailInputField(email) { email = it }

                    Spacer(modifier = Modifier.height(20.dp))

                    InputLabel("Password")
                    PasswordInputField(
                        password = password,
                        onValueChange = { password = it },
                        isVisible = passwordVisible,
                        onVisibilityToggle = { passwordVisible = !passwordVisible }
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = {

                            if (email.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true

                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(context as Activity) { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(context, DashboardActivity::class.java)
                                        // Clear back stack and launch new task
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(intent)

                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Authentication failed: ${task.exception?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryRed),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        }
                        else {
                            Text("Sign In")
                        }
                    }



                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Forgot Password?",
                        color = PrimaryRed,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                Toast.makeText(
                                    context,
                                    "Forgot password clicked",
                                    Toast.LENGTH_SHORT
                                ).show()
                                 context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
                            }
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("Don't have an account? ")
                                withStyle(
                                    SpanStyle(
                                        color = PrimaryRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    // NOTE: Changed from RegisterPage to RegisterActivity
                                    append("Register")
                                }
                            },
                            modifier = Modifier.clickable {
                                context.startActivity(
                                    // Intent targets RegisterActivity which is the name of the file you provided
                                    Intent(context, RegisterActivity::class.java)
                                )
                            },
                            color = SecondaryText
                        )
                    }
                }
            }
        }
    }
}


// ---------- REUSABLE COMPONENTS ----------
@Composable
fun InputLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = DarkText, // Uses local color
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun EmailInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Enter your email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email"
            )
        }
    )
}

@Composable
fun PasswordInputField(
    password: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit
) {
    OutlinedTextField(
        value = password,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Enter your password") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation =
            if (isVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Password"
            )
        },
        trailingIcon = {
            Icon(
                imageVector =
                    if (isVisible) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff,
                contentDescription = "Toggle password",
                modifier = Modifier.clickable { onVisibilityToggle() }
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    // Theme wrapper removed as requested
    LoginPageScreen()
}
