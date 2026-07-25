package com.example.personalmidterm.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.repository.CoffeeRepository
import com.example.personalmidterm.data.repository.FavoriteRepository
import com.example.personalmidterm.model.Category
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.model.Favorite
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val coffeeRepository: CoffeeRepository,
    private val favoriteRepository: FavoriteRepository,
    private val loyaltyPrefs: LoyaltyPrefs
) : ViewModel() {

    private val _heroCoffee = MutableStateFlow<Coffee?>(null)
    val heroCoffee: StateFlow<Coffee?> = _heroCoffee.asStateFlow()

    val favorites: StateFlow<List<Favorite>> = favoriteRepository.allFavorites.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val loyaltyState = loyaltyPrefs.loyaltyState

    init {
        viewModelScope.launch {
            coffeeRepository.allCoffees.collect { coffees ->
                if (coffees.isNotEmpty() && _heroCoffee.value == null) {
                    val specials = coffees.filter { it.category == Category.SPECIAL }
                    _heroCoffee.value = if (specials.isNotEmpty()) specials.random() else coffees.random()
                }
            }
        }
    }
}
