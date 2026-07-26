package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.OrderDao
import com.example.personalmidterm.data.local.dao.RewardDao
import com.example.personalmidterm.data.local.entity.OrderEntity
import com.example.personalmidterm.data.local.entity.OrderItemEntity
import com.example.personalmidterm.data.local.entity.RewardTransactionEntity
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class OrderRepository(
    private val orderDao: OrderDao,
    private val rewardDao: RewardDao,
    private val loyaltyPrefs: LoyaltyPrefs
) {
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders().map { entities ->
        entities.map { entity ->
            val items = orderDao.getItemsForOrder(entity.id).map { it.toDomainModel() }
            entity.toDomainModel(items)
        }
    }

    suspend fun placeOrder(cartItems: List<CartItem>, finalTotal: Long, address: String): Long {
        val orderEntity = OrderEntity(
            status = OrderStatus.ONGOING,
            resultStatus = OrderResultStatus.DELIVERED,
            totalPrice = finalTotal,
            timestamp = System.currentTimeMillis(),
            address = address
        )
        
        val orderItemEntities = cartItems.map { item ->
            OrderItemEntity(
                orderId = 0, // Will be set by DAO
                coffeeName = item.coffee.name,
                coffeeImageRes = item.coffee.imageRes,
                sweetness = item.customization.sweetness,
                temperature = item.customization.temperature,
                temperatureLevel = item.customization.temperatureLevel,
                shots = item.customization.shots,
                flavors = item.customization.flavors,
                quantity = item.quantity,
                unitPrice = item.unitPrice
            )
        }

        return orderDao.placeOrder(orderEntity, orderItemEntities)
    }

    suspend fun completeOrder(orderId: Long): OrderCompletionResult? {
        val order = orderDao.getOrderById(orderId) ?: return null
        if (order.status == OrderStatus.ONGOING) {
            orderDao.updateOrder(order.copy(status = OrderStatus.HISTORY))
            
            // Loyalty Logic
            val loyaltyState = loyaltyPrefs.loyaltyState.first()
            var stamps = loyaltyState.stamps + 1
            var rankIndex = loyaltyState.rankIndex
            var rankUpOccurred = false
            
            if (stamps == 8) {
                val newRankIndex = (rankIndex + 1).coerceAtMost(rankTiers.lastIndex)
                if (newRankIndex > rankIndex) {
                    rankUpOccurred = true
                }
                rankIndex = newRankIndex
                stamps = 0
            }
            
            // Points: 1 point per 1.000đ spent
            val pointsEarned = (order.totalPrice / 1000).toInt()
            val totalPoints = loyaltyState.totalPoints + pointsEarned
            
            loyaltyPrefs.updateLoyalty(stamps, totalPoints, rankIndex)
            
            // Reward Transaction
            rewardDao.insertTransaction(
                RewardTransactionEntity(
                    orderId = orderId,
                    description = "Order completion points",
                    points = pointsEarned,
                    timestamp = System.currentTimeMillis()
                )
            )

            return OrderCompletionResult(
                stamps = stamps,
                pointsEarned = pointsEarned,
                rankUpOccurred = rankUpOccurred,
                newRankName = rankTiers[rankIndex].rank
            )
        }
        return null
    }
}

data class OrderCompletionResult(
    val stamps: Int,
    val pointsEarned: Int,
    val rankUpOccurred: Boolean,
    val newRankName: String
)
