package com.example.bloodbank

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bloodbank.view.LoginActivity
import com.example.bloodbank.view.RegisterActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent

@RunWith(AndroidJUnit4::class)
class RegisterInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<RegisterActivity>()

    @Before
    fun setup() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testRegisterScreenUI_isVisible() {
        // Verify core UI elements exist
        composeRule.onNodeWithText("Create Account").assertExists()
        composeRule.onNodeWithText("Full Name").assertExists()
        composeRule.onNodeWithText("Email").assertExists()
        composeRule.onNodeWithText("Register").assertExists()
    }

    @Test
    fun testNavigationToLogin() {
        // 1. Find the text at the bottom.
        // We add performScrollTo() because the text is inside a scrollable Column 
        // and might be off-screen, causing the click to fail silently.
        composeRule.onNodeWithText("Already have an account?", substring = true)
            .performScrollTo()
            .performClick()

        // 2. Verify that the app attempts to open LoginActivity
        // We use intended() to check if the Intent was fired
        intended(hasComponent(LoginActivity::class.java.name))
    }
}
