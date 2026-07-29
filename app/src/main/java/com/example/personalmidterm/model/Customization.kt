package com.example.personalmidterm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Sweetness(val label: String) : Parcelable {
    ZERO("0%"),
    TWENTY_FIVE("25%"),
    FIFTY("50%"),
    SEVENTY_FIVE("75%"),
    HUNDRED("100%")
}

@Parcelize
enum class Temperature(val label: String) : Parcelable {
    HOT("Hot"),
    ICED("Iced")
}

@Parcelize
enum class TemperatureLevel(val label: String) : Parcelable {
    ZERO("0%"),
    TWENTY_FIVE("25%"),
    FIFTY("50%"),
    SEVENTY_FIVE("75%"),
    HUNDRED("100%")
}

@Parcelize
enum class Flavor(val label: String, val price: Long) : Parcelable {
    CARAMEL("Caramel", 5000L),
    VANILLA("Vanilla", 5000L),
    HAZELNUT("Hazelnut", 5000L)
}

@Parcelize
data class Customization(
    val sweetness: Sweetness = Sweetness.FIFTY,
    val temperature: Temperature = Temperature.ICED,
    val temperatureLevel: TemperatureLevel = TemperatureLevel.FIFTY,
    val shots: Int = 0,
    val flavors: List<Flavor> = emptyList()
) : Parcelable {
    fun getDescription(): String = buildString {
        append("${temperature.label}, ")
        append("${sweetness.label} Sweet, ")

        val levelLabel = if (temperature == Temperature.ICED) "Ice" else "Heat"
        append("${temperatureLevel.label} $levelLabel")

        if (shots > 0) {
            append(", $shots extra ${if (shots == 1) "shot" else "shots"}")
        }

        if (flavors.isNotEmpty()) {
            append(", ")
            append(flavors.joinToString(", ") { it.label })
        }
    }
}
