package com.theternal.record_details

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.theternal.domain.usecases.DeleteRecordUseCase
import com.theternal.domain.usecases.GetRecordUseCase
import com.theternal.domain.usecases.UpdateRecordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class RecordDetailsViewModelTest {

    private lateinit var viewModel: RecordDetailsViewModel
    private val getRecordUseCase: GetRecordUseCase = mock()
    private val deleteRecordUseCase: DeleteRecordUseCase = mock()
    private val updateRecordUseCase: UpdateRecordUseCase = mock()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RecordDetailsViewModel(
            getRecordUseCase,
            deleteRecordUseCase,
            updateRecordUseCase
        )
    }

    @Test
    fun `when SetNote event is received, state should update with new note`() = runTest {
        // Given
        val initialNote = "Initial Note"
        val newNote = "Updated Note"
        viewModel.onEventUpdate(RecordDetailsContract.Event.SetNote(initialNote))
        
        // When
        viewModel.onEventUpdate(RecordDetailsContract.Event.SetNote(newNote))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.note).isEqualTo(newNote)
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun `when SetNote event is received with null, state should update with null note`() = runTest {
        // Given
        val initialNote = "Initial Note"
        viewModel.onEventUpdate(RecordDetailsContract.Event.SetNote(initialNote))

        // When
        viewModel.onEventUpdate(RecordDetailsContract.Event.SetNote(null))

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertThat(state.note).isNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
