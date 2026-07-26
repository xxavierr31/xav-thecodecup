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
    version = 3,
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
                    CoffeeEntity(name = "Americano", description = "Bold espresso diluted with water for a classic, smooth finish.", basePrice = 45000, imageRes = R.drawable.drinks_americano, category = Category.CLASSIC, canBeHot = true, canBeIced = true),
                    CoffeeEntity(name = "Cappuccino", description = "Perfectly balanced espresso, steamed milk, and rich foam.", basePrice = 50000, imageRes = R.drawable.drinks_cappuccino, category = Category.CLASSIC, canBeHot = true, canBeIced = false),
                    CoffeeEntity(name = "Mocha", description = "Rich chocolate meets bold espresso and velvety milk.", basePrice = 55000, imageRes = R.drawable.drinks_mocha, category = Category.CLASSIC, canBeHot = true, canBeIced = true),
                    CoffeeEntity(name = "Vietnamese Coffee", description = "Traditional drip coffee with sweet condensed milk.", basePrice = 40000, imageRes = R.drawable.drinks_vietnamese_coffee, category = Category.CLASSIC, canBeHot = true, canBeIced = true),
                    CoffeeEntity(name = "Dark Espresso", description = "Intense, concentrated shot of our finest dark roast.", basePrice = 40000, imageRes = R.drawable.drinks_dark_espresso, category = Category.CLASSIC, canBeHot = true, canBeIced = false),
                    CoffeeEntity(name = "Matcha Latte", description = "Premium ceremonial grade matcha whisked with creamy milk.", basePrice = 55000, imageRes = R.drawable.drinks_matcha_latte, category = Category.CLASSIC, canBeHot = true, canBeIced = true),
                    CoffeeEntity(name = "Fluffy Cafe Au Lait", description = "Light and airy blend of coffee and extra frothy milk.", basePrice = 50000, imageRes = R.drawable.drinks_fluffy_cafe_au_lait, category = Category.CLASSIC, canBeHot = true, canBeIced = true),
                    CoffeeEntity(name = "Cream Mocha", description = "Indulgent mocha topped with a layer of luxurious silk cream.", basePrice = 65000, imageRes = R.drawable.drinks_cream_mocha, category = Category.SPECIAL, canBeHot = false, canBeIced = true),
                    CoffeeEntity(name = "Dreamy Clouds Tea", description = "A magical swirl of blue and purple, floral and creamy with chewy delight.", basePrice = 60000, imageRes = R.drawable.drinks_dreamy_clouds_tea, category = Category.SPECIAL, canBeHot = false, canBeIced = true),
                    CoffeeEntity(name = "Dulce De Leche Latte", description = "Sweet, warm caramel-toffee notes blended with smooth espresso.", basePrice = 70000, imageRes = R.drawable.drinks_dulce_de_leche_latte, category = Category.SPECIAL, canBeHot = true, canBeIced = true),
                    CoffeeEntity(name = "Matcha Cold Foam", description = "Refreshing iced coffee drink topped with a novel, velvety matcha-infused foam.", basePrice = 65000, imageRes = R.drawable.drinks_matcha_cold_foam, category = Category.SPECIAL, canBeHot = false, canBeIced = true),
                    CoffeeEntity(name = "Matcha Fizz Mocktail", description = "Sparkling and bright matcha infusion with a tangy hint of citrus.", basePrice = 60000, imageRes = R.drawable.drinks_matcha_fizz_mocktail, category = Category.SPECIAL, canBeHot = false, canBeIced = true)
                ))

                redeemableDao.insertAll(listOf(
                    RedeemableItemEntity(name = "Free Classic Brew", description = "Any classic drink from our menu", pointsCost = 300, imageRes = R.drawable.drinks_dark_espresso),
                    RedeemableItemEntity(name = "Free Special Brew", description = "Any special drink from our menu", pointsCost = 500, imageRes = R.drawable.drinks_dulce_de_leche_latte),
                    RedeemableItemEntity(name = "Reusable Straw Set", description = "Eco-friendly stainless steel straws with cleaning brush", pointsCost = 800, imageRes = R.drawable.redeem_reusable_straw_set),
                    RedeemableItemEntity(name = "Ceramic Mug", description = "Durable ceramic mug for everyday use", pointsCost = 1500, imageRes = R.drawable.redeem_ceramic_mug),
                    RedeemableItemEntity(name = "Premium Thermos ", description = "High-quality vacuum flask ideal for travel or work.", pointsCost = 2000, imageRes = R.drawable.redeem_thermos)
                ))
            }
        }
    }
}
