package com.example.personalmidterm.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.FragmentCustomizeBinding
import com.example.personalmidterm.model.*
import com.example.personalmidterm.ui.ViewModelFactory
import com.example.personalmidterm.util.CurrencyFormatter
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private var _binding: FragmentCustomizeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailsViewModel by viewModels { ViewModelFactory.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCustomizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coffeeId = arguments?.getLong("coffeeId") ?: return
        viewModel.loadCoffee(coffeeId)

        setupHeader()
        setupFlavorLabels()
        setupListeners()
        observeViewModel()
    }

    private fun setupHeader() {
        binding.header.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.header.backButton.visibility = View.VISIBLE
        binding.header.screenTitle.text = "Customize"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.favoriteIcon.visibility = View.VISIBLE
        binding.header.cartIcon.visibility = View.VISIBLE
        
        binding.header.favoriteIcon.setOnClickListener {
            viewModel.toggleFavorite()
        }
        binding.header.cartIcon.setOnClickListener {
            CartPreviewBottomSheet().show(childFragmentManager, "CartPreview")
        }
    }

    private fun setupFlavorLabels() {
        binding.cbVanilla.text = "${Flavor.VANILLA.label} (+ ${CurrencyFormatter.format(Flavor.VANILLA.price)})"
        binding.cbHazelnut.text = "${Flavor.HAZELNUT.label} (+ ${CurrencyFormatter.format(Flavor.HAZELNUT.price)})"
        binding.cbCaramel.text = "${Flavor.CARAMEL.label} (+ ${CurrencyFormatter.format(Flavor.CARAMEL.price)})"
    }

    private fun setupListeners() {
        binding.sliderSweetness.addOnChangeListener { _, value, _ ->
            val sweetness = when (value.toInt()) {
                0 -> Sweetness.ZERO
                25 -> Sweetness.TWENTY_FIVE
                50 -> Sweetness.FIFTY
                75 -> Sweetness.SEVENTY_FIVE
                else -> Sweetness.HUNDRED
            }
            viewModel.updateCustomization { it.copy(sweetness = sweetness) }
        }

        binding.sliderTempLevel.addOnChangeListener { _, value, _ ->
            val level = when (value.toInt()) {
                0 -> TemperatureLevel.ZERO
                25 -> TemperatureLevel.TWENTY_FIVE
                50 -> TemperatureLevel.FIFTY
                75 -> TemperatureLevel.SEVENTY_FIVE
                else -> TemperatureLevel.HUNDRED
            }
            viewModel.updateCustomization { it.copy(temperatureLevel = level) }
        }

        binding.btnTempIced.setOnClickListener {
            viewModel.updateCustomization { it.copy(temperature = Temperature.ICED) }
        }
        binding.btnTempHot.setOnClickListener {
            viewModel.updateCustomization { it.copy(temperature = Temperature.HOT) }
        }

        binding.btnShotsMinus.setOnClickListener {
            viewModel.updateCustomization { it.copy(shots = (it.shots - 1).coerceAtLeast(1)) }
        }
        binding.btnShotsPlus.setOnClickListener {
            viewModel.updateCustomization { it.copy(shots = (it.shots + 1).coerceAtMost(4)) }
        }

        binding.cbVanilla.setOnCheckedChangeListener { _, isChecked ->
            updateFlavor(Flavor.VANILLA, isChecked)
        }
        binding.cbHazelnut.setOnCheckedChangeListener { _, isChecked ->
            updateFlavor(Flavor.HAZELNUT, isChecked)
        }
        binding.cbCaramel.setOnCheckedChangeListener { _, isChecked ->
            updateFlavor(Flavor.CARAMEL, isChecked)
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart(1)
            findNavController().navigate(R.id.action_detailsFragment_to_cartFragment)
        }
    }

    private fun updateFlavor(flavor: Flavor, add: Boolean) {
        viewModel.updateCustomization { current ->
            val newFlavors = current.flavors.toMutableList()
            if (add) {
                if (!newFlavors.contains(flavor)) newFlavors.add(flavor)
            } else {
                newFlavors.remove(flavor)
            }
            current.copy(flavors = newFlavors)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.coffee.collect { coffee ->
                        coffee?.let { bindCoffee(it) }
                    }
                }
                launch {
                    viewModel.customization.collect { customization ->
                        bindCustomization(customization)
                    }
                }
                launch {
                    viewModel.totalPrice.collect { price ->
                        binding.tvTotalPriceVal.text = CurrencyFormatter.format(price)
                    }
                }
                launch {
                    viewModel.isFavorite.collect { isFavorite ->
                        binding.header.favoriteIcon.setImageResource(
                            if (isFavorite) R.drawable.ic_full_heart 
                            else R.drawable.ic_empty_heart
                        )
                    }
                }
            }
        }
    }

    private fun bindCoffee(coffee: Coffee) {
        binding.tvProductTitle.text = coffee.name
        binding.tvProductSubtitle.text = coffee.description
        binding.ivProductImage.setImageResource(coffee.imageRes)
    }

    private fun bindCustomization(c: Customization) {
        binding.tvSweetnessValue.text = c.sweetness.label
        binding.sliderSweetness.value = when (c.sweetness) {
            Sweetness.ZERO -> 0f
            Sweetness.TWENTY_FIVE -> 25f
            Sweetness.FIFTY -> 50f
            Sweetness.SEVENTY_FIVE -> 75f
            Sweetness.HUNDRED -> 100f
        }

        binding.tvTempLevelLabel.text = if (c.temperature == Temperature.ICED) "Ice Level" else "Heat Level"
        binding.tvTempLevelValue.text = c.temperatureLevel.label
        binding.sliderTempLevel.value = when (c.temperatureLevel) {
            TemperatureLevel.ZERO -> 0f
            TemperatureLevel.TWENTY_FIVE -> 25f
            TemperatureLevel.FIFTY -> 50f
            TemperatureLevel.SEVENTY_FIVE -> 75f
            TemperatureLevel.HUNDRED -> 100f
        }

        val cream = android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.card_cream))
        val grey = android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.bg_grey))

        if (c.temperature == Temperature.ICED) {
            binding.btnTempIced.backgroundTintList = cream
            binding.btnTempIced.setTextColor(requireContext().getColor(R.color.theme_brown))
            binding.btnTempHot.backgroundTintList = grey
            binding.btnTempHot.setTextColor(requireContext().getColor(R.color.text_secondary))
        } else {
            binding.btnTempHot.backgroundTintList = cream
            binding.btnTempHot.setTextColor(requireContext().getColor(R.color.theme_brown))
            binding.btnTempIced.backgroundTintList = grey
            binding.btnTempIced.setTextColor(requireContext().getColor(R.color.text_secondary))
        }

        binding.tvShotsCount.text = c.shots.toString()
        
        binding.cbVanilla.isChecked = c.flavors.contains(Flavor.VANILLA)
        binding.cbHazelnut.isChecked = c.flavors.contains(Flavor.HAZELNUT)
        binding.cbCaramel.isChecked = c.flavors.contains(Flavor.CARAMEL)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
