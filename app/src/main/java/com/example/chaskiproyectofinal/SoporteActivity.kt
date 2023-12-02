package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaskiproyectofinal.adapter.AdapterPedidos
import com.example.chaskiproyectofinal.adapter.AdapterRestaurantes
import com.example.chaskiproyectofinal.adapter.AdapterSoporte
import com.example.chaskiproyectofinal.dialog.CustomDialogSoporte
import com.example.chaskiproyectofinal.model.PedidoModel
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.example.chaskiproyectofinal.model.SoporteModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
/**
 * `SoporteActivity` es una actividad de Android que permite a los usuarios enviar solicitudes de soporte a través de un formulario.
 * También proporciona una lista de solicitudes de soporte anteriores. Los usuarios pueden agregar nuevas solicitudes y ver el estado
 * y detalles de las solicitudes anteriores. La información de la solicitud, como el asunto y el mensaje, se almacena en Firestore.
 *
 * @property toggle Toggle para implementar la funcionalidad de navegación lateral.
 * @property mFirebase Instancia de FirebaseFirestore para interactuar con la base de datos Firestore.
 * @property recyclerView RecyclerView para mostrar la lista de solicitudes de soporte.
 * @property mAdapter Adaptador para el RecyclerView que maneja la visualización de las solicitudes de soporte.
 * @property auth Instancia de FirebaseAuth para gestionar la autenticación del usuario.
 * @property contador Contador atómico utilizado para generar números únicos de solicitud.
 */
class SoporteActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mFirebase: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: AdapterSoporte
    private lateinit var auth: FirebaseAuth
    private val contador = AtomicInteger(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soporte)
        // ... (Inicialización de componentes y obtención de instancias)
        val txtAgg = findViewById<TextView>(R.id.txtAgg)
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        val btnAtras: ImageView = findViewById(R.id.btnBack)

        // Configuración del botón de retroceso para redirigir al usuario según su rol.
        //Es igual que la ListPedidoActivity
        btnAtras.setOnClickListener {
            if (uid?.let { it1 -> tieneRolRestaurante(it1) } == true) {
                val intent = Intent(this, MainRestaurantActivity::class.java)
                this.startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, PrincipalActivity::class.java)
                this.startActivity(intent)
                finish()
            }
        }

        // Inicialización del RecyclerView y obtención de las solicitudes de soporte desde Firestore.
        recyclerView = findViewById(R.id.rvSoporte)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        mFirebase = FirebaseFirestore.getInstance()
        if (uid != null) {
            mFirebase = FirebaseFirestore.getInstance()
            val query: Query = mFirebase.collection("soporte")

            // Realizar la consulta para obtener todos los pedidos asociados al uid
            // Obtener opciones de configuración para el RecyclerView y el adaptador.
            val firestoreRecyclerOptions: FirestoreRecyclerOptions<SoporteModel> =
                FirestoreRecyclerOptions.Builder<SoporteModel>()
                    .setQuery(
                        query,
                        SoporteModel::class.java
                    ).build()

            //configurar el adapter
            mAdapter = AdapterSoporte(mFirebase,firestoreRecyclerOptions)
            recyclerView.adapter = mAdapter
        }else {
            // Si no hay usuario autenticado, redirigir al LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
        }


        //Cuando alguien haga clic en el boton mas
        txtAgg.setOnClickListener {
            // Cuando se hace clic en el TextView, abre el DialogFragment
            //El dialogFragment de soporte
            val dialogSoporte = CustomDialogSoporte()
            dialogSoporte.setDialogListener(object : CustomDialogSoporte.DialogListener {
                override fun onEnviarClicked(editTextAsunto: String, editTextMensaje: String) {
                    crearPedido(uid,
                    editTextAsunto, editTextMensaje)
                }
            })
            dialogSoporte.show(supportFragmentManager, "CustomDialogSoporte")
        }
    }

    //Funcion para crear un nuevo pedido
    private fun crearPedido(uid: String?,
                            editTextAsunto: String, editTextMensaje: String) {
        // Verificar que el UID no sea nulo
        if (uid == null) {
            // Manejar el caso en que el UID sea nulo
            return
        }

        // Obtener una referencia a la colección "pedidos"
        val db = FirebaseFirestore.getInstance()
        val soporteRef = db.collection("soporte")

        // Crear un nuevo objeto PedidoModel con la información del pedido
        val soporte = SoporteModel(
            estado = "Pendiente",
            num = generarNumeroUnico(),
            fecha = obtenerFechaActual(),  // Puedes ajustar la fecha según tus necesidades
            hora = obtenerHoraActual(),
            categoria = "App",
            asunto = editTextAsunto,
            mensaje = editTextMensaje,
            userId = uid,
            soporteId = "",
            idRestaurante = "",
        )

        // Añadir el pedido a Firestore
        soporteRef.add(soporte)
            .addOnSuccessListener {documentReference ->
                // El pedido se añadió exitosamente
                val soporteIdGenerado = documentReference.id

                // Actualizar el pedido recién creado con el ID de documento autogenerado
                soporteRef.document(soporteIdGenerado)
                    .update("soporteId", soporteIdGenerado)
                    .addOnSuccessListener {
                        // El campo "pedidoId" se actualizó exitosamente
                        Log.d("TAG", "Pedido creado exitosamente con pedidoId: $soporteIdGenerado")
                    }
                    .addOnFailureListener { e ->
                        // Manejar errores al actualizar el campo "pedidoId"
                        Log.w("TAG", "Error al actualizar pedidoId", e)
                    }
            }
            .addOnFailureListener { e ->
                // Manejar errores
                Log.w("TAG", "Error al crear el soporte", e)
            }
    }
    private fun tieneRolRestaurante(uid: String): Boolean {
        val usuariosCollection = mFirebase.collection("usuarios")
        val restaurantesCollection = mFirebase.collection("restaurantes")

        return runBlocking {
            try {
                // Verificar si el uid existe en la colección "usuarios"
                val usuarioDocument = usuariosCollection.document(uid).get().await()

                // Si el documento del usuario normal existe, entonces no tiene el rol de restaurante
                if (usuarioDocument.exists()) {
                    return@runBlocking false
                }

                // Si el documento del usuario normal no existe, verificar si existe en la colección "restaurantes"
                val restauranteDocument = restaurantesCollection.document(uid).get().await()

                // Devolver true si el documento del restaurante existe
                return@runBlocking restauranteDocument.exists()
            } catch (e: Exception) {
                // Manejar la excepción si ocurre algún error al consultar la base de datos
                Log.e("ListPedidosActivity", "Error al verificar el rol de restaurante", e)
                return@runBlocking false
            }
        }
    }

    fun obtenerFechaActual(): String {
        val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fechaActual = Date()
        return formatoFecha.format(fechaActual)
    }


    fun generarNumeroUnico(): Int {
        return contador.incrementAndGet()
    }

    fun obtenerHoraActual(): String {
        val formatoHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val horaActual = Date()
        return formatoHora.format(horaActual)
    }

    public override fun onStart() {
        super.onStart()
        mAdapter.startListening()
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

    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

}