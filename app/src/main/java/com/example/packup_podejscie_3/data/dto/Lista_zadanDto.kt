package com.example.packup_podejscie_3.data.dto

import com.example.packup_podejscie_3.domain.model.Lista_zadan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Lista_zadanDto(
    @SerialName("nazwa_zadania")
    val nazwa: String,

    @SerialName("opis_zadania")
    val opis: String? = null,

    @SerialName("czy_wykonano")
    val czyWykonano: Boolean = false,

    @SerialName("wydarzenieuuid")
    val wydarzenieId: String,

    @SerialName("user_id")
    val userId: String
)

// Funkcja rozszerzająca do konwersji DTO na model domenowy
fun Lista_zadanDto.asDomainModel(): Lista_zadan {
    return Lista_zadan(
        nazwa = this.nazwa,
        opis = this.opis,
        czyWykonano = this.czyWykonano,
        wydarzenieId = this.wydarzenieId,
        userId = this.userId
    )
}