package com.bagas.klinikapp.model

data class PersonalUser(
    val uid: String? = "",
    val username: String? = "",
    val name: String? = "",
    val email: String? = "",
    val phonenumber: String = "",
    val age: String = "",
    val gender: String = "",
    val address: String = "",
    val usertype: String = "",
    val bloodtype: String = "",
    val obat: String = "",
    val riwayatPenyakit: String = ""
)
