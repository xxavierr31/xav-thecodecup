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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredCoffees: StateFlow<List<Coffee>> = combine(
        coffeeRepository.allCoffees,
        _selectedCategory,
        _searchQuery
    ) { coffees, category, query ->
        coffees.filter { 
            it.category == category && 
            (query.isBlank() || it.name.contains(query, ignoreCase = true))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setCategory(category: Category) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
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
