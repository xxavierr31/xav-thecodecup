package com.example.personalmidterm.di

import android.content.Context
import com.example.personalmidterm.data.local.AppDatabase
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.prefs.ProfilePrefs
import com.example.personalmidterm.data.repository.*

interface AppContainer {
    val coffeeRepository: CoffeeRepository
    val cartRepository: CartRepository
    val orderRepository: OrderRepository
    val rewardRepository: RewardRepository
    val favoriteRepository: FavoriteRepository
    val redeemableRepository: RedeemableRepository
    val profileRepository: ProfileRepository
    val voucherRepository: VoucherRepository
    val loyaltyPrefs: LoyaltyPrefs
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    
    override val loyaltyPrefs: LoyaltyPrefs by lazy { LoyaltyPrefs(context) }
    private val profilePrefs: ProfilePrefs by lazy { ProfilePrefs(context) }

    override val coffeeRepository: CoffeeRepository by lazy {
        CoffeeRepository(database.coffeeDao())
    }

    override val cartRepository: CartRepository by lazy {
        CartRepository(database.cartDao(), coffeeRepository)
    }

    override val orderRepository: OrderRepository by lazy {
        OrderRepository(database.orderDao(), database.rewardDao(), loyaltyPrefs)
    }

    override val rewardRepository: RewardRepository by lazy {
        RewardRepository(database.rewardDao(), loyaltyPrefs)
    }

    override val favoriteRepository: FavoriteRepository by lazy {
        FavoriteRepository(database.favoriteDao(), coffeeRepository)
    }

    override val redeemableRepository: RedeemableRepository by lazy {
        RedeemableRepository(database.redeemableDao())
    }

    override val profileRepository: ProfileRepository by lazy {
        ProfileRepository(profilePrefs)
    }

    override val voucherRepository: VoucherRepository by lazy {
        VoucherRepository(database.voucherDao())
    }
}
