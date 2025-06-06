package com.example.packup_podejscie_3.domain.model

import kotlinx.serialization.Serializable // Dodaj ten import

@Serializable // Ważne: Ta adnotacja pozwala na serializację/deserializację obiektu przez kotlinx.serialization
data class Wydatki(
    val id: String, // Dodajemy pole 'id'. Będzie ono generowane przez Supabase.
    val nazwaPrzedmiotu: String,
    val kwota: Double,
    val wydarzenieId: String, // Zmieniłem na String (nie opcjonalny), jeśli zawsze jest powiązane z wydarzeniem
    val userId: String
)