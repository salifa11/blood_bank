package com.example.bloodbank.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.bloodbank.theme.BloodbankTheme

class LandingpageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodbankTheme {

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun LandingPagePreview() {
    BloodbankTheme {

    }
}