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
import com.example.personalmidterm.model.VoucherType
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
        RedeemableItemEntity::class,
        VoucherEntity::class
    ],
    version = 4,
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
    abstract fun voucherDao(): VoucherDao

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
                val voucherDao = database.voucherDao()

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
                    RedeemableItemEntity(name = "Ceramic Mug", description = "Durable, stylish for everyday use", pointsCost = 1500, imageRes = R.drawable.redeem_ceramic_mug),
                    RedeemableItemEntity(name = "Premium Thermos ", description = "High-quality, ideal for travel or work.", pointsCost = 2000, imageRes = R.drawable.redeem_thermos)
                ))

                val farFuture = System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365) // 1 year from now
                voucherDao.insertAll(listOf(
                    VoucherEntity(code = "WELCOME20", description = "20.000đ off your order", type = VoucherType.FLAT, value = 20000, minSpend = 0, maxDiscount = null, expiryDate = farFuture),
                    VoucherEntity(code = "COFFEE50", description = "50% off, max 30.000đ", type = VoucherType.PERCENTAGE, value = 50, minSpend = 60000, maxDiscount = 30000, expiryDate = farFuture),
                    VoucherEntity(code = "BEANFRIDAY", description = "15.000đ off orders over 40.000đ", type = VoucherType.FLAT, value = 15000, minSpend = 40000, maxDiscount = null, expiryDate = farFuture),
                    VoucherEntity(code = "SUMMERMATCH", description = "10% off, max 50.000đ", type = VoucherType.PERCENTAGE, value = 10, minSpend = 100000, maxDiscount = 50000, expiryDate = farFuture),
                    VoucherEntity(code = "CODECUP10", description = "10% off orders over 50.000đ", type = VoucherType.PERCENTAGE, value = 10, minSpend = 50000, maxDiscount = null, expiryDate = farFuture),
                    VoucherEntity(code = "BOLDMORNING", description = "5.000đ off, start your day bold", type = VoucherType.FLAT, value = 5000, minSpend = 0, maxDiscount = null, expiryDate = farFuture),
                    VoucherEntity(code = "TEATIME", description = "15% off tea specials, max 20.000đ", type = VoucherType.PERCENTAGE, value = 15, minSpend = 50000, maxDiscount = 20000, expiryDate = farFuture),
                    VoucherEntity(code = "FIRSTORDER", description = "30.000đ off your first 100.000đ order", type = VoucherType.FLAT, value = 30000, minSpend = 100000, maxDiscount = null, expiryDate = farFuture),
                    VoucherEntity(code = "WEEKENDVIBE", description = "20% off weekend treats, max 40.000đ", type = VoucherType.PERCENTAGE, value = 20, minSpend = 80000, maxDiscount = 40000, expiryDate = farFuture),
                    VoucherEntity(code = "LOYALTYLUV", description = "25.000đ off big orders over 150.000đ", type = VoucherType.FLAT, value = 25000, minSpend = 150000, maxDiscount = null, expiryDate = farFuture)
                ))
            }
        }
    }
}
