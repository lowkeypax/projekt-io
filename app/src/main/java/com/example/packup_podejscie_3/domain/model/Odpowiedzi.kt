package com.example.packup_podejscie_3.domain.model

data class Odpowiedzi(
    val id: String?,
    val ankietaId: String,
    val odpowiedz: String,
    val liczbaGlosow: Int?
)
