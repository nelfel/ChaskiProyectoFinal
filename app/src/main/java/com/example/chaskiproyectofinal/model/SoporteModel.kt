package com.example.chaskiproyectofinal.model

import java.io.Serializable
/**
 * Modelo de datos que representa un caso de soporte o asistencia.
 * Incluye propiedades como estado, número de caso, fecha, hora, categoría, asunto, mensaje,
 * identificación de usuario, identificación de soporte, y identificación de restaurante.
 * Algunas propiedades pueden ser nulas (estado, número, fecha, hora, categoría, asunto, mensaje,
 * userId, soporteId) y se han establecido valores predeterminados nulos para ellas.
 */
data class SoporteModel(
    var estado: String? = "",
    val num: Int? = 0,
    val fecha: String?= "",
    val hora: String?= "",
    val categoria: String?= "",
    val asunto: String?= "",
    val mensaje: String?= "",
    val userId: String?= "",
    val soporteId: String?= "",
    val idRestaurante: String = "",
) : Serializable
