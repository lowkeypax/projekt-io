package com.example.packup_podejscie_3.data.dto

import com.example.packup_podejscie_3.domain.model.Ankiety
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AnkietyDto(
    @SerialName("id")
    val id: String? = null,

    @SerialName("pytanie")
    val pytanie: String,

    @SerialName("wydarzenieuuid")
    val wydarzenieId: String
)

// DODAŁEM TĘ FUNKCJĘ ROZSZERZAJĄCĄ TUTAJ
// Jest publiczna i umożliwia konwersję AnkietyDto na model domenowy Ankiety
fun AnkietyDto.asDomainModel(): Ankiety {
    return Ankiety(
        id = this.id,
        pytanie = this.pytanie,
        wydarzenieId = this.wydarzenieId
    )
}