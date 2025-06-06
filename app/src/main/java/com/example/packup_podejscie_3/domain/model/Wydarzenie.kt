package com.example.packup_podejscie_3.domain.model

import kotlinx.datetime.LocalDate
import java.util.UUID

data class Wydarzenie(
    val id: String,
    val nazwa: String,
    val data_rozpoczecia: LocalDate, // Zmieniono na LocalDate
    val data_zakonczenia: LocalDate, // Zmieniono na LocalDate
    val opis_wydarzenia: String?
)