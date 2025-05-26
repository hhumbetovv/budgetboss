package com.theternal.home

import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getDrawable
import com.theternal.common.extensions.gone
import com.theternal.common.extensions.show
import com.theternal.common.extensions.showToast
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.core.managers.LayoutManager
import com.theternal.domain.entities.local.TransferEntity
import com.theternal.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import com.theternal.uikit.R.drawable as Drawables
import com.theternal.home.HomeContract.*
import com.theternal.record_details.RecordDetailsFragment
import com.theternal.uikit.adapters.RecordAdapter
import com.theternal.uikit.fragments.AppBottomSheetFragment

// MPAndroidChart imports
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.XAxis
import android.graphics.Color
// com.theternal.uikit.R will be used directly via requireContext().getColor(com.theternal.uikit.R.color.some_color)

@AndroidEntryPoint
class HomeFragment : BaseStatefulFragment<FragmentHomeBinding, HomeViewModel,
        ViewEvent.Empty, State, Effect>() {

    //! Initialize Binding and State
    override val inflateBinding: Inflater<FragmentHomeBinding>
        get() = FragmentHomeBinding::inflate

    override val getViewModelClass: () -> Class<HomeViewModel> = {
        HomeViewModel::class.java
    }

    //! Screen Transitions
    override val transitionDuration: Long = 300
    override val viewEntering: Transition = MaterialFadeThrough().apply {
        secondaryAnimatorProvider = SlideDistanceProvider(Gravity.START)
    }
    override val viewExiting: Transition = MaterialFadeThrough().apply {
        secondaryAnimatorProvider = SlideDistanceProvider(Gravity.START)
    }

    //! UI Properties
    private val recordDetailsFragment = RecordDetailsFragment()
    private val recordDetailsSheet = AppBottomSheetFragment { recordDetailsFragment }

    private var recordAdapter = RecordAdapter { record ->
        recordDetailsFragment.setProperties(record.id, record is TransferEntity)

        if(!recordDetailsSheet.isAdded) {
            recordDetailsSheet.show(childFragmentManager, "Record Details")
        }
    }
    private var smile: Drawable? = null
    private var neutral: Drawable? = null
    private var frown: Drawable? = null

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentHomeBinding> = {
        (requireActivity() as LayoutManager).apply {
            showSettingsBtn()
            hideBackBtn()
            showNavBar()
            setTitle(getString(Strings.home))
        }

        (emoji.drawable as AnimatedVectorDrawable).start()

        smile = getDrawable(Drawables.ic_emoji_smile)
        neutral = getDrawable(Drawables.ic_emoji_neutral)
        frown = getDrawable(Drawables.ic_emoji_frown)

        recordList.adapter = recordAdapter
    }

    override fun onDestroyView() {
        smile = null
        neutral = null
        frown = null
        super.onDestroyView()
    }

    //!  UI Updates
    override fun onStateUpdate(state: State) {

        binding {
            recordAdapter.apply {
                // currentList.size < state.records.size // Removed problematic line
                submitList(state.records)
                if (state.records.isNotEmpty()) { // Scroll only if list is not empty
                    recordList.scrollToPosition(0)
                }
            }

            total.text = state.balance.format(true)
            updateEmoji(state.balance)

            if(state.records.isEmpty()) {
                emptyListTitle.show()
            } else {
                emptyListTitle.gone()
            }

            // New: Update Monthly Chart
            updateMonthlyChart(state.monthlySummary)
        }
    }

    private fun updateMonthlyChart(summary: com.theternal.domain.entities.aggregations.MonthlySummary) {
        val chart = binding.monthlyChartView ?: return // Or handle if null differently

        // 1. Create BarEntries
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, summary.totalIncome.toFloat(), "Income"))
        entries.add(BarEntry(1f, summary.totalExpense.toFloat(), "Expense"))

        // 2. Create BarDataSet
        val dataSet = BarDataSet(entries, "Monthly Overview")
        
        // 3. Configure colors for Income and Expense bars
        dataSet.colors = listOf(
            requireContext().getColor(com.theternal.uikit.R.color.primary), // Income color
            requireContext().getColor(com.theternal.uikit.R.color.danger)    // Expense color
        )
        dataSet.valueTextColor = Color.BLACK // Or use a color from resources
        dataSet.valueTextSize = 12f

        // 4. Create BarData object
        val barData = BarData(dataSet)
        barData.barWidth = 0.4f // Adjust bar width

        // 5. Configure Chart appearance
        chart.data = barData
        chart.description.isEnabled = false // No description text
        chart.setFitBars(true) // make the x-axis fit exactly all bars

        // X-Axis configuration
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false) // No grid lines
        xAxis.granularity = 1f // only intervals of 1 unit
        xAxis.valueFormatter = object : ValueFormatter() {
            private val labels = arrayOf("Income", "Expense")
            override fun getFormattedValue(value: Float): String {
                return if (value >= 0 && value < labels.size) labels[value.toInt()] else ""
            }
        }

        // Y-Axis configuration (Left)
        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(true) // Horizontal grid lines
        leftAxis.axisMinimum = 0f // start at zero

        // Y-Axis configuration (Right) - Disable
        chart.axisRight.isEnabled = false

        // Legend configuration
        chart.legend.isEnabled = false // Hide legend as colors and X-axis labels are descriptive enough

        // Animation (optional)
        chart.animateY(1000)

        // Refresh the chart
        chart.invalidate()
    }

    private fun updateEmoji(balance: BigDecimal) {
        val drawable = when {
            balance > BigDecimal.ZERO -> smile
            balance < BigDecimal.ZERO -> frown
            else -> neutral
        }
        if(drawable != binding.emoji.drawable) {
            binding.emoji.setImageDrawable(drawable)
            (binding.emoji.drawable as Animatable).start()
        }
    }

    override fun onEffectUpdate(effect: Effect) {
        when(effect) {
            Effect.FetchFailedNotify -> {
                showToast(getString(Strings.currency_fetch_failed))
            }
        }
    }
}