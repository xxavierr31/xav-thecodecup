package com.example.personalmidterm.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.personalmidterm.R
import com.example.personalmidterm.data.local.dao.*
import com.example.personalmidterm.data.local.entity.*
import com.example.personalmidterm.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CoffeeEntity::class,
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        RewardTransactionEntity::class,
        FavoriteEntity::class,
        RedeemableItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun rewardDao(): RewardDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun redeemableDao(): RedeemableDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "codecup_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                    .also { Instance = it }
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Instance?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }

            private suspend fun seedDatabase(database: AppDatabase) {
                val coffeeDao = database.coffeeDao()
                val redeemableDao = database.redeemableDao()

                coffeeDao.insertAll(listOf(
                    CoffeeEntity(name = "Heritage Americano", description = "Balanced espresso, steamed milk, and dense foam", basePrice = 70000, imageRes = R.drawable.coffee_1, category = Category.CLASSIC),
                    CoffeeEntity(name = "Midnight Latte", description = "Rich espresso with velvety microfoam and chocolate notes", basePrice = 85000, imageRes = R.drawable.coffee_1, category = Category.SPECIAL),
                    CoffeeEntity(name = "Golden Cappuccino", description = "Classic equal parts espresso, milk, and foam with a hint of gold", basePrice = 75000, imageRes = R.drawable.coffee_1, category = Category.CLASSIC),
                    CoffeeEntity(name = "Emerald Matcha Latte", description = "Premium ceremonial matcha with smooth steamed milk", basePrice = 90000, imageRes = R.drawable.coffee_1, category = Category.SPECIAL),
                    CoffeeEntity(name = "Caramel Macchiato", description = "Freshly steamed milk with vanilla-flavored syrup marked with espresso", basePrice = 80000, imageRes = R.drawable.coffee_1, category = Category.CLASSIC)
                ))

                redeemableDao.insertAll(listOf(
                    RedeemableItemEntity(name = "Free Classic Coffee", description = "Any classic drink from our menu", pointsCost = 500, imageRes = R.drawable.coffee_1),
                    RedeemableItemEntity(name = "The Code Cup Tote Bag", description = "Durable canvas bag for your daily brew", pointsCost = 1500, imageRes = R.drawable.bag),
                    RedeemableItemEntity(name = "Botanical Gift Set", description = "A curated collection of coffee-themed succulents", pointsCost = 3000, imageRes = R.drawable.gift)
                ))
            }
        }
    }
}
