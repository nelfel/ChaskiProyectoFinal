package com.example.chaskiproyectofinal.model

import java.io.Serializable
/**
 * Representa un modelo de datos para almacenar información sobre un pedido.
 * Esta data class se utiliza para estructurar los detalles de un pedido, incluyendo
 * la identificación del usuario, la fecha y hora del pedido, el identificador del restaurante,
 * una lista de productos (utilizando el modelo ProductModel), el total del pedido, el estado actual,
 * y un identificador único para el pedido. La implementación de la interfaz Serializable sugiere
 * que esta clase se puede serializar para transmitir o almacenar en un formato recuperable.
 * Se proporciona un constructor adicional sin argumentos requerido por Firebase para la deserialización.
 */
data class PedidoModel(
    val idUser: String = "",
    val fecha: String = "",
    val hora: String = "",
    val restauranteId: String = "",
    val productos: List<ProductModel> = emptyList(),
    val total: Double = 0.0,
    var estado: String = "",
    val pedidoId: String = ""
) {
    // Constructor sin argumentos requerido por Firebase
    constructor() : this("", "", "", "", emptyList(), 0.0, "", "")
}
