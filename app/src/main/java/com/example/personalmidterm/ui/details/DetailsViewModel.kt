package com.example.personalmidterm.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.repository.CartRepository
import com.example.personalmidterm.data.repository.CoffeeRepository
import com.example.personalmidterm.data.repository.FavoriteRepository
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.model.Customization
import com.example.personalmidterm.model.Flavor
import com.example.personalmidterm.model.Temperature
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val coffeeRepository: CoffeeRepository,
    private val cartRepository: CartRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _coffee = MutableStateFlow<Coffee?>(null)
    val coffee: StateFlow<Coffee?> = _coffee.asStateFlow()

    private val _customization = MutableStateFlow(Customization())
    val customization: StateFlow<Customization> = _customization.asStateFlow()

    val isFavorite: StateFlow<Boolean> = combine(
        _coffee,
        _customization,
        favoriteRepository.allFavorites
    ) { coffee, customization, favorites ->
        if (coffee == null) false
        else favorites.any {
            it.coffee.id == coffee.id &&
            it.customization.sweetness == customization.sweetness &&
            it.customization.temperature == customization.temperature &&
            it.customization.temperatureLevel == customization.temperatureLevel &&
            it.customization.shots == customization.shots &&
            it.customization.flavors == customization.flavors
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val totalPrice: StateFlow<Long> = combine(_coffee, _customization) { coffee, customization ->
        if (coffee == null) 0L
        else {
            val flavorSurcharge = customization.flavors.size * 5000L
            val shotSurcharge = customization.shots * 7000L
            coffee.basePrice + flavorSurcharge + shotSurcharge
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun loadCoffee(id: Long, initialCustomization: Customization? = null) {
        if (_coffee.value != null) return // Already loaded, don't overwrite current state

        viewModelScope.launch {
            val coffee = coffeeRepository.getCoffeeById(id)
            _coffee.value = coffee
            
            if (initialCustomization != null) {
                _customization.value = initialCustomization
            } else if (coffee != null) {
                // Enforce valid default temperature
                if (!coffee.canBeIced && _customization.value.temperature == Temperature.ICED) {
                    _customization.value = _customization.value.copy(temperature = Temperature.HOT)
                } else if (!coffee.canBeHot && _customization.value.temperature == Temperature.HOT) {
                    _customization.value = _customization.value.copy(temperature = Temperature.ICED)
                }
            }
        }
    }

    fun updateCustomization(update: (Customization) -> Customization) {
        _customization.value = update(_customization.value)
    }

    fun toggleFavorite() {
        val coffee = _coffee.value ?: return
        val currentCust = _customization.value
        viewModelScope.launch {
            if (isFavorite.value) {
                favoriteRepository.removeFavoriteExact(coffee.id, currentCust)
            } else {
                favoriteRepository.addFavorite(coffee, currentCust)
            }
        }
    }

    fun addToCart(quantity: Int) {
        val coffee = _coffee.value ?: return
        viewModelScope.launch {
            cartRepository.addItem(
                coffee = coffee,
                customization = _customization.value,
                quantity = quantity,
                unitPrice = totalPrice.value
            )
        }
    }
}
