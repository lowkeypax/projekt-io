package com.example.packup_podejscie_3.data.dto

import com.example.packup_podejscie_3.domain.model.Wydarzenie
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat // Import for date formatting

@Serializable
data class WydarzenieDto(
    @SerialName("nazwa_wydarzenia")
    val nazwa: String,

    @SerialName("data_rozpoczecia")
    val data_rozpoczecia: String, // Keep as String for serialization with Supabase

    @SerialName("data_zakonczenia")
    val data_zakonczenia: String, // Keep as String for serialization with Supabase

    @SerialName("opis_wydarzenia")
    val opis_wydarzenia: String? = null,

    @SerialName("wydarzenieuuid")
    val id: String? = null, // ID from Supabase
)

// Extension function to convert WydarzenieDto to domain model Wydarzenie
fun WydarzenieDto.asDomainModel(): Wydarzenie {
    // Assuming Supabase stores dates in ISO 8601 format (e.g., "YYYY-MM-DD")
    // If not, you'll need to define a custom DateTimeFormat
    return Wydarzenie(
        id = this.id ?: throw IllegalArgumentException("Wydarzenie ID cannot be null when converting to domain model"),
        nazwa = this.nazwa,
        data_rozpoczecia = LocalDate.parse(this.data_rozpoczecia), // Convert String to LocalDate
        data_zakonczenia = LocalDate.parse(this.data_zakonczenia), // Convert String to LocalDate
        opis_wydarzenia = this.opis_wydarzenia
    )
}

// Extension function to convert domain model Wydarzenie to WydarzenieDto
fun Wydarzenie.asDto(): WydarzenieDto {
    // Convert LocalDate back to String for serialization to Supabase
    // Using ISO 8601 format (yyyy-MM-dd) which is generally compatible with databases
    return WydarzenieDto(
        id = this.id, // Keep the ID
        nazwa = this.nazwa,
        data_rozpoczecia = this.data_rozpoczecia.toString(), // Convert LocalDate to String
        data_zakonczenia = this.data_zakonczenia.toString(), // Convert LocalDate to String
        opis_wydarzenia = this.opis_wydarzenia
    )
}