package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.CartDao
import com.example.personalmidterm.data.local.entity.CartItemEntity
import com.example.personalmidterm.model.CartItem
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.model.Customization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CartRepository(
    private val cartDao: CartDao,
    private val coffeeRepository: CoffeeRepository
) {
    val cartItems: Flow<List<CartItem>> = cartDao.getAllCartItems().map { entities ->
        entities.map { entity ->
            val coffee = coffeeRepository.getCoffeeById(entity.coffeeId)!!
            entity.toDomainModel(coffee)
        }
    }

    suspend fun addItem(coffee: Coffee, customization: Customization, quantity: Int, unitPrice: Long) {
        val existingItems = cartDao.getAllCartItems().first()
        val existingItem = existingItems.find {
            it.coffeeId == coffee.id &&
            it.sweetness == customization.sweetness &&
            it.temperature == customization.temperature &&
            it.intensity == customization.intensity &&
            it.shots == customization.shots &&
            it.flavors == customization.flavors
        }

        if (existingItem != null) {
            cartDao.updateItem(existingItem.copy(quantity = existingItem.quantity + quantity))
        } else {
            cartDao.insertItem(
                CartItemEntity(
                    coffeeId = coffee.id,
                    coffeeName = coffee.name,
                    coffeeImageRes = coffee.imageRes,
                    sweetness = customization.sweetness,
                    temperature = customization.temperature,
                    intensity = customization.intensity,
                    shots = customization.shots,
                    flavors = customization.flavors,
                    quantity = quantity,
                    unitPrice = unitPrice
                )
            )
        }
    }

    suspend fun updateQuantity(item: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteItem(CartItemEntity(
                id = item.id,
                coffeeId = item.coffee.id,
                coffeeName = item.coffee.name,
                coffeeImageRes = item.coffee.imageRes,
                sweetness = item.customization.sweetness,
                temperature = item.customization.temperature,
                intensity = item.customization.intensity,
                shots = item.customization.shots,
                flavors = item.customization.flavors,
                quantity = item.quantity,
                unitPrice = item.unitPrice
            ))
        } else {
            cartDao.updateItem(CartItemEntity(
                id = item.id,
                coffeeId = item.coffee.id,
                coffeeName = item.coffee.name,
                coffeeImageRes = item.coffee.imageRes,
                sweetness = item.customization.sweetness,
                temperature = item.customization.temperature,
                intensity = item.customization.intensity,
                shots = item.customization.shots,
                flavors = item.customization.flavors,
                quantity = newQuantity,
                unitPrice = item.unitPrice
            ))
        }
    }

    suspend fun removeItem(item: CartItem) {
        cartDao.deleteItem(CartItemEntity(
            id = item.id,
            coffeeId = item.coffee.id,
            coffeeName = item.coffee.name,
            coffeeImageRes = item.coffee.imageRes,
            sweetness = item.customization.sweetness,
            temperature = item.customization.temperature,
            intensity = item.customization.intensity,
            shots = item.customization.shots,
            flavors = item.customization.flavors,
            quantity = item.quantity,
            unitPrice = item.unitPrice
        ))
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
