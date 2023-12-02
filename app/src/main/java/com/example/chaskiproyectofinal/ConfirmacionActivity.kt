package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
/**
 * Actividad de confirmación que se muestra al usuario después de realizar una orden.
 * Proporciona un botón para regresar a la pantalla principal (LoginActivity) y realiza
 * la verificación de la autenticación del usuario al iniciarse. Si el usuario no está autenticado,
 * redirige a la pantalla de inicio de sesión.
 */
class ConfirmacionActivity : AppCompatActivity() {
    // Instancia de FirebaseAuth para la autenticación
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion)

        // Obtener referencia al botón de ordenar o siguiente en las vistas
        val btnOrdena: Button = findViewById(R.id.btnOrdena)

        // Configurar el evento de clic del botón para regresar a la pantalla principal (LoginActivity)
        btnOrdena.setOnClickListener {
            // Intent para dirigirte a PrincipalActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
    //On star
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