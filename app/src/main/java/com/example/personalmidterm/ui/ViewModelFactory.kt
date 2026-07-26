package com.example.personalmidterm.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.personalmidterm.CodeCupApplication
import com.example.personalmidterm.data.repository.*
import com.example.personalmidterm.ui.cart.CartViewModel
import com.example.personalmidterm.ui.details.DetailsViewModel
import com.example.personalmidterm.ui.home.HomeViewModel
import com.example.personalmidterm.ui.menu.MenuViewModel
import com.example.personalmidterm.ui.myorders.MyOrdersViewModel
import com.example.personalmidterm.ui.profile.ProfileViewModel
import com.example.personalmidterm.ui.redeem.RedeemViewModel
import com.example.personalmidterm.ui.rewards.RewardsViewModel

object ViewModelFactory {
    val Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as CodeCupApplication
            val container = application.container

            return when {
                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel(
                        container.coffeeRepository,
                        container.favoriteRepository,
                        container.loyaltyPrefs,
                        container.profileRepository
                    ) as T
                }
                modelClass.isAssignableFrom(MenuViewModel::class.java) -> {
                    MenuViewModel(container.coffeeRepository, container.cartRepository) as T
                }
                modelClass.isAssignableFrom(DetailsViewModel::class.java) -> {
                    DetailsViewModel(container.coffeeRepository, container.cartRepository, container.favoriteRepository) as T
                }
                modelClass.isAssignableFrom(CartViewModel::class.java) -> {
                    CartViewModel(container.cartRepository, container.orderRepository, container.loyaltyPrefs) as T
                }
                modelClass.isAssignableFrom(RewardsViewModel::class.java) -> {
                    RewardsViewModel(container.rewardRepository, container.loyaltyPrefs) as T
                }
                modelClass.isAssignableFrom(RedeemViewModel::class.java) -> {
                    RedeemViewModel(container.redeemableRepository, container.loyaltyPrefs) as T
                }
                modelClass.isAssignableFrom(MyOrdersViewModel::class.java) -> {
                    MyOrdersViewModel(container.orderRepository) as T
                }
                modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                    ProfileViewModel(container.profileRepository, container.loyaltyPrefs) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}
