package com.example.personalmidterm.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.FragmentProfileBinding
import com.example.personalmidterm.ui.ViewModelFactory
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels { ViewModelFactory.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader()
        setupListeners()
        observeViewModel()
    }

    private fun setupHeader() {
        binding.header.screenTitle.text = "Profile"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.backButton.visibility = View.GONE
        binding.header.favoriteIcon.visibility = View.GONE
        binding.header.cartIcon.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.btnEditDetails.setOnClickListener {
            toggleEditMode(true)
        }
        binding.btnSaveDetails.setOnClickListener {
            val name = binding.tvProfileName.text.toString() // Wait, name is a TextView. 
            // In fragment_profile.xml, only email, phone, address have EditTexts.
            val email = binding.etEmailValue.text.toString()
            val phone = binding.etPhoneValue.text.toString()
            val address = binding.etAddressValue.text.toString()
            
            viewModel.updateProfile(name, email, phone, address)
            toggleEditMode(false)
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        binding.btnEditDetails.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.btnSaveDetails.visibility = if (isEditing) View.VISIBLE else View.GONE
        
        binding.tvEmailValue.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.etEmailValue.visibility = if (isEditing) View.VISIBLE else View.GONE
        
        binding.tvPhoneValue.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.etPhoneValue.visibility = if (isEditing) View.VISIBLE else View.GONE
        
        binding.tvAddressValue.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.etAddressValue.visibility = if (isEditing) View.VISIBLE else View.GONE
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.profile.collect { profile ->
                        binding.tvProfileName.text = profile.name
                        binding.tvEmailValue.text = profile.email
                        binding.etEmailValue.setText(profile.email)
                        binding.tvPhoneValue.text = profile.phone
                        binding.etPhoneValue.setText(profile.phone)
                        binding.tvAddressValue.text = profile.address
                        binding.etAddressValue.setText(profile.address)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
