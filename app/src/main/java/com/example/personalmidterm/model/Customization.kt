package com.example.personalmidterm.model

enum class Sweetness(val label: String) {
    ZERO("0%"),
    TWENTY_FIVE("25%"),
    FIFTY("50%"),
    SEVENTY_FIVE("75%"),
    HUNDRED("100%")
}

enum class Temperature(val label: String) {
    HOT("Hot"),
    ICED("Iced")
}

enum class Intensity(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class Flavor(val label: String, val price: Long) {
    CARAMEL("Caramel", 5000L),
    VANILLA("Vanilla", 5000L),
    HAZELNUT("Hazelnut", 5000L)
}

data class Customization(
    val sweetness: Sweetness = Sweetness.FIFTY,
    val temperature: Temperature = Temperature.ICED,
    val intensity: Intensity = Intensity.MEDIUM,
    val shots: Int = 1,
    val flavors: List<Flavor> = emptyList()
)
