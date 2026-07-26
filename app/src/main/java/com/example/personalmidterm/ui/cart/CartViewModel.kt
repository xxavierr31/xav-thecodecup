package com.example.personalmidterm.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.repository.CartRepository
import com.example.personalmidterm.data.repository.OrderRepository
import com.example.personalmidterm.model.CartItem
import com.example.personalmidterm.model.rankTiers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val loyaltyPrefs: LoyaltyPrefs,
    private val profileRepository: com.example.personalmidterm.data.repository.ProfileRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val subtotal: StateFlow<Long> = cartItems.map { items ->
        items.sumOf { it.unitPrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val loyaltyState = loyaltyPrefs.loyaltyState

    val discount: StateFlow<Double> = loyaltyState.map { state ->
        rankTiers.getOrNull(state.rankIndex)?.discountPercent ?: 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val rankName: StateFlow<String> = loyaltyState.map { state ->
        rankTiers.getOrNull(state.rankIndex)?.rank ?: "Sprout"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Sprout")

    val discountAmount: StateFlow<Long> = combine(subtotal, discount) { subtotal, discount ->
        (subtotal * discount).toLong()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val total: StateFlow<Long> = combine(subtotal, discountAmount) { subtotal, amount ->
        subtotal - amount
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    data class DiscountUiState(
        val rankName: String = "Sprout",
        val discountPercent: Int = 0,
        val discountAmount: Long = 0L,
        val subtotal: Long = 0L,
        val isVisible: Boolean = false
    )

    val discountUiState: StateFlow<DiscountUiState> = combine(
        rankName,
        discount,
        discountAmount,
        subtotal
    ) { name, disc, amount, sub ->
        DiscountUiState(
            rankName = name,
            discountPercent = (disc * 100).toInt(),
            discountAmount = amount,
            subtotal = sub,
            isVisible = disc > 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DiscountUiState())

    fun updateQuantity(item: CartItem, delta: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item, item.quantity + delta)
        }
    }

    fun removeItem(item: CartItem) {
        viewModelScope.launch {
            cartRepository.removeItem(item)
        }
    }

    fun checkout(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isNotEmpty()) {
                val address = profileRepository.profile.value.address
                val orderId = orderRepository.placeOrder(items, total.value, address)
                cartRepository.clearCart()
                onSuccess(orderId)
            }
        }
    }
}
