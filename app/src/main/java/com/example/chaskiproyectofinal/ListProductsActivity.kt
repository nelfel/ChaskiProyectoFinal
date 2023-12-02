package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaskiproyectofinal.adapter.AdapterProducts
import com.example.chaskiproyectofinal.model.ProductModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson

/**
 * Actividad que muestra la lista de productos de un restaurante específico.
 * Permite al usuario realizar búsquedas de productos y agregarlos al carrito.
 * También incluye un panel de navegación lateral con opciones como ir a la pantalla principal,
 * ver el seguimiento de pedidos, acceder al soporte o cerrar sesión.
 */
class ListProductsActivity : AppCompatActivity() {
   // Instancias necesarias para interactuar con Firebase
    private lateinit var mFirebase: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    // Componentes de la interfaz de usuario
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: AdapterProducts
    var search_view: SearchView? = null
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_products)

        // Habilitar el botón de retroceso en la barra de acciones
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        //Obtener id de restaurante de la Activity anterior(PrincipalActivity)
        val restaurantId = intent.getStringExtra("restaurantId")

        // Inicializar componentes de la interfaz de usuario
        recyclerView = findViewById(R.id.rvProductos)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        mFirebase = FirebaseFirestore.getInstance()
        search_view = findViewById(R.id.searchProduct)
        Log.d("Restaurante ID EN LA PANTALLA LIST PRODUCT", restaurantId.toString())

        // Configurar la consulta de productos para el restaurante específico
        val query: Query = mFirebase.collection("productos")
            .whereEqualTo("restauranteId", restaurantId)

        // Configurar las opciones para el adaptador FirestoreRecyclerAdapter
        val options: FirestoreRecyclerOptions<ProductModel> = FirestoreRecyclerOptions.Builder<ProductModel>()
            .setQuery(query, ProductModel::class.java)
            .build()

        // Inicializar el adaptador con las opciones configuradas
        mAdapter = AdapterProducts(mFirebase,options)
        recyclerView.adapter = mAdapter
        search_view()

        // Configurar el botón de carrito de compras
        val imgShopping = findViewById<ImageView>(R.id.imgShopping)
        imgShopping.setOnClickListener {
            // Obtener la lista de productos seleccionados
            val listaProductos = mAdapter.listaProductos

            // Convertir la lista a formato JSON
            val gson = Gson()
            val productListJson = gson.toJson(listaProductos)

            // Iniciar la actividad del carrito de compras y pasar la información necesaria
            val intent = Intent(this, CarritoActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            intent.putExtra("listaProductos", productListJson)
            startActivity(intent)
            //Log.d("Lista de productos que envia", productListJson.toString())

        }

//Implementar el panel de navegacion lateral
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayoutRoot)
        val navView = findViewById<NavigationView>(R.id.navView)

        toggle = ActionBarDrawerToggle(this@ListProductsActivity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.itemIconTintList = null
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navPrincipal -> {
                    val intent = Intent(this, PrincipalActivity::class.java)
                    startActivity(intent)
                }
                R.id.navSeguimiento -> {
                    val intent = Intent(this, ListPedidosActivity::class.java)
                    startActivity(intent)
                }
                R.id.navSoporte -> {
                    val intent = Intent(this, SoporteActivity::class.java)
                    startActivity(intent)
                }
                R.id.navCerrar -> {
                    auth.signOut()
                    val intent = Intent(this, LoginActivity::class.java)
                    this.startActivity(intent)
                    true

                }

            }
            true
        }
    }
    // Método para configurar la funcionalidad de búsqueda
    private fun search_view() {
        search_view!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                textSearch(s)
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                textSearch(s)
                return false
            }
        })
    }
    //Recordar busqueda se debe hacer tal cual esta ingresada en la bd
    // Primer letra mayuscula resto en minuscula
    fun textSearch(s: String) {
        val restaurantId = intent.getStringExtra("restaurantId")
        mFirebase = FirebaseFirestore.getInstance()
        val query: Query = mFirebase.collection("productos")
            .whereEqualTo("restauranteId", restaurantId)
        // Convertir el texto de búsqueda a minúsculas
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<ProductModel> =
            FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(
                    query.orderBy("nombre")
                        .startAt(s).endAt("$s~"), ProductModel::class.java
                ).build()
        mAdapter = AdapterProducts(mFirebase, firestoreRecyclerOptions)
        mAdapter.startListening()
        recyclerView.adapter = mAdapter
    }


    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }

    public override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        mAdapter.startListening()
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

    // Método llamado al seleccionar un elemento del menú de opciones
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}