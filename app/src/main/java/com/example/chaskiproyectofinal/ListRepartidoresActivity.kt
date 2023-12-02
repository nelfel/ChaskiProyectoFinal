package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaskiproyectofinal.adapter.AdapterRepartidores
import com.example.chaskiproyectofinal.adapter.AdapterRestaurantes
import com.example.chaskiproyectofinal.model.RepartidorModel
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
/**
 * Actividad que muestra la lista de repartidores disponibles.
 * Permite al administrador del sistema ver y gestionar la lista de repartidores.
 * Los adapter funcionan igual puede ver en ListProductActivity
 */
class ListRepartidoresActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mFirebase: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: AdapterRepartidores
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_repartidores)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        auth = Firebase.auth
        recyclerView = findViewById(R.id.rvRepartidores)
        recyclerView.layoutManager = GridLayoutManager(this, 1)
        mFirebase = FirebaseFirestore.getInstance()
        val query: Query = mFirebase.collection("repartidores")
        val options: FirestoreRecyclerOptions<RepartidorModel> = FirestoreRecyclerOptions.Builder<RepartidorModel>()
            .setQuery(query, RepartidorModel::class.java)
            .build()
        mAdapter = AdapterRepartidores(mFirebase,options)
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