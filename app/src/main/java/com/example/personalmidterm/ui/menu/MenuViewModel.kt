package com.example.personalmidterm.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.repository.CartRepository
import com.example.personalmidterm.data.repository.CoffeeRepository
import com.example.personalmidterm.model.Category
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.model.Customization
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MenuViewModel(
    private val coffeeRepository: CoffeeRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    
    private val _selectedCategory = MutableStateFlow(Category.SPECIAL)
    val selectedCategory: StateFlow<Category> = _selectedCategory.asStateFlow()

    val filteredCoffees: StateFlow<List<Coffee>> = combine(
        coffeeRepository.allCoffees,
        _selectedCategory
    ) { coffees, category ->
        coffees.filter { it.category == category }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun quickAddToCart(coffee: Coffee) {
        viewModelScope.launch {
            cartRepository.addItem(
                coffee = coffee,
                customization = Customization(), // Default customization
                quantity = 1,
                unitPrice = coffee.basePrice
            )
        }
    }
}
