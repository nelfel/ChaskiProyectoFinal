package com.example.chaskiproyectofinal.model

/**
 * Representa un modelo de datos para almacenar la calificación de un repartidor.
 * Esta clase utiliza la característica de data class en Kotlin, lo que significa que
 * automáticamente proporciona una implementación predeterminada de funciones como equals(),
 * hashCode(), y toString(), basándose en las propiedades declaradas en el constructor primario.
 * La data class también permite una creación concisa de instancias y facilita la copia de objetos
 * con algunas propiedades modificadas. En este caso, el modelo incluye la identificación del
 * repartidor, la puntuación otorgada y un comentario asociado.
 */
data class CalificacionRepartidorModel(
    val repartidor: String = "",
    val puntuacion: Int = 0,
    val comentario: String = ""
)
