package com.example.chaskiproyectofinal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
/**
 * `RegisterRestaurantActivity` es una actividad de Android que permite el registro de restaurantes en la aplicación.
 * Captura información como nombre, dirección, teléfono, correo electrónico, contraseña, URL de la imagen del restaurante,
 * tiempo de demora en la entrega y otros detalles. Realiza la autenticación del restaurante utilizando Firebase Authentication
 * y almacena los detalles del restaurante en Firebase Firestore.
 *
 * Funciona igual que registerActivity solo que esta almacena en restaurante
 * @property auth Instancia de FirebaseAuth para gestionar la autenticación del restaurante.
 * @property db Instancia de FirebaseFirestore para interactuar con la base de datos Firestore.
 */
class RegisterRestaurantActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_restaurant)


        val etNombreR: EditText = findViewById(R.id.etNombreR)
        val etDireccionR: EditText = findViewById(R.id.etDireccionR)
        val etTelefonoR: EditText = findViewById(R.id.etTelefonoR)
        val etCorreoR: EditText = findViewById(R.id.etCorreoR)
        val etContraseniaR: EditText = findViewById(R.id.etContraseniaR)
        val etUrlImagenRe: EditText = findViewById(R.id.etUrlRestaR)
        val etTiempoR: EditText = findViewById(R.id.etTiempoR)

        val btnSaveRegister: Button = findViewById(R.id.btnRegistrar)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val collectionRef = db.collection("restaurantes")

        btnSaveRegister.setOnClickListener {
            if (etNombreR.text.isNotEmpty() &&
                etDireccionR.text.isNotEmpty() &&
                etTelefonoR.text.isNotEmpty() &&
                etCorreoR.text.isNotEmpty() &&
                etContraseniaR.text.isNotEmpty() &&
                etUrlImagenRe.text.isNotEmpty() &&
                etTiempoR.text.isNotEmpty()) {

                val nombres = etNombreR.text.toString()
                val direccion = etDireccionR.text.toString()
                val telefono = etTelefonoR.text.toString()
                val correo = etCorreoR.text.toString()
                val contrasenia = etContraseniaR.text.toString()
                val urlImagenRestaurant = etUrlImagenRe.text.toString()
                val urlComidaRest = urlImagenRestaurant
                val tiempoDeDemora = etTiempoR.text.toString()
                val rating = 4.0

                auth.createUserWithEmailAndPassword(correo, contrasenia)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            //Se registró en Firebase Authentication y
                            // ahora registramos en Firebase Firestore
                            var user: FirebaseUser? = auth.currentUser
                            var uid = user?.uid
                            var RestaurantModel =
                                RestaurantModel(
                                    nombres,
                                    direccion,
                                    telefono,
                                    correo,
                                    contrasenia,
                                    urlImagenRestaurant,
                                    urlComidaRest,
                                    tiempoDeDemora,
                                    rating
                                )
                            collectionRef.document(uid.toString()).set(RestaurantModel)
                                .addOnCompleteListener {
                                    Snackbar
                                        .make(
                                            findViewById(android.R.id.content),
                                            "Registro exitoso",
                                            Snackbar.ANIMATION_MODE_FADE
                                        ).show()
                                    etDireccionR.setText("")
                                    etNombreR.setText("")
                                    etTelefonoR.setText("")
                                    etCorreoR.setText("")
                                    etContraseniaR.setText("")
                                    etUrlImagenRe.setText("")
                                    etTiempoR.setText("")

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