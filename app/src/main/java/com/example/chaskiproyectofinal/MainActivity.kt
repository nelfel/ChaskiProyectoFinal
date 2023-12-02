package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Handler
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Esta activity lo manda al login despues de unos segundo
        // Agrega un retraso de 2000 milisegundos (2 segundos)
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Cierra la actividad actual para que no puedas volver atrás
        }, 2000)

        val db = FirebaseFirestore.getInstance()


    }
}