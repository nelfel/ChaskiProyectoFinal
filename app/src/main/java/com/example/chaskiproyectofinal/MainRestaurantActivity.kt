package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
/**
 * Actividad principal para la gestión de un restaurante.
 * Permite la navegación a través del panel lateral y contiene acciones específicas para los restaurantes.
 */
class MainRestaurantActivity : AppCompatActivity() {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_restaurant)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        //Implementar el panel de navegacion lateral
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayoutRoot)
        val navView = findViewById<NavigationView>(R.id.navView)
        auth = Firebase.auth

        toggle = ActionBarDrawerToggle(this@MainRestaurantActivity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        navView.itemIconTintList = null
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navPrincipal -> {
                    val intent = Intent(this, MainRestaurantActivity::class.java)
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

    public override fun onStart() {
        super.onStart()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}