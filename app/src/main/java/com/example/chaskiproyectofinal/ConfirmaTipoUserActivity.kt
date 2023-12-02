package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
/**
 * Actividad de confirmación del tipo de usuario que se muestra al usuario al registrarse.
 * Permite al usuario seleccionar entre registrarse como restaurante o como usuario normal.
 * El botón de continuar lleva al usuario a la pantalla de registro correspondiente.
 */
class ConfirmaTipoUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirma_tipo_user)
        val rgOpcionesUser = findViewById<RadioGroup>(R.id.rgOpcionesUser)
        val btnContinuar = findViewById<Button>(R.id.btnContinuar)

        btnContinuar.setOnClickListener {
            // Obtener el tipo de usuario seleccionado
            val tipoUsuario = rgOpcionesUser.checkedRadioButtonId

            // Verificar si el usuario ha seleccionado una opción
            if (tipoUsuario == -1) {
                // El usuario no ha seleccionado ninguna opción
                Toast.makeText(this, "Por favor, seleccione un tipo de usuario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Si el usuario ha seleccionado una opción, enviar al usuario a la pantalla correspondiente
            if (tipoUsuario == R.id.rbRestaurante) {
                // Ir a la pantalla de registro de restaurante
                val intent = Intent(this, RegisterRestaurantActivity::class.java)
                startActivity(intent)
            } else {
                // Ir a la pantalla de registro de usuario
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val currentUser = auth.currentUser
            val currentUserId = currentUser?.uid
        }else{

        }
    }
}