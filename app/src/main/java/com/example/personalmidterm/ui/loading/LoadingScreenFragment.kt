package com.example.personalmidterm.ui.loading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.FragmentLoadingScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoadingScreenFragment : Fragment() {

    private var _binding: FragmentLoadingScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoadingScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Linger for 2 seconds before navigating to Home
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            if (isAdded) {
                findNavController().navigate(R.id.action_loadingScreenFragment_to_homeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
