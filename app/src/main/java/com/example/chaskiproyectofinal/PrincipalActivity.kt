package com.example.chaskiproyectofinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaskiproyectofinal.adapter.AdapterRestaurantes
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

/**
 * `PrincipalActivity` es una actividad de Android que muestra una lista de restaurantes y permite a los usuarios
 * buscar entre ellos. Utiliza un RecyclerView para mostrar la información de los restaurantes y ofrece un campo
 * de búsqueda para filtrar la lista según el texto ingresado por el usuario.
 *
 * @property toggle Instancia de ActionBarDrawerToggle para gestionar la interfaz del panel de navegación lateral.
 * @property mFirebase Instancia de FirebaseFirestore para interactuar con la base de datos Firestore.
 * @property recyclerView Instancia de RecyclerView para mostrar la lista de restaurantes.
 * @property mAdapter Adaptador para gestionar los datos y la interfaz de usuario del RecyclerView.
 * @property auth Instancia de FirebaseAuth para gestionar la autenticación del usuario.
 * @property search_view Componente SearchView para permitir a los usuarios buscar restaurantes.
 *
 * Se usa un adapter lleva el mismo estilo de ListProductActivity
 * Tambien el metodo de buscar es similar a ListProductActivity
 */
class PrincipalActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mFirebase: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: AdapterRestaurantes
    private lateinit var auth: FirebaseAuth
    var search_view: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        auth = Firebase.auth
        setContentView(R.layout.activity_principal)
        recyclerView = findViewById(R.id.rvRestaurant)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        mFirebase = FirebaseFirestore.getInstance()
        search_view = findViewById(R.id.search)
        val query: Query = mFirebase.collection("restaurantes")
        Log.d("Restaurantes", query.toString())
        val options: FirestoreRecyclerOptions<RestaurantModel> = FirestoreRecyclerOptions.Builder<RestaurantModel>()
            .setQuery(query, RestaurantModel::class.java)
            .build()
        Log.d("Option Restaurantes", options.toString())
        mAdapter = AdapterRestaurantes(mFirebase,options)
        Log.d("mAdapter Restaurantes", mAdapter.toString())
        recyclerView.adapter = mAdapter
        search_view()
        val mAuth = FirebaseAuth.getInstance()

        //Implementar el panel de navegacion lateral
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayoutRoot)
        val navView = findViewById<NavigationView>(R.id.navView)

        toggle = ActionBarDrawerToggle(this@PrincipalActivity, drawerLayout, R.string.open, R.string.close)
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
    //Recordar busqueda se debe hacer tal cual esta ingresada Primer letra mayuscula resto en minuscula
    fun textSearch(s: String) {
        mFirebase = FirebaseFirestore.getInstance()
        val query: Query = mFirebase.collection("restaurantes")
        // Convertir el texto de búsqueda a minúsculas
        val firestoreRecyclerOptions: FirestoreRecyclerOptions<RestaurantModel> =
            FirestoreRecyclerOptions.Builder<RestaurantModel>()
                .setQuery(
                    query.orderBy("nombre")
                        .startAt(s).endAt("$s~"), RestaurantModel::class.java
                ).build()
        mAdapter = AdapterRestaurantes(mFirebase, firestoreRecyclerOptions)
        mAdapter.startListening()
        recyclerView.adapter = mAdapter
    }



    public override fun onStart() {
        super.onStart()
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
    override fun onStop() {
        super.onStop()
        mAdapter.stopListening()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}








