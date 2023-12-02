package com.example.chaskiproyectofinal.model

/**
 * Modelo de datos que representa un restaurante.
 * Incluye propiedades como nombre, dirección, teléfono, correo electrónico, contraseña,
 * URL de imagen del restaurante, URL de comida/restaurante, tiempo de demora y calificación.
 */
data class RestaurantModel(
    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val correo: String = "",
    val contrasenia: String = "",
    val urlImagenRestaurant: String = "",
    val urlComidaRest: String = "",
    val tiempoDeDemora: String = "",
    val rating: Double = 0.0
)
