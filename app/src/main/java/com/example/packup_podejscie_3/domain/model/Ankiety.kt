package com.example.packup_podejscie_3.domain.model

import java.util.UUID


data class Ankiety(
    val id: String?,
    val pytanie: String,
    val wydarzenieId: String
)