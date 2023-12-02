package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaskiproyectofinal.adapter.AdapterPedidos
import com.example.chaskiproyectofinal.adapter.AdapterRepartidores
import com.example.chaskiproyectofinal.adapter.AdapterRestaurantes
import com.example.chaskiproyectofinal.model.PedidoModel
import com.example.chaskiproyectofinal.model.RepartidorModel
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

/**
 * Actividad que muestra la lista de pedidos asociados al usuario actual, ya sea como cliente o restaurante.
 * Se utiliza FirestoreRecyclerAdapter para mostrar los datos en un RecyclerView.
 * Permite al usuario volver a la pantalla principal o a la pantalla principal del restaurante según su rol.
 */
class ListPedidosActivity : AppCompatActivity() {
    // Instancias necesarias para interactuar con Firebase
    private lateinit var mFirebase: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    // Componentes de la interfaz de usuario
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: AdapterPedidos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_pedidos)

        // Inicializar componentes de la interfaz de usuario
        recyclerView = findViewById(R.id.rvPedidos)
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        // Inicializar instancia de Firebase Firestore
        mFirebase = FirebaseFirestore.getInstance()
        val query: Query = mFirebase.collection("pedidos")

        // Obtener el usuario actualmente autenticado
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentUserId = currentUser?.uid

        //configurar boton atra
        val btnAtras: ImageView = findViewById(R.id.btnBack)

        //Si alguien hace clic sobre el boton atras
        btnAtras.setOnClickListener {
            //verifica que rol tiene el usuario si es restaurante entra al if si es usuario normal al else
            //se usa la funcion que esta mas abbajo llamada tieneRolRestaurante que devuelve un boolen
            // true si es restaurante, false si es usuario
            // Lo manda a la pantalla correspodinete
            // recordemos que se usa la misma pantalla de pedidos para los dos usuarios pero los roles son
            //diferentes y la pantalla principal para cada uno es diferente
            if (currentUserId?.let { it1 -> tieneRolRestaurante(it1) } == true) {
                val intent = Intent(this, MainRestaurantActivity::class.java)
                this.startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, PrincipalActivity::class.java)
                this.startActivity(intent)
                finish()
            }
        }

        //Aqui igual se verifica el rol del usuario por medio de la misma funcion
        //y asi usar el adapter correcto con los datos correctos
        //ejemplos si es usuario se le mostraran los pedidos de el como usuario
        // si es restaurante igual se le mostraran los pedidos que tiene como restaurante
        if (currentUserId != null) {
            if (tieneRolRestaurante(currentUserId)) {
                mFirebase = FirebaseFirestore.getInstance()
                val query: Query = mFirebase.collection("pedidos")

                // Realizar la consulta para obtener todos los pedidos asociados al uid
                val firestoreRecyclerOptions: FirestoreRecyclerOptions<PedidoModel> =
                    FirestoreRecyclerOptions.Builder<PedidoModel>()
                        .setQuery(
                            query.whereEqualTo("restauranteId", currentUserId),
                            PedidoModel::class.java
                        ).build()

                mAdapter = AdapterPedidos(mFirebase,firestoreRecyclerOptions)
                recyclerView.adapter = mAdapter

                }  else {
                mFirebase = FirebaseFirestore.getInstance()
                val query: Query = mFirebase.collection("pedidos")

                // Realizar la consulta para obtener todos los pedidos asociados al uid
                val firestoreRecyclerOptions: FirestoreRecyclerOptions<PedidoModel> =
                    FirestoreRecyclerOptions.Builder<PedidoModel>()
                        .setQuery(
                            query.whereEqualTo("idUser", currentUserId),
                            PedidoModel::class.java
                        ).build()
                mAdapter = AdapterPedidos(mFirebase,firestoreRecyclerOptions)
                recyclerView.adapter = mAdapter

            }
            }else {
            // Si no hay usuario autenticado, redirigir al LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
        }

    }

    // Método para verificar si un usuario tiene el rol de restaurante
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

    // Método llamado cuando la actividad deja de estar en primer plano
    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    public override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
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

}