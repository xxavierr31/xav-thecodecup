package com.example.personalmidterm.model

data class RankTier(
    val rank: String,
    val discountPercent: Double
)

val rankTiers = listOf(
    RankTier("Sprout", 0.0),
    RankTier("Naturalist", 0.05),
    RankTier("Botanist", 0.10),
    RankTier("Master Gardener", 0.15)
)
