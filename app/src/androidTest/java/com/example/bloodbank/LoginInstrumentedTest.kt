package com.example.bloodbank

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bloodbank.view.LoginActivity
import com.example.bloodbank.view.RegisterActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import org.junit.After
import org.junit.Before

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    // Starts the LoginActivity before the test
    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testNavigationToRegister() {
        // 1. Find the text "Register" (which is in your 'Don't have an account?' row)
        // substring = true is used because it's part of a larger string
        composeRule.onNodeWithText("Register", substring = true, ignoreCase = true)
            .performClick()

        // 2. Verify that the app tries to open RegisterActivity
        intended(hasComponent(RegisterActivity::class.java.name))
    }

    @Test
    fun testLoginButtonIsVisible() {
        // Simple check to ensure the "Sign In" button is displayed on screen
        composeRule.onNodeWithText("Sign In", ignoreCase = true).assertExists()
    }
}