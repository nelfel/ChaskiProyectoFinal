package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
/**
 * Actividad responsable de gestionar el inicio de sesión de los usuarios.
 * Permite a los usuarios autenticarse y redirige según el tipo de usuario.
 */
class LoginActivity : AppCompatActivity() {
    // Instancias necesarias para interactuar con Firebase
    val db = FirebaseFirestore.getInstance()
    var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Referencias a los elementos de la interfaz de usuario
        val txtCorreoLogin: EditText = findViewById(R.id.txtCorreoLogin)
        val txtContraseniaLogin: EditText = findViewById(R.id.txtContraseniaLogin)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)
        val btnRegistrarse: Button = findViewById(R.id.btnRegistrarse)

        // Configuración del botón para iniciar sesión
        btnIngresar.setOnClickListener {
            val correo = txtCorreoLogin.text.toString()
            val clave = txtContraseniaLogin.text.toString()
            // Validar que se haya ingresado un correo
            if (correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                txtCorreoLogin.error = "Ingrese un correo electrónico válido"
                return@setOnClickListener
            }
            // Validar que se haya ingresado una contraseña
            if (clave.isBlank()) {
                txtContraseniaLogin.error = "Ingrese una contraseña"
                return@setOnClickListener
            }
            //Llama a unna funcion que hicimos abajo
            authenticateUser(correo, clave)
        }

        //si se ehace clic en registrar te manda a otra activity
        btnRegistrarse.setOnClickListener {
            startActivity(Intent(this, ConfirmaTipoUserActivity::class.java))
        }

    }

    // Método para autenticar al usuario con Firebase Authentication
    private fun authenticateUser(correo: String, clave: String) {
        auth.signInWithEmailAndPassword(correo, clave)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User identificado correctamente llamamos a la funcion
                    // para verificar el tipo de usuario
                    checkUserTypeAndRedirect()
                } else {
                    // Authentication failed
                    Snackbar
                        .make(findViewById(android.R.id.content),
                            "Credenciales inválidas",
                            Snackbar.LENGTH_LONG).show()
                }
            }
    }

    // Método para verificar el tipo de usuario y redirigir a la pantalla correspondiente
    private fun checkUserTypeAndRedirect() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        if (currentUserUid != null) {
            Log.d("Firestore", "UID del usuario actual: $currentUserUid")

            // Colecciones en Firestore
            val userCollection = FirebaseFirestore.getInstance().collection("usuarios")
            val restaurantCollection = FirebaseFirestore.getInstance().collection("restaurantes")

            // Obtener el documento del usuario en la colección de usuarios
            userCollection.document(currentUserUid).get()
                .addOnSuccessListener { userDocument ->
                    if (userDocument.exists()) {
                        Log.d("Firestore", "Usuario encontrado en la colección de usuarios.")
                        val intent = Intent(this, PrincipalActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("Firestore", "Usuario NO encontrado en la colección de usuarios. Verificando en la colección de restaurantes.")
                        // Usuario no encontrado en la colección de usuarios
                        // Verificar en la colección de restaurantes
                        restaurantCollection.document(currentUserUid).get()
                            .addOnSuccessListener { restaurantDocument ->
                                if (restaurantDocument.exists()) {
                                    Log.d("Firestore", "Usuario encontrado en la colección de restaurantes.")
                                    // Resto del código para redirigir a la pantalla de restaurante...
                                    val intent = Intent(this, MainRestaurantActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Log.d("Firestore", "Usuario NO encontrado en la colección de restaurantes.")
                                    // Manejar según sea necesario...
                                }
                            }
                            .addOnFailureListener { restaurantException ->
                                Log.e("Firestore", "Error al obtener el documento de restaurante: $restaurantException")
                            }
                    }
                }
                .addOnFailureListener { userException ->
                    Log.e("Firestore", "Error al obtener el documento de usuario: $userException")
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
            checkUserTypeAndRedirect()
        }else{

        }
    }

}