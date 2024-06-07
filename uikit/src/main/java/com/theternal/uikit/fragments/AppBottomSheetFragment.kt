package com.theternal.uikit.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theternal.uikit.databinding.FragmentAppBottomSheetBinding

class AppBottomSheetFragment(
    private val fragmentFactory: () -> Fragment
) : BottomSheetDialogFragment() {

    private var binding: FragmentAppBottomSheetBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {

            childFragmentManager.beginTransaction().apply {
                add(fragmentContainerView.id, fragmentFactory())
            }.commit()

            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    fun setTitle(title: String) {
        binding?.title?.text = title
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}