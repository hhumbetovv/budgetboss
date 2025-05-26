package com.theternal.alltransactions

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.usecases.GetFilteredRecordsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal // For sample data
import com.theternal.domain.entities.local.FinancialRecordEntity // For sample data
import java.util.Calendar // For activeFilterDescription test

@ExperimentalCoroutinesApi
class AllTransactionsViewModelTest {

    private lateinit var viewModel: AllTransactionsViewModel
    private val getFilteredRecordsUseCase: GetFilteredRecordsUseCase = mock()
    private val testDispatcher = StandardTestDispatcher()

    private val sampleRecord = FinancialRecordEntity(id = 1, title = "Test", amount = BigDecimal.TEN, date = System.currentTimeMillis(), isExpense = true, accountId = 1, categoryId = 1, currencyId = 1, amountText = "10")
    private val sampleRecordList = listOf<RecordEntity>(sampleRecord)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        // Default mock behavior for successful data fetch
        whenever(getFilteredRecordsUseCase.invoke(any(), any(), any())).thenReturn(flowOf(sampleRecordList))
        viewModel = AllTransactionsViewModel(getFilteredRecordsUseCase)
    }

    @Test
    fun `init loads all transactions`() = runTest {
        // ViewModel's init calls loadTransactions(null, null, null)
        viewModel.state.test {
            val initialState = awaitItem() // Initial state from createState()
            assertThat(initialState.isLoading).isFalse() // Should be false initially

            val loadingState = awaitItem() // State after onStart in loadTransactions
            assertThat(loadingState.isLoading).isTrue()
            
            val finalState = awaitItem() // State after data collection
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.transactions).isEqualTo(sampleRecordList)
            assertThat(finalState.error).isNull()
            verify(getFilteredRecordsUseCase).invoke(null, null, null)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `Event LoadTransactions reloads with current filters`() = runTest {
        // Set some initial filters
        viewModel.onEventUpdate(AllTransactionsContract.Event.SearchQueryChanged("initial"))
        advanceTimeBy(600) // Past debounce
        // Consume states from search query change
        viewModel.state.test { skipItems(3) }


        viewModel.onEventUpdate(AllTransactionsContract.Event.MonthSelected(2023, 10)) // Nov 2023
        // Consume states from month selection
        viewModel.state.test { skipItems(2) }
        
        // Clear initial invocations from setup and previous events
        org.mockito.kotlin.reset(getFilteredRecordsUseCase)
        whenever(getFilteredRecordsUseCase.invoke(any(), any(), any())).thenReturn(flowOf(sampleRecordList))


        viewModel.onEventUpdate(AllTransactionsContract.Event.LoadTransactions)
        
        viewModel.state.test {
            // Expect loading true, then loading false
            assertThat(awaitItem().isLoading).isTrue()
            val finalState = awaitItem()
            assertThat(finalState.isLoading).isFalse() 
            verify(getFilteredRecordsUseCase).invoke("initial", Pair(2023, 10), null)
            cancelAndConsumeRemainingEvents()
        }
    }


    @Test
    fun `SearchQueryChanged updates searchQuery and triggers load with debounce`() = runTest {
        viewModel.onEventUpdate(AllTransactionsContract.Event.SearchQueryChanged("test query"))
        
        var latestState = viewModel.state.value
        assertThat(latestState.searchQuery).isEqualTo("test query")
        assertThat(latestState.isLoading).isFalse() 

        advanceTimeBy(400) // Before debounce
        latestState = viewModel.state.value
        assertThat(latestState.isLoading).isFalse() // Still false

        advanceTimeBy(101) // Past debounce period (total 501ms)

        viewModel.state.test {
            val loadingState = awaitItem() // Should be loading true now
            assertThat(loadingState.isLoading).isTrue()
            assertThat(loadingState.searchQuery).isEqualTo("test query")
            
            val finalState = awaitItem() // after loading
            assertThat(finalState.searchQuery).isEqualTo("test query")
            assertThat(finalState.isLoading).isFalse()
            assertThat(finalState.transactions).isEqualTo(sampleRecordList)
            verify(getFilteredRecordsUseCase).invoke("test query", null, null)
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun `SearchQueryChanged with blank query sets searchQuery to null`() = runTest {
        viewModel.onEventUpdate(AllTransactionsContract.Event.SearchQueryChanged("  ")) // Blank query
        advanceTimeBy(501)
        
        viewModel.state.test {
            // Skip initial state + loading state
            skipItems(2) 
            val finalState = awaitItem()
            assertThat(finalState.searchQuery).isNull()
            verify(getFilteredRecordsUseCase).invoke(null, null, null)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `FilterByMonthClicked posts ShowMonthPicker effect`() = runTest {
        viewModel.effects.test {
            viewModel.onEventUpdate(AllTransactionsContract.Event.FilterByMonthClicked)
            assertThat(awaitItem()).isEqualTo(AllTransactionsContract.Effect.ShowMonthPicker)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `FilterByPeriodClicked posts ShowDateRangePicker effect`() = runTest {
        viewModel.effects.test {
            viewModel.onEventUpdate(AllTransactionsContract.Event.FilterByPeriodClicked)
            assertThat(awaitItem()).isEqualTo(AllTransactionsContract.Effect.ShowDateRangePicker)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `MonthSelected updates filterMonthYear and triggers load`() = runTest {
        val year = 2023
        val month = 10 // November (0-indexed)
        viewModel.onEventUpdate(AllTransactionsContract.Event.MonthSelected(year, month))
        
        viewModel.state.test {
            // Expect: initial state, state with filterMonthYear set, loading true, loading false
            skipItems(3) 
            val finalState = awaitItem()
            assertThat(finalState.filterMonthYear).isEqualTo(Pair(year, month))
            assertThat(finalState.filterPeriod).isNull() 
            assertThat(finalState.isLoading).isFalse()
            verify(getFilteredRecordsUseCase).invoke(null, Pair(year, month), null)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `PeriodSelected updates filterPeriod and triggers load`() = runTest {
        val startDate = System.currentTimeMillis()
        val endDate = startDate + 100000
        viewModel.onEventUpdate(AllTransactionsContract.Event.PeriodSelected(startDate, endDate))
        
        viewModel.state.test {
            skipItems(3) 
            val finalState = awaitItem()
            assertThat(finalState.filterPeriod).isEqualTo(Pair(startDate, endDate))
            assertThat(finalState.filterMonthYear).isNull() 
            assertThat(finalState.isLoading).isFalse()
            verify(getFilteredRecordsUseCase).invoke(null, null, Pair(startDate, endDate))
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `ClearSearchAndFilters resets filters and reloads all data`() = runTest {
        viewModel.onEventUpdate(AllTransactionsContract.Event.SearchQueryChanged("test"))
        advanceTimeBy(600)
        viewModel.state.test { skipItems(3) } // Consume search states

        viewModel.onEventUpdate(AllTransactionsContract.Event.MonthSelected(2023, 0))
        viewModel.state.test { skipItems(2) } // Consume month filter states
        
        viewModel.onEventUpdate(AllTransactionsContract.Event.ClearSearchAndFilters)
        
        viewModel.state.test {
            // Expect: state with filters cleared, loading true, loading false
            val clearedState = awaitItem()
            assertThat(clearedState.searchQuery).isNull()
            assertThat(clearedState.filterMonthYear).isNull()
            assertThat(clearedState.filterPeriod).isNull()
            assertThat(clearedState.activeFilterDescription).isNull()

            assertThat(awaitItem().isLoading).isTrue() // Loading state

            val finalState = awaitItem() // Final state after load
            assertThat(finalState.isLoading).isFalse()
            verify(getFilteredRecordsUseCase).invoke(null, null, null) 
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun `loadTransactions updates state correctly on error`() = runTest {
        val errorMessage = "Test error"
        whenever(getFilteredRecordsUseCase.invoke(any(), any(), any())).thenReturn(flow { throw RuntimeException(errorMessage) })
        
        viewModel.onEventUpdate(AllTransactionsContract.Event.LoadTransactions) 
        
        viewModel.state.test {
            // Expect: initial state (if not already consumed), loading true, loading false with error
            skipItems(2) // Skip initial/current and loading state
            val errorState = awaitItem()
            assertThat(errorState.isLoading).isFalse()
            assertThat(errorState.error).isEqualTo(errorMessage)
            assertThat(errorState.transactions).isEmpty() // Assuming transactions are cleared on error
            cancelAndConsumeRemainingEvents()
        }
    }
    
    @Test
    fun `activeFilterDescription updates correctly`() = runTest {
        viewModel.onEventUpdate(AllTransactionsContract.Event.SearchQueryChanged("groceries"))
        advanceTimeBy(600)
        viewModel.state.test { 
            skipItems(2) // initial, loading
            assertThat(awaitItem().activeFilterDescription).contains("Search: "groceries"")
            cancelAndConsumeRemainingEvents()
        }

        viewModel.onEventUpdate(AllTransactionsContract.Event.MonthSelected(2023, Calendar.JANUARY))
        viewModel.state.test {
            skipItems(1) // filter set state
            skipItems(1) // loading true
            val stateWithMonth = awaitItem() // loading false
            assertThat(stateWithMonth.activeFilterDescription).contains("Search: "groceries"")
            assertThat(stateWithMonth.activeFilterDescription).contains("Month: Jan 2023")
            cancelAndConsumeRemainingEvents()
        }

        viewModel.onEventUpdate(AllTransactionsContract.Event.ClearSearchAndFilters)
        viewModel.state.test {
            skipItems(2) // cleared state, loading state
            assertThat(awaitItem().activeFilterDescription).isNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
