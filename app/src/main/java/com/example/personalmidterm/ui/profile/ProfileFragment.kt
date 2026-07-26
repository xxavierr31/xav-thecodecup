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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels { ViewModelFactory.Factory }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            saveImageToInternalStorage(uri)
        }
    }

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
        binding.btnChangePhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.profilePhotoBg.setOnClickListener {
            if (binding.btnChangePhoto.visibility == View.VISIBLE) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
        binding.btnSaveDetails.setOnClickListener {
            val name = binding.etProfileName.text.toString().trim()
            val email = binding.etEmailValue.text.toString().trim()
            val phone = binding.etPhoneValue.text.toString().trim()
            val address = binding.etAddressValue.text.toString().trim()
            
            if (name.isEmpty() || address.isEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Name and Address cannot be empty", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                android.widget.Toast.makeText(requireContext(), "Please enter a valid email address", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.PHONE.matcher(phone).matches() || phone.length < 10 || phone.length > 11) {
                android.widget.Toast.makeText(requireContext(), "Please enter a valid phone number", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateProfile(name, email, phone, address, viewModel.profile.value.imagePath)
            toggleEditMode(false)
        }
    }

    private fun saveImageToInternalStorage(uri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return
            val file = File(requireContext().filesDir, "profile_picture.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            val profile = viewModel.profile.value
            viewModel.updateProfile(profile.name, profile.email, profile.phone, profile.address, file.absolutePath)
        } catch (e: Exception) {
            android.util.Log.e("ProfileFragment", "Error saving image", e)
        }
    }

    private fun toggleEditMode(isEditing: Boolean) {
        binding.btnEditDetails.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.btnSaveDetails.visibility = if (isEditing) View.VISIBLE else View.GONE
        binding.btnChangePhoto.visibility = if (isEditing) View.VISIBLE else View.GONE
        
        binding.tvProfileName.visibility = if (isEditing) View.GONE else View.VISIBLE
        binding.etProfileName.visibility = if (isEditing) View.VISIBLE else View.GONE
        
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
                        binding.etProfileName.setText(profile.name)
                        binding.tvEmailValue.text = profile.email
                        binding.etEmailValue.setText(profile.email)
                        binding.tvPhoneValue.text = profile.phone
                        binding.etPhoneValue.setText(profile.phone)
                        binding.tvAddressValue.text = profile.address
                        binding.etAddressValue.setText(profile.address)

                        if (profile.imagePath != null) {
                            val file = File(profile.imagePath)
                            if (file.exists()) {
                                binding.ivProfilePhoto.setPadding(0, 0, 0, 0)
                                binding.ivProfilePhoto.setImageURI(Uri.fromFile(file))
                            } else {
                                // Fallback if file was deleted
                                binding.ivProfilePhoto.setPadding(28, 28, 28, 28)
                                binding.ivProfilePhoto.setImageResource(R.drawable.profile)
                            }
                        } else {
                            binding.ivProfilePhoto.setPadding(28, 28, 28, 28)
                            binding.ivProfilePhoto.setImageResource(R.drawable.profile)
                        }
                    }
                }
                launch {
                    viewModel.loyaltyState.collect { state ->
                        val rank = com.example.personalmidterm.model.rankTiers.getOrNull(state.rankIndex)?.rank ?: "Sprout"
                        binding.tvTagLabel.text = rank
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
