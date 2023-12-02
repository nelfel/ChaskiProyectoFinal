package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.chaskiproyectofinal.model.CalificacionRepartidorModel
import com.example.chaskiproyectofinal.model.UserModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
/**
 * `RegisterActivity` es una actividad de Android que permite a los usuarios registrarse en la aplicación.
 * Captura información como nombre, apellidos, dirección, año de nacimiento, correo electrónico y contraseña.
 * Realiza la autenticación del usuario utilizando Firebase Authentication y almacena los detalles del usuario
 * en Firebase Firestore.
 *
 * @property auth Instancia de FirebaseAuth para gestionar la autenticación del usuario.
 * @property db Instancia de FirebaseFirestore para interactuar con la base de datos Firestore.
 */
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inicialización de componentes de la interfaz de usuario y obtención de instancias de Firebase.
        val etNombreR: EditText = findViewById(R.id.etNombreR)
        val etApellidoR: EditText = findViewById(R.id.etApellidoR)
        val etAnionacimientoR: EditText = findViewById(R.id.etAnionacimientoR)
        val etCorreoR: EditText = findViewById(R.id.etCorreoR)
        val etDireccionR: EditText = findViewById(R.id.etDireccionR)
        val etContraseniaR: EditText = findViewById(R.id.etContraseniaR)
        val btnSaveRegister: Button = findViewById(R.id.btnRegistrar)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("usuarios")

        // Configuración del botón para el registro de usuarios.
        btnSaveRegister.setOnClickListener {
            if (etNombreR.text.isNotEmpty() &&
                etDireccionR.text.isNotEmpty() &&
                etAnionacimientoR.text.isNotEmpty() &&
                etCorreoR.text.isNotEmpty() &&
                etContraseniaR.text.isNotEmpty() &&
                etDireccionR.text.isNotEmpty()) {

                // Obtención de valores ingresados por el usuario.
                val nombres = etNombreR.text.toString()
                val apellidos = etApellidoR.text.toString()
                val direccion = etDireccionR.text.toString()
                val anionacimiento = etAnionacimientoR.text.toString()
                val correo = etCorreoR.text.toString()
                val contrasenia = etContraseniaR.text.toString()
                var calificaciones = emptyList<CalificacionRepartidorModel>()
                val tipo = "usuario"

                // Creación del usuario en Firebase Authentication.
                auth.createUserWithEmailAndPassword(correo, contrasenia)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Se registró en Firebase Authentication y
                            // ahora registramos en Firebase Firestore

                            var user: FirebaseUser? = auth.currentUser
                            var uid = user?.uid
                            var userModel =
                                UserModel(
                                    nombres,
                                    apellidos,
                                    correo,
                                    contrasenia,
                                    anionacimiento,
                                    direccion,
                                    calificaciones,
                                    tipo
                                )

                            // Establecer manualmente el UID como el ID del documento en Firestore
                            collectionRef.document(uid.toString()).set(userModel)
                                .addOnCompleteListener {
                                    Snackbar
                                        .make(
                                            findViewById(android.R.id.content),
                                            "Registro exitoso",
                                            Snackbar.ANIMATION_MODE_FADE
                                        ).show()

                                    //Limpiar los datos ingresados
                                    etApellidoR.setText("")
                                    etNombreR.setText("")
                                    etAnionacimientoR.setText("")
                                    etCorreoR.setText("")
                                    etContraseniaR.setText("")

                                    // Redirección a ConfirmacionActivity
                                    val intent = Intent(this, ConfirmacionActivity::class.java)
                                    startActivity(intent)

                                }.addOnFailureListener {
                                    Snackbar
                                        .make(
                                            findViewById(android.R.id.content),
                                            "No se pudo completar la transacción",
                                            Snackbar.LENGTH_LONG
                                        ).show()
                                }
                        } else {
                            Snackbar
                                .make(
                                    findViewById(android.R.id.content),
                                    "Ocurrió un error al registrarse: ${task.exception?.message}",
                                    Snackbar.LENGTH_LONG
                                ).show()
                        }
                    }



            } else {
                // Muestra un mensaje de error o realiza alguna acción si algún campo está vacío
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }

        }

        //linkIngresa
        val etIngresa: TextView = findViewById(R.id.etIngresa)

        etIngresa.setOnClickListener {
            // Intent para dirigirte a LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)


        }
    }
}