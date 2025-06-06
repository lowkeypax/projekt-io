package com.example.packup_podejscie_3.domain.model

import java.util.UUID


data class Lista_zadan(
    val nazwa: String,
    val opis: String?,
    val czyWykonano: Boolean,
    val wydarzenieId: String,
    val userId: String
)
