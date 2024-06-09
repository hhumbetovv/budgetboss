package com.theternal.home

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.Gravity
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getDrawable
import com.theternal.common.extensions.hide
import com.theternal.common.extensions.show
import com.theternal.common.extensions.showToast
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import com.theternal.uikit.R.drawable as Drawables
import com.theternal.home.HomeContract.*
import com.theternal.record_details.RecordAdapter

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
    private var recordAdapter: RecordAdapter? = null
    private var smile: Drawable? = null
    private var neutral: Drawable? = null
    private var frown: Drawable? = null

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentHomeBinding> = {
        (emoji.drawable as AnimatedVectorDrawable).start()

        smile = getDrawable(Drawables.ic_emoji_smile)
        neutral = getDrawable(Drawables.ic_emoji_neutral)
        frown = getDrawable(Drawables.ic_emoji_frown)

        recordAdapter = RecordAdapter(childFragmentManager)
        recordList.adapter = recordAdapter
    }

    override fun onDestroyView() {
        recordAdapter = null
        smile = null
        neutral = null
        frown = null
        super.onDestroyView()
    }

    //!  UI Updates
    @SuppressLint("SetTextI18n")
    override fun onStateUpdate(state: State) {
        recordAdapter?.submitList(state.records)

        binding {
            total.text = "${state.balance.format()} $"

            updateEmoji(state.balance)

            if(state.records.isEmpty()) {
                emptyListTitle.show()
            } else {
                emptyListTitle.hide()
            }

            recordList.smoothScrollToPosition(0)
        }
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
                showToast("Currency fetch failed")
            }
        }
    }
}