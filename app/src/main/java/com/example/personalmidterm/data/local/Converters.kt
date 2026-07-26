package com.example.personalmidterm.data.local

import androidx.room.TypeConverter
import com.example.personalmidterm.model.*

class Converters {
    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(name: String): Category = Category.valueOf(name)

    @TypeConverter
    fun fromSweetness(sweetness: Sweetness): String = sweetness.name

    @TypeConverter
    fun toSweetness(name: String): Sweetness = Sweetness.valueOf(name)

    @TypeConverter
    fun fromTemperature(temp: Temperature): String = temp.name

    @TypeConverter
    fun toTemperature(name: String): Temperature = Temperature.valueOf(name)

    @TypeConverter
    fun fromTemperatureLevel(level: TemperatureLevel): String = level.name

    @TypeConverter
    fun toTemperatureLevel(name: String): TemperatureLevel = TemperatureLevel.valueOf(name)

    @TypeConverter
    fun fromFlavorList(flavors: List<Flavor>): String = flavors.joinToString(",") { it.name }

    @TypeConverter
    fun toFlavorList(data: String): List<Flavor> =
        if (data.isBlank()) emptyList() else data.split(",").map { Flavor.valueOf(it) }

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String = status.name

    @TypeConverter
    fun toOrderStatus(name: String): OrderStatus = OrderStatus.valueOf(name)

    @TypeConverter
    fun fromOrderResultStatus(status: OrderResultStatus): String = status.name

    @TypeConverter
    fun toOrderResultStatus(name: String): OrderResultStatus = OrderResultStatus.valueOf(name)
}
