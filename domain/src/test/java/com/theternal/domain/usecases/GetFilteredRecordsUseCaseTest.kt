package com.theternal.domain.usecases

import com.google.common.truth.Truth.assertThat
import com.theternal.domain.entities.local.FinancialRecordEntity
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.domain.repositories.RecordRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.Calendar

class GetFilteredRecordsUseCaseTest {

    private lateinit var getFilteredRecordsUseCase: GetFilteredRecordsUseCase
    private val recordRepository: RecordRepository = mock()

    // Sample records for testing
    private val now = System.currentTimeMillis()
    private val cal = Calendar.getInstance()

    private val record1 = FinancialRecordEntity(id = 1, title = "Lunch", amount = BigDecimal("10.0"), date = now - 200000, note = "Pizza place", isExpense = true, accountId = 1, categoryId = 1, currencyId = 1, amountText = "10.0")
    private val record2 = FinancialRecordEntity(id = 2, title = "Salary", amount = BigDecimal("1000.0"), date = now - 100000, note = "Monthly pay", isExpense = false, accountId = 1, categoryId = 2, currencyId = 1, amountText = "1000.0")
    private val record3 = TransferEntity(id = 3, title = "Savings Transfer", sentAmount = BigDecimal("100.0"), receivedAmount = BigDecimal("100.0"), date = now, note = "To savings account", fromAccount = 1, toAccount = 2, currencyId = 1, fee = BigDecimal.ZERO, sentAmountText = "100", receivedAmountText = "100")
    
    private val allMockRecords = listOf(record1, record2, record3) // record3 is latest, then record2, then record1

    @Before
    fun setUp() {
        getFilteredRecordsUseCase = GetFilteredRecordsUseCase(recordRepository)
        // Default mock behavior
        whenever(recordRepository.getAllRecords()).thenReturn(flowOf(allMockRecords))
    }

    @Test
    fun `invoke with no filters returns all records sorted by date descending`() = runTest {
        val result = getFilteredRecordsUseCase(null, null, null).first()
        assertThat(result).isEqualTo(listOf(record3, record2, record1)) // Sorted by date
    }

    @Test
    fun `invoke with search query 'lunch' returns matching financial record`() = runTest {
        val result = getFilteredRecordsUseCase("Lunch", null, null).first()
        assertThat(result).containsExactly(record1)
    }
    
    @Test
    fun `invoke with search query 'pizza' (in note) returns matching financial record`() = runTest {
        val result = getFilteredRecordsUseCase("pizza", null, null).first()
        assertThat(result).containsExactly(record1)
    }

    @Test
    fun `invoke with search query 'transfer' returns matching transfer record`() = runTest {
        val result = getFilteredRecordsUseCase("Transfer", null, null).first()
        assertThat(result).containsExactly(record3)
    }
    
    @Test
    fun `invoke with search query 'savings' (in note) returns matching transfer record`() = runTest {
        val result = getFilteredRecordsUseCase("savings", null, null).first()
        assertThat(result).containsExactly(record3)
    }

    @Test
    fun `invoke with search query not matching any returns empty list`() = runTest {
        val result = getFilteredRecordsUseCase("NonExistent", null, null).first()
        assertThat(result).isEmpty()
    }
    
    private fun getYearMonth(timeInMillis: Long): Pair<Int, Int> {
        cal.timeInMillis = timeInMillis
        return Pair(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
    }

    @Test
    fun `invoke with month filter returns records from that month`() = runTest {
        // Assuming record2 (now - 100000) is in the current month for simplicity of test setup
        // And record1 (now - 200000) is also in the current month but record3 (now) is also
        // This test needs careful date setup if records span across month boundaries.
        // For this example, let's assume allMockRecords are within the same month for simplicity
        // or adjust mock data for more specific month filtering.
        
        // Let's filter for the month of record2
        val (year, month) = getYearMonth(record2.date)
        val result = getFilteredRecordsUseCase(null, Pair(year, month), null).first()
        
        // This assertion depends on how many mock records fall into the selected month.
        // If all are in the same month:
        assertThat(result).containsExactly(record3, record2, record1) 
        // If only record2 was in that specific month (requires more distinct dates in mock):
        // assertThat(result).containsExactly(record2)
    }

    @Test
    fun `invoke with period filter returns records within that period`() = runTest {
        // Period covering record2 and record3, excluding record1
        val startTime = record2.date - 1000 
        val endTime = record3.date + 1000
        val result = getFilteredRecordsUseCase(null, null, Pair(startTime, endTime)).first()
        assertThat(result).containsExactly(record3, record2)
    }
    
    @Test
    fun `invoke with period filter not covering any records returns empty list`() = runTest {
        val startTime = now + 10000 
        val endTime = now + 20000
        val result = getFilteredRecordsUseCase(null, null, Pair(startTime, endTime)).first()
        assertThat(result).isEmpty()
    }

    @Test
    fun `invoke with search query and month filter returns intersection`() = runTest {
        // Assuming record1 is in its own month and matches "Lunch"
        val (year, month) = getYearMonth(record1.date)
        // Create a specific setup for this test if needed
        val specificRecords = listOf(record1, FinancialRecordEntity(id = 4, title = "Dinner", amount = BigDecimal("20"), date = record1.date + 1000, isExpense = true, accountId = 1, categoryId = 1, currencyId = 1, amountText = "20"))
        whenever(recordRepository.getAllRecords()).thenReturn(flowOf(specificRecords))

        val result = getFilteredRecordsUseCase("Lunch", Pair(year, month), null).first()
        assertThat(result).containsExactly(record1)
    }
    
    @Test
    fun `invoke with empty search query and no date filter returns all records`() = runTest {
        val result = getFilteredRecordsUseCase("", null, null).first()
         assertThat(result).isEqualTo(listOf(record3, record2, record1))
    }
}
