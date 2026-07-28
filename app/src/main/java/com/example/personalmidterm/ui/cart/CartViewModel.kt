package com.example.personalmidterm.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.repository.CartRepository
import com.example.personalmidterm.data.repository.OrderRepository
import com.example.personalmidterm.model.CartItem
import com.example.personalmidterm.model.VoucherType
import com.example.personalmidterm.model.rankTiers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val loyaltyPrefs: LoyaltyPrefs,
    private val profileRepository: com.example.personalmidterm.data.repository.ProfileRepository,
    private val voucherRepository: com.example.personalmidterm.data.repository.VoucherRepository
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

    val selectedVoucher = voucherRepository.selectedVoucher

    val voucherDiscountAmount: StateFlow<Long> = combine(subtotal, selectedVoucher) { sub, voucher ->
        if (voucher == null || sub == 0L || sub < voucher.minSpend) 0L
        else {
            val amount = when (voucher.type) {
                VoucherType.FLAT -> voucher.value
                VoucherType.PERCENTAGE -> (sub * (voucher.value / 100.0)).toLong()
            }
            if (voucher.maxDiscount != null) amount.coerceAtMost(voucher.maxDiscount) else amount
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val total: StateFlow<Long> = combine(subtotal, discountAmount, voucherDiscountAmount) { sub, rankDisc, voucherDisc ->
        (sub - rankDisc - voucherDisc).coerceAtLeast(0L)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    data class DiscountUiState(
        val rankName: String = "Sprout",
        val discountPercent: Int = 0,
        val rankDiscountAmount: Long = 0L,
        val voucherCode: String? = null,
        val voucherDiscountAmount: Long = 0L,
        val subtotal: Long = 0L,
        val isRankDiscountVisible: Boolean = false,
        val isVoucherDiscountVisible: Boolean = false
    )

    val discountUiState: StateFlow<DiscountUiState> = combine(
        rankName,
        discount,
        discountAmount,
        selectedVoucher,
        voucherDiscountAmount,
        subtotal
    ) { args ->
        val name = args[0] as String
        val disc = args[1] as Double
        val rankAmt = args[2] as Long
        val voucher = args[3] as com.example.personalmidterm.model.Voucher?
        val vAmt = args[4] as Long
        val sub = args[5] as Long
        
        DiscountUiState(
            rankName = name,
            discountPercent = (disc * 100).toInt(),
            rankDiscountAmount = rankAmt,
            voucherCode = voucher?.code,
            voucherDiscountAmount = vAmt,
            subtotal = sub,
            isRankDiscountVisible = disc > 0,
            isVoucherDiscountVisible = vAmt > 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DiscountUiState())

    private var lastDeletedItem: CartItem? = null

    fun updateQuantity(item: CartItem, delta: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(item, item.quantity + delta)
        }
    }

    fun removeItem(item: CartItem) {
        lastDeletedItem = item
        viewModelScope.launch {
            cartRepository.removeItem(item)
        }
    }

    fun undoDelete() {
        val item = lastDeletedItem ?: return
        viewModelScope.launch {
            cartRepository.addItem(
                coffee = item.coffee,
                customization = item.customization,
                quantity = item.quantity,
                unitPrice = item.unitPrice
            )
            lastDeletedItem = null
        }
    }

    fun checkout(onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isNotEmpty()) {
                val address = profileRepository.profile.value.address
                val orderId = orderRepository.placeOrder(items, total.value, address)
                
                // If a voucher was used, remove it from the user's list
                selectedVoucher.value?.let { voucher ->
                    voucherRepository.useVoucher(voucher.id)
                }
                
                cartRepository.clearCart()
                voucherRepository.clearSelectedVoucher()
                onSuccess(orderId)
            }
        }
    }
}
