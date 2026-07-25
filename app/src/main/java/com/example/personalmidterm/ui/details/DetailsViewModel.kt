package com.example.personalmidterm.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.repository.CartRepository
import com.example.personalmidterm.data.repository.CoffeeRepository
import com.example.personalmidterm.data.repository.FavoriteRepository
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.model.Customization
import com.example.personalmidterm.model.Flavor
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

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    val totalPrice: StateFlow<Long> = combine(_coffee, _customization) { coffee, customization ->
        if (coffee == null) 0L
        else {
            val flavorSurcharge = customization.flavors.size * 5000L
            coffee.basePrice + flavorSurcharge
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun loadCoffee(id: Long) {
        viewModelScope.launch {
            val coffee = coffeeRepository.getCoffeeById(id)
            _coffee.value = coffee
            
            if (coffee != null) {
                favoriteRepository.allFavorites.collect { favorites ->
                    _isFavorite.value = favorites.any { it.coffee.id == id }
                }
            }
        }
    }

    fun updateCustomization(update: (Customization) -> Customization) {
        _customization.value = update(_customization.value)
    }

    fun toggleFavorite() {
        val coffee = _coffee.value ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoriteRepository.removeFavoriteByCoffeeId(coffee.id)
            } else {
                favoriteRepository.addFavorite(coffee, _customization.value)
            }
            _isFavorite.value = !_isFavorite.value
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
