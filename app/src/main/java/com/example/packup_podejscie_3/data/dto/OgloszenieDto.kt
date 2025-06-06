package com.example.packup_podejscie_3.data.dto

import com.example.packup_podejscie_3.domain.model.Ogloszenie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OgloszenieDto(
    @SerialName("nazwa_ogloszenia")
    val nazwa: String,

    @SerialName("opis_ogloszenia")
    val opis: String? = null,

    @SerialName("wydarzenieuuid")
    val wydarzenieId: String
)

// Funkcja rozszerzająca do konwersji DTO na model domenowy
fun OgloszenieDto.asDomainModel(): Ogloszenie {
    return Ogloszenie(
        nazwa = this.nazwa,
        opis = this.opis,
        wydarzenieId = this.wydarzenieId
    )
}