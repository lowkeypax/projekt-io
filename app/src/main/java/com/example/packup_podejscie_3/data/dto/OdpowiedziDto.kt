package com.example.packup_podejscie_3.data.dto

import com.example.packup_podejscie_3.domain.model.Odpowiedzi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class OdpowiedziDto(
    @SerialName("id")
    val id: String? = null,

    @SerialName("odpowiedz")
    val odpowiedz: String,

    @SerialName("liczba_glosow")
    val liczbaGlosow: Int? = null,

    @SerialName("ankietauuid")
    val ankietaId: String
)

// Funkcja rozszerzająca do konwersji DTO na model domenowy
fun OdpowiedziDto.asDomainModel(): Odpowiedzi {
    return Odpowiedzi(
        id = this.id,
        odpowiedz = this.odpowiedz,
        liczbaGlosow = this.liczbaGlosow,
        ankietaId = this.ankietaId
    )
}