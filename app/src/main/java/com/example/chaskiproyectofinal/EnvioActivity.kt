package com.example.chaskiproyectofinal

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.chaskiproyectofinal.dialog.CustomDialogComida
import com.example.chaskiproyectofinal.dialog.CustomDialogFragment
import com.google.firebase.auth.FirebaseAuth
/**
 * Actividad de envío que simula el progreso de un pedido con una barra de progreso.
 * Muestra un diálogo personalizado (CustomDialogFragment) al completar la simulación de progreso.
 * Implementa las interfaces de los diálogos personalizados (CustomDialogFragment.DialogListener
 * y CustomDialogComida.DialogListener) para manejar eventos de los botones del diálogo.
 */
class EnvioActivity : AppCompatActivity(), CustomDialogFragment.DialogListener, CustomDialogComida.DialogListener  {
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_envio)
        progressBar = findViewById(R.id.progressBar)

        // Configura la duración total en milisegundos
        val totalDuration = 15000L

        // Configura la duración para cada segmento (ejemplo 0%, 50%, 100%)
        val segmentDuration = totalDuration / 3

        // Actualiza el progreso cada segundo
        val updateInterval = 1000L

        // Configura el progreso inicial
        progressBar.progress = 0

        // Simula el progreso durante el tiempo especificado
        simulateProgress(segmentDuration, updateInterval)

    }

    // Método para simular el progreso de la barra de progreso
    private fun simulateProgress(duration: Long, interval: Long) {
        val handler = Handler(Looper.getMainLooper())
        val maxProgress = progressBar.max

        handler.postDelayed(object : Runnable {
            var elapsedTime = 0L

            override fun run() {
                // Actualiza el progreso cada segundo
                elapsedTime += interval
                progressBar.progress = ((elapsedTime.toFloat() / duration) * maxProgress).toInt()

                if (elapsedTime < duration) {
                    handler.postDelayed(this, interval)
                } else {
                    // La animación ha terminado, muestra el diálogo
                    showCustomDialog()
                }
            }
        }, interval)
    }

    // Método para mostrar el diálogo personalizado al completar la simulación de progreso
    private fun showCustomDialog() {
        val dialog = CustomDialogFragment()
        dialog.setDialogListener(this)
        dialog.show(supportFragmentManager, "CustomDialogFragment")
    }

    // Método para mostrar el diálogo personalizado de comida
    private fun showCustomDialogComida(context: Context) {
        val dialog = CustomDialogComida()
        dialog.setDialogListener(object : CustomDialogComida.DialogListener {
            override fun onEnviarClicked(rating: Float, comment: String) {
                // Crear una intención para ir a la MainActivity
                val intent = Intent(context, PrincipalActivity::class.java)

                // Agregar banderas para limpiar la pila de actividades y comenzar una nueva instancia de MainActivity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                // Iniciar la actividad
                startActivity(intent)

                // Cerrar la actividad actual (EnvioActivity) si no deseas volver a ella
                finish()

            }

            override fun onOmitirClicked() {
                // Crear una intención para ir a la MainActivity
                val intent = Intent(context, PrincipalActivity::class.java)

                // Agregar banderas para limpiar la pila de actividades y comenzar una nueva instancia de MainActivity
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                // Iniciar la actividad
                startActivity(intent)

                // Cerrar la actividad actual (EnvioActivity) si no deseas volver a ella
                finish()
            }
        })
        dialog.show(supportFragmentManager, "CustomDialogComida")
    }

    // Implementación del método de la interfaz CustomDialogFragment.DialogListener
    override fun onEnviarClicked(rating: Float, comment: String) {
        val message = "¡Tus datos fueron enviados!\nRating: $rating\nComentario: $comment"
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        showCustomDialogComida(this)

    }

    override fun onOmitirClicked() {
        val intent = Intent(this, PrincipalActivity::class.java)

        // Agregar banderas para limpiar la pila de actividades y comenzar una nueva instancia de MainActivity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Iniciar la actividad
        startActivity(intent)

        // Cerrar la actividad actual (EnvioActivity) si no deseas volver a ella
        finish()
    }

    // Método llamado al iniciar la actividad, verificar usuario conectado
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