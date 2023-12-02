package com.example.chaskiproyectofinal.model
/**
 * Modelo de datos que representa un usuario.
 * Incluye propiedades como nombres, apellidos, correo electrónico, contraseña,
 * año de nacimiento, dirección, lista de calificaciones de repartidores y tipo de usuario.
 */
data class UserModel(
    val nombres: String = "",
    val apellidos: String = "",
    val correo: String = "",
    val contrasenia: String = "",
    val anionacimiento: String ="",
    val direccion: String= "",
    val calificaciones: List<CalificacionRepartidorModel>,
    val tipo: String = ""

)
