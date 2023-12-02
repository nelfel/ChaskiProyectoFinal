package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.chaskiproyectofinal.model.PedidoModel
import com.example.chaskiproyectofinal.model.ProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
/**
 * Actividad de confirmación de pago que se muestra al usuario después de realizar el pago.
 * Muestra un resumen del pago, incluyendo detalles como la tarjeta utilizada, el total, impuestos,
 * descuentos y la dirección de entrega. Permite al usuario finalizar el proceso de compra
 * y crea un nuevo pedido en Firestore con la información asociada.
 */
class ConfirmarPagoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmar_pago)
        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Obtener datos pasados desde la actividad anterior
        val restaurantId = intent.getStringExtra("restaurantId")
        val tarjeta = intent.getStringExtra("numeroTarjeta")
        val sumaTotal = intent.getDoubleExtra("sumaTotal", 0.0)
        val listaProductos = intent.getSerializableExtra("listaProduc") as? ArrayList<ProductModel>

        // Obtener el UID del usuario actual
        val uid = auth.currentUser?.uid


        //Los Log son mensajes que se imprimen en consola
        Log.d("Lista Product Models", listaProductos.toString())

        // Calcular impuestos, subtotal y descuentos
        val impuesto = sumaTotal * 0.15
        val subtotal = sumaTotal - impuesto
        val descuento = 0.0

        // Mostrar los detalles del pago en la interfaz de usuario
        findViewById<TextView>(R.id.tvTarjeta).text = tarjeta
        findViewById<TextView>(R.id.tvSubtotalCantidad).text = "$${String.format("%.2f", subtotal)}"
        findViewById<TextView>(R.id.tvDescuento).text = "$${String.format("%.2f", descuento)}"
        findViewById<TextView>(R.id.tvImpuesto).text = "$${String.format("%.2f", impuesto)}"
        findViewById<TextView>(R.id.tvTotalCantidad).text = "$${String.format("%.2f", sumaTotal)}"


        // Obtener la dirección del usuario, por medio de la funcionn obtenerDireccionUsuario
        obtenerDireccionUsuario(uid) { direccion ->
            findViewById<TextView>(R.id.tvUbicacion).text = direccion ?: "Dirección no encontrada"
        }

        val btnPagar = findViewById<Button>(R.id.btnPagar)

        // Configurar el evento de clic del botón de pago
        btnPagar.setOnClickListener{
            if (listaProductos != null) {
                if (restaurantId != null) {
                    //Craer un nuevo pedido por medio de la funcion crearPedido
                    crearPedido(uid,listaProductos,sumaTotal,restaurantId)
                }
            }
            //cuando cree un nuevo pedido que lo mande a principalActivity
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)

        }



    }

    //Método para obtener la dirección del usuario desde Firestore
    private fun obtenerDireccionUsuario(uid: String?, onComplete: (String?) -> Unit) {
        // Si el UID es nulo, no se puede obtener la dirección
        if (uid == null) {
            onComplete(null)
            return
        }

        // Obtener la dirección del usuario desde Firestore
        val db = FirebaseFirestore.getInstance()
        val usuariosRef = db.collection("usuarios").document(uid)

        usuariosRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Acceder a la dirección del usuario desde el documento
                    val direccion = documentSnapshot.getString("direccion")
                    onComplete(direccion)
                } else {
                    onComplete(null) // El documento no fue encontrado
                }
            }
            .addOnFailureListener { exception ->
                // Manejar errores
                Log.w("TAG", "Error getting document: ", exception)
                onComplete(null)
            }
    }

    //Funcion para crear un nuevo pedido
    private fun crearPedido(uid: String?, productos: List<ProductModel>, total: Double, idRestaurant: String) {
        // Verificar que el UID no sea nulo
        if (uid == null) {
            // Manejar el caso en que el UID sea nulo
            return
        }

        // Obtener una referencia a la colección "pedidos"
        val db = FirebaseFirestore.getInstance()
        val pedidosRef = db.collection("pedidos")

        // Crear un nuevo objeto PedidoModel con la información del pedido
        val pedido = PedidoModel(
            idUser = uid,
            fecha = obtenerFechaActual(),  // Se uso una funcion
            hora = obtenerHoraActual(),   //
            restauranteId = idRestaurant,  //
            productos = productos,
            total = total,
            estado = "Pendiente",
            pedidoId = "" //Se genera el id de pedido no es necesario guardar el id en las colecciones
            //Sin embargo en este caso especifico si lo necesitamos para verificar en el Adapter
            //Despues de agregarlo vacio abajo se actualiza el pedido con el generado por firebase
        )

        // Añadir el pedido a Firestore
        pedidosRef.add(pedido)
            .addOnSuccessListener {documentReference ->
                // El pedido se añadió exitosamente
                val pedidoIdGenerado = documentReference.id

                // Actualizar el pedido recién creado con el ID de documento autogenerado
                pedidosRef.document(pedidoIdGenerado)
                    .update("pedidoId", pedidoIdGenerado)
                    .addOnSuccessListener {
                        // El campo "pedidoId" se actualizó exitosamente
                        Log.d("TAG", "Pedido creado exitosamente con pedidoId: $pedidoIdGenerado")
                    }
                    .addOnFailureListener { e ->
                        // Manejar errores al actualizar el campo "pedidoId"
                        Log.w("TAG", "Error al actualizar pedidoId", e)
                    }
            }
            .addOnFailureListener { e ->
                // Manejar errores
                Log.w("TAG", "Error al crear el pedido", e)
            }
    }

    fun obtenerFechaActual(): String {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = Date()
        return formatoFecha.format(fechaActual)
    }

    fun obtenerHoraActual(): String {
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val horaActual = Date()
        return formatoHora.format(horaActual)
    }

    public override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUser = auth.currentUser
            val currentUserId = currentUser?.uid
        }else{
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
        }
    }
}