package com.bagas.klinikapp.model

data class UserAppointment(
    val uid: String? = "",
    val name: String? = "",
    val email: String? = "",
    val date: String? = "",
    val poli: String? = "",
    val time: String? = "",
    val description: String? = "",
    val noantrian: Int? = null
)
