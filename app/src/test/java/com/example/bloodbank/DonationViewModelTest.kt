package com.example.bloodbank

import com.example.bloodbank.model.Donation
import com.example.bloodbank.repository.DonationRepo
import com.example.bloodbank.viewmodel.DonationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DonationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repo: DonationRepo
    private lateinit var viewModel: DonationViewModel

    @Before
    fun setup() {
        // Set the main dispatcher to our test dispatcher
        Dispatchers.setMain(testDispatcher)
        repo = mock()
        viewModel = DonationViewModel(repo)
    }

    @After
    fun tearDown() {
        // Reset the main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun add_donation_success_test() = runTest {
        val donation = Donation(
            id = "test-id",
            userId = "user-123",
            location = "New York",
            bloodGroup = "O+"
        )

        // Execution: Call the function to add a donation
        viewModel.addDonation(donation)
        
        // Advance the dispatcher to allow the coroutine to complete
        testDispatcher.scheduler.advanceUntilIdle()

        // Verification: Ensure the repository was called correctly
        verify(repo).addDonation(donation)
        
        // Verification: Check if the state was updated correctly
        assertTrue(viewModel.donationAdded.value)
    }
}
