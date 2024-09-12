package com.theternal.settings

import com.theternal.common.constants.PrefKeys
import com.theternal.common.extensions.Strings
import com.theternal.common.extensions.getSavedBoolean
import com.theternal.common.extensions.getSavedString
import com.theternal.common.extensions.saveBoolean
import com.theternal.common.extensions.saveString
import com.theternal.core.base.BaseStatelessFragment
import com.theternal.core.base.Inflater
import com.theternal.core.base.Initializer
import com.theternal.core.managers.LayoutManager
import com.theternal.settings.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : BaseStatelessFragment<FragmentSettingsBinding>() {

    override val inflateBinding: Inflater<FragmentSettingsBinding>
        get() = FragmentSettingsBinding::inflate

    private var selectedLocaleCode: String? = null

    private val localeAdapter = LocaleAdapter {code ->
        if(selectedLocaleCode != code) {
            requireContext().saveString(PrefKeys.LOCALE, code) {
                requireActivity().supportFragmentManager.executePendingTransactions()
                requireActivity().recreate()
            }
        }
    }

    override val initViews: Initializer<FragmentSettingsBinding> = {
        (requireActivity() as LayoutManager).apply {
            hideSettingsBtn()
            showBackBtn()
            hideNavBar()
            setTitle(getString(Strings.settings))
        }

        initThemeButton()

        initLocaleList()
    }

    private fun initThemeButton() {
        val isDarkMode = requireContext().getSavedBoolean(PrefKeys.IS_DARK_MODE, true)

        binding.apply {
            themeSwitch.isChecked = isDarkMode
            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked != isDarkMode) {
                    requireContext().saveBoolean(PrefKeys.IS_THEME_SAVED, true) {
                        requireContext().saveBoolean(PrefKeys.IS_DARK_MODE, isChecked) {
                            requireActivity().supportFragmentManager.executePendingTransactions()
                            requireActivity().recreate()
                        }
                    }
                }
            }

            themeButton.setOnClickListener {
                themeSwitch.isChecked = !themeSwitch.isChecked
            }
        }
    }

    private fun initLocaleList() {
        binding.localeList.adapter = localeAdapter

        selectedLocaleCode = requireContext().getSavedString(
            PrefKeys.LOCALE, Locale.getDefault().language
        )

        localeAdapter.submitList(listOf(
            Pair("en", getString(Strings.en)),
            Pair("az", getString(Strings.az)),
            Pair("tr", getString(Strings.tr)),
            Pair("ru", getString(Strings.ru)),
        ).map { pair ->
            Triple(
                pair.first,
                pair.second,
                selectedLocaleCode == pair.first
            )
        })
    }

}