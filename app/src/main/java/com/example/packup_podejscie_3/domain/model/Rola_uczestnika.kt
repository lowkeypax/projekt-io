package com.example.packup_podejscie_3.domain.model

import kotlinx.serialization.Serializable // Dodaj ten import

@Serializable // Nadal potrzebne, aby KotlinXSerialization mogło zmapować dane z Supabase
data class RolaUczestnika(
    val rola: String,
    val userId: String,
    val wydarzenieId: String
)