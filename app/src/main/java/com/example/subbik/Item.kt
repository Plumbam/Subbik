package com.example.subbik

data class Item(
    val id: Int,
    val name: String,
    val cost: String,
    val day: String? = null,
    val photoPath: String? = null,
    val image: String = "netflix"
)
