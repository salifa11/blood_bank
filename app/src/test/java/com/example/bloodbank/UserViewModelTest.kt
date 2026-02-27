package com.example.bloodbank

import com.example.bloodbank.repository.UserRepo
import com.example.bloodbank.viewmodel.UserViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class UserViewModelTest {

    @Test
    fun login_success_test() {
        // 1. Setup: Create a mock of UserRepo and initialize the ViewModel
        val repo = mock<UserRepo>()
        val viewModel = UserViewModel(repo)

        // 2. Define Mock Behavior: When repo.login is called, trigger the callback with success
        doAnswer { invocation ->
            // The callback is the 3rd argument (index 2)
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(2)
            callback(true, "Login success")
            null
        }.`when`(repo).login(eq("test@gmail.com"), eq("123456"), any())

        // 3. Variables to capture the results from the callback
        var successResult = false
        var messageResult = ""

        // 4. Execution: Call the login function on the ViewModel
        viewModel.login("test@gmail.com", "123456") { success, msg ->
            successResult = success
            messageResult = msg
        }

        // 5. Verification: Assert that the captured results are correct
        assertTrue(successResult)
        assertEquals("Login success", messageResult)

        // 6. Verification: Ensure that the repository's login function was actually called
        verify(repo).login(eq("test@gmail.com"), eq("123456"), any())
    }
}