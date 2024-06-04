package com.theternal.home

import android.annotation.SuppressLint
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.SlideDistanceProvider
import com.theternal.common.extensions.format
import com.theternal.common.extensions.getDrawable
import com.theternal.common.extensions.showToast
import com.theternal.core.base.BaseStatefulFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.core.base.interfaces.ViewEvent
import com.theternal.uikit.adapters.RecordAdapter
import com.theternal.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import com.theternal.uikit.R.drawable as Drawables
import com.theternal.home.HomeContract.*

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
    private val recordAdapter = RecordAdapter()
    private var smile: Drawable? = null
    private var neutral: Drawable? = null
    private var frown: Drawable? = null

    //! UI Listeners and Initialization
    override val initViews: Initializer<FragmentHomeBinding> = {
        (emoji.drawable as AnimatedVectorDrawable).start()

        smile = getDrawable(Drawables.ic_emoji_smile)
        neutral = getDrawable(Drawables.ic_emoji_neutral)
        frown = getDrawable(Drawables.ic_emoji_frown)

        recordList.adapter = recordAdapter
    }

    //!  UI Updates
    @SuppressLint("SetTextI18n")
    override fun onStateUpdate(state: State) {
        recordAdapter.submitList(state.records)

        binding {
            total.text = state.balance.format() + " \$"

            emoji.setImageDrawable(
                when{
                    state.balance > BigDecimal.ZERO -> smile
                    state.balance == BigDecimal.ZERO -> neutral
                    else -> frown
                }
            )
            (emoji.drawable as Animatable).start()

            recordList.smoothScrollToPosition(0)
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