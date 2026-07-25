package com.example.personalmidterm.ui.myorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.repository.OrderRepository
import com.example.personalmidterm.model.Order
import com.example.personalmidterm.model.OrderStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MyOrdersViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _selectedStatus = MutableStateFlow(OrderStatus.ONGOING)
    val selectedStatus: StateFlow<OrderStatus> = _selectedStatus.asStateFlow()

    val filteredOrders: StateFlow<List<Order>> = combine(
        orderRepository.allOrders,
        _selectedStatus
    ) { orders, status ->
        orders.filter { it.status == status }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setStatus(status: OrderStatus) {
        _selectedStatus.value = status
    }

    fun completeOrder(orderId: Long) {
        viewModelScope.launch {
            orderRepository.completeOrder(orderId)
        }
    }
}
