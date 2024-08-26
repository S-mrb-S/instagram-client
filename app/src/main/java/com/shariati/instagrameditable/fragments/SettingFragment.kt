package com.shariati.instagrameditable.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shariati.instagrameditable.R
import com.shariati.instagrameditable.databinding.FragmentSettingBinding
import com.shariati.instagrameditable.utils.getEndReach
import com.shariati.instagrameditable.utils.getStartReach
import com.shariati.instagrameditable.utils.saveReachRange

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater)


        binding.firstNum.setText(getStartReach(requireContext()).toString())

        binding.secNum.setText(getEndReach(requireContext()).toString())


        binding.btnOk.setOnClickListener {
            saveReachRange(
                requireContext(),
                binding.firstNum.text.toString().toInt(),
                binding.secNum.text.toString().toInt()
            )

            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}