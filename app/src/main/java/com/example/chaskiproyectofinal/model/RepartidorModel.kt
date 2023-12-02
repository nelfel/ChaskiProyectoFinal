package com.example.chaskiproyectofinal.model

/**
 * Modelo de datos que representa a un repartidor.
 * Incluye propiedades como nombre, URL de la foto del repartidor y calificación.
 */
data class RepartidorModel(
    val nombre: String = "",
    val urlFoto: String = "",
    val rating: Double = 0.0
)
