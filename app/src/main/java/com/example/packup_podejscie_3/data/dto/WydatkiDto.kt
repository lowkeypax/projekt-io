package com.example.packup_podejscie_3.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WydatkiDto(
    @SerialName("id")
    val id: String? = null, // Uczyniliśmy 'id' opcjonalnym i domyślnie null
    @SerialName("nazwa_przedmiotu")
    val nazwaPrzedmiotu: String,
    @SerialName("kwota")
    val kwota: Double,
    @SerialName("wydarzenieuuid") // Upewnij się, że nazwa kolumny w Supabase to "WydarzenieUUID"
    val wydarzenieId: String,
    @SerialName("user_id")
    val userId: String
)