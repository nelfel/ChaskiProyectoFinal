package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chaskiproyectofinal.model.ProductModel
import com.google.firebase.auth.FirebaseAuth

/**
 * `PagoOnlineActivity` es una actividad de Android que permite a los usuarios realizar pagos en línea.
 * Esta actividad recibe información sobre el total a pagar, la lista de productos seleccionados,
 * y el identificador del restaurante asociado. Los usuarios ingresan los detalles de su tarjeta de
 * crédito y, al hacer clic en el botón "Siguiente", son dirigidos a la actividad `ConfirmarPagoActivity`
 * para finalizar la transacción.
 *
 * @property auth Instancia de FirebaseAuth para gestionar la autenticación del usuario.
 */
class PagoOnlineActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pago_online)
        val btnSiguiente = findViewById<Button>(R.id.btnSiguiente)

        // Inicialización de componentes de la interfaz de usuario y obtención de datos de la Activity Anterior
        val sumaTotal = intent.getDoubleExtra("sumaTotal", 0.0)
        val listaProduc = intent.getSerializableExtra("listaProduc") as? ArrayList<ProductModel>
        val listaProductosValida = listaProduc ?: ArrayList()
        val listaProductosValidaSinNull = listaProductosValida ?: emptyList()
        val restaurantId = intent.getStringExtra("restaurantId")

        //Cuando alguien haga clic en siguiente
        btnSiguiente.setOnClickListener {
            Log.d("Lista de Productos desde PagoOnline listaProduc", listaProduc.toString())
            Log.d("Lista de Productos desde PagoOnline listaProductos", listaProductosValida.toString())

            // Obtén el número de tarjeta
            Log.d("Lista de Productos desde PagoOnline listaProductosValida", listaProductosValidaSinNull.toString())

            // Obtención de datos ingresados por el usuario.
            val etNumeroTar: EditText = findViewById(R.id.etNumeroTar)
            val etVencimiento: EditText = findViewById(R.id.etVencimiento)
            val etCvv: EditText = findViewById(R.id.etCvv)
            val etNombre: EditText = findViewById(R.id.etNombreTar)
            val numeroTarjeta = etNumeroTar.text.toString()
            val nombre = etNombre.text.toString()
            val vencimiento = etVencimiento.text.toString()
            val cvv = etCvv.text.toString()

            // Validar que no se envíen campos vacíos
            if (nombre.isBlank() || numeroTarjeta.isBlank() || vencimiento.isBlank() || cvv.isBlank()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (numeroTarjeta.length != 12) {
                etNumeroTar.error = "El Numero de Tarjeta debe tener 12 dígitos sin espacios"
                return@setOnClickListener
            }
            if (cvv.length != 3) {
                etCvv.error = "El CVV debe tener 3 dígitos"
                return@setOnClickListener
            }
            // Crea un Intent para la siguiente actividad y tambien mandar estos datos a la siguiente activity
            val intent = Intent(this, ConfirmarPagoActivity::class.java)
            // Agrega el número de tarjeta como extra al Intent
            intent.putExtra("numeroTarjeta", numeroTarjeta)
            intent.putExtra("sumaTotal", sumaTotal)
            intent.putExtra("listaProduc", ArrayList(listaProductosValidaSinNull))
            intent.putExtra("restaurantId", restaurantId)
            // Inicia la siguiente actividad
            startActivity(intent)
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
            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
            finish()
        }
    }
}