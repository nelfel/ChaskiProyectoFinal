package com.example.chaskiproyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chaskiproyectofinal.adapter.AdapterCarritoProducts
import com.example.chaskiproyectofinal.model.ProductModel
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
/**
 * Actividad que representa el carrito de compras del usuario. Muestra los productos seleccionados,
 * permite visualizar el total de la compra y proporciona la opción de finalizar la compra.
 * Utiliza un RecyclerView y un adaptador personalizado para mostrar la lista de productos en el carrito.
 * Además, realiza la autenticación del usuario al iniciar la actividad y redirige al usuario al
 * inicio de sesión si no está autenticado.
 */
class CarritoActivity : AppCompatActivity() {
    // Declaración de la instancia de FirebaseAuth para la autenticación
    private lateinit var auth: FirebaseAuth
    // Método llamado al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Obtener referencias a las vistas y datos pasados desde la actividad anterior(ListProductActivity)
        val rvProductos = findViewById<RecyclerView>(R.id.rcvBolsaCompras)
        val restaurantId = intent.getStringExtra("restaurantId")
        val productListJson = intent.getStringExtra("listaProductos")

        // Deserializar la lista de productos utilizando Gson
        val gson = Gson()
        val type = object : TypeToken<List<ProductModel>>() {}.type
        val productList: List<ProductModel> = gson.fromJson(productListJson, type)
        //Log.d("Lista de productos que recibio", productList.toString())
        //Log.d("Lista de productos", listaProductos.toString())
        val adapter = AdapterCarritoProducts(this, productList)

        // Crear y asignar un Adapter para el RecyclerView
        rvProductos.adapter = adapter
        rvProductos.layoutManager = LinearLayoutManager(this)
        // Obtener referencias a otras vistas y configurar el botón para finalizar la compra
        val btnFinalizarCompra = findViewById<Button>(R.id.btnFinalizarCompra)

        // Obtener la lista de totales y la lista de productos del adapter
        val listaTotales = adapter.getListaTotales()
        val listaProduc: List<ProductModel> = adapter.obtenerListaProductos()
        Log.d("Lista Productos", listaProduc.toString())

        // Configurar el evento de clic del botón "Finalizar Compra"
        btnFinalizarCompra.setOnClickListener{
            // Calcular la suma total de los productos en el carrito
            val sumaTotal = listaTotales.fold(0.0) { acc, valor -> acc + valor }
            // Verificar si la suma total es mayor que cero
            if (sumaTotal > 0.0) {
                // Iniciar la actividad de pago en línea y pasar la información necesaria
                val intent = Intent(this, PagoOnlineActivity::class.java)
                intent.putExtra("sumaTotal", sumaTotal)
                intent.putExtra("listaProduc", ArrayList(listaProduc))
                intent.putExtra("restaurantId", restaurantId)
                this.startActivity(intent)
            } else {
                // Muestra un mensaje o toma alguna acción si la suma total es cero
                Toast.makeText(this, "El carrito está vacío. Agrega productos antes de continuar.", Toast.LENGTH_SHORT).show()
            }
        }


            // Calcular el total multiplicando la cantidad por el precio

        }


    //metodo onStar, verifica que hay un usuario autentificado si no esta autentificado lo manda al login
    //Uno personaliza el on start
    public override fun onStart() {
        super.onStart()
        // Inicializar la instancia de FirebaseAuth
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
