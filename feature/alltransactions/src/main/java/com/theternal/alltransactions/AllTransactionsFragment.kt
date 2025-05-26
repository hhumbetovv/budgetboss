package com.theternal.alltransactions

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.alltransactions.databinding.FragmentAllTransactionsBinding
import com.theternal.uikit.adapters.RecordAdapter
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.domain.interfaces.RecordEntity
import com.theternal.record_details.RecordDetailsFragment
import com.theternal.uikit.fragments.AppBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class AllTransactionsFragment : BaseStatefulFragment<
        FragmentAllTransactionsBinding,
        AllTransactionsViewModel,
        AllTransactionsContract.Event,
        AllTransactionsContract.State,
        AllTransactionsContract.Effect>() {

    override val inflateBinding: Inflater<FragmentAllTransactionsBinding> =
        FragmentAllTransactionsBinding::inflate

    override val getViewModelClass: () -> Class<AllTransactionsViewModel> = {
        AllTransactionsViewModel::class.java
    }

    private val recordAdapter = RecordAdapter { record ->
        showRecordDetails(record)
    }

    override val initViews: Initializer<FragmentAllTransactionsBinding> = {
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recordAdapter
        }

        // Search Input Listener
        binding.searchInputEditText.doAfterTextChanged { text ->
            postEvent(AllTransactionsContract.Event.SearchQueryChanged(text.toString()))
        }

        // Filter Button Listeners
        binding.filterByMonthButton.setOnClickListener {
            postEvent(AllTransactionsContract.Event.FilterByMonthClicked)
        }
        binding.filterByPeriodButton.setOnClickListener {
            postEvent(AllTransactionsContract.Event.FilterByPeriodClicked)
        }
        binding.clearFiltersButton.setOnClickListener {
            binding.searchInputEditText.setText("") // Clear search text
            postEvent(AllTransactionsContract.Event.ClearSearchAndFilters)
        }

        // Initial load is handled by ViewModel's init block
    }

    override fun onStateUpdate(state: AllTransactionsContract.State) {
        binding.loadingIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        val noFiltersApplied = state.searchQuery.isNullOrBlank() &&
                               state.filterMonthYear == null &&
                               state.filterPeriod == null

        if (!state.isLoading && state.transactions.isEmpty()) {
            binding.emptyStateTextView.text = if (state.error != null) {
                state.error
            } else if (!noFiltersApplied) {
                "No transactions found matching your filters." // TODO: Use string resource
            } else {
                getString(R.string.no_transactions_found) // Use existing string
            }
            binding.emptyStateTextView.visibility = View.VISIBLE
            binding.transactionsRecyclerView.visibility = View.GONE
        } else {
            binding.emptyStateTextView.visibility = View.GONE
            binding.transactionsRecyclerView.visibility = View.VISIBLE
            recordAdapter.submitList(state.transactions)
        }
        
        // Update active filter description text
        if (state.activeFilterDescription != null) {
            binding.activeFiltersTextView.text = state.activeFilterDescription
            binding.activeFiltersTextView.visibility = View.VISIBLE
        } else {
            binding.activeFiltersTextView.visibility = View.GONE
        }

        // Show/Hide Clear button
        binding.clearFiltersButton.visibility = if (!noFiltersApplied) View.VISIBLE else View.GONE

        // Update search input text if it was cleared by ClearSearchAndFilters from ViewModel state
        if (state.searchQuery == null && binding.searchInputEditText.text?.isNotEmpty() == true && !state.isLoading) {
             if(noFiltersApplied) { // Only clear if truly no filters are active from state
                binding.searchInputEditText.setText("")
             }
        }
    }

    override fun onEffectUpdate(effect: AllTransactionsContract.Effect) {
        when (effect) {
            is AllTransactionsContract.Effect.ShowMonthPicker -> showMonthPicker()
            is AllTransactionsContract.Effect.ShowDateRangePicker -> showDateRangePicker()
        }
    }

    private fun showMonthPicker() {
        val calendar = Calendar.getInstance()
        // Set initial selection to current month or last selected month if available
        val currentSelection = currentState.filterMonthYear?.let { (year, month) ->
            Calendar.getInstance().apply { set(year, month, 1) }.timeInMillis
        } ?: calendar.timeInMillis

        val monthPicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select month") // TODO: Use string resource
            .setSelection(currentSelection)
            .build()

        monthPicker.addOnPositiveButtonClickListener { selection ->
            val selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            selectedCalendar.timeInMillis = selection
            val year = selectedCalendar.get(Calendar.YEAR)
            val month = selectedCalendar.get(Calendar.MONTH) // 0-indexed
            postEvent(AllTransactionsContract.Event.MonthSelected(year, month))
        }
        monthPicker.show(childFragmentManager, "MONTH_PICKER_TAG")
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val currentStart = currentState.filterPeriod?.first ?: calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        val currentEnd = currentState.filterPeriod?.second ?: calendar.timeInMillis


        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select period") // TODO: Use string resource
            .setSelection(
                androidx.core.util.Pair(currentStart, currentEnd)
            )
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate = selection.first
            val endDate = selection.second
            // Ensure endDate covers the whole day selected
            val calEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calEnd.timeInMillis = endDate
            calEnd.set(Calendar.HOUR_OF_DAY, 23)
            calEnd.set(Calendar.MINUTE, 59)
            calEnd.set(Calendar.SECOND, 59)
            calEnd.set(Calendar.MILLISECOND, 999)
            
            postEvent(AllTransactionsContract.Event.PeriodSelected(startDate, calEnd.timeInMillis))
        }
        dateRangePicker.show(childFragmentManager, "DATE_RANGE_PICKER_TAG")
    }

    private fun showRecordDetails(record: RecordEntity) {
        val recordDetailsFragment = RecordDetailsFragment().apply {
            setProperties(record.id, record is TransferEntity)
        }
        AppBottomSheetFragment { recordDetailsFragment }.show(childFragmentManager, "RecordDetails")
    }
}
