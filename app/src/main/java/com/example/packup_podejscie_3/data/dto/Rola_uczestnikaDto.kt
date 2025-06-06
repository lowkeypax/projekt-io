package com.example.packup_podejscie_3.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RolaUczestnikaDto(
    @SerialName("rola")
    val rola: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("wydarzenieuuid")
    val wydarzenieId: String
)
