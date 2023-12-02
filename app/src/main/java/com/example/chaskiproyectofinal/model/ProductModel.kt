package com.example.chaskiproyectofinal.model

import java.io.Serializable
/**
 * Representa un modelo de datos para almacenar información sobre un producto.
 * Esta data class incluye propiedades como el nombre del producto, una descripción,
 * el precio, la URL de la imagen asociada al producto, el identificador del restaurante al que pertenece,
 * y la cantidad disponible. Al implementar la interfaz Serializable, esta clase se vuelve
 * capaz de ser serializada para transmitir información o almacenarse en un formato recuperable.
 * Las propiedades predeterminadas de cada producto se proporcionan para asegurar valores por defecto
 * en caso de que no se especifiquen al crear instancias de esta clase.
 */
data class ProductModel(
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val urlImagenProduct: String = "",
    val restauranteId: String = "",
    var cantidad: Int = 0
) : Serializable
