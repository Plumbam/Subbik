package com.example.subbik

data class Note(
    val id: Long? = null,  // ✅ Long как в твоем DbHelper
    val userLogin: String,
    val name: String,
    val cost: Double,
    val day: String,
    val photoPath: String? = null
)
