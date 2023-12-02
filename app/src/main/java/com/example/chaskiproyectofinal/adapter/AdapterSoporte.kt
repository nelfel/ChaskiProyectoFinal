package com.example.chaskiproyectofinal.adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaskiproyectofinal.EnvioActivity
import com.example.chaskiproyectofinal.ListProductsActivity
import com.example.chaskiproyectofinal.ListRepartidoresActivity
import com.example.chaskiproyectofinal.R
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.example.chaskiproyectofinal.model.SoporteModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

//Ver info en AdapterPedidos es lo mismo, lo unico que cambia es cuano se hace clic sobre un elemento
//Cuando se hace clic sobre un elemento es igual a pedidos se muestran dialog y se modifica el estado en
// la bd depenndiendo los permisos del usuaruio osea si es restaurante o si es un usuario normale
//AdapterRestaurantes se utiliza en PrincipalActivity
class AdapterSoporte(
    private val db: FirebaseFirestore,
    options: FirestoreRecyclerOptions<SoporteModel>
) : FirestoreRecyclerAdapter<SoporteModel, AdapterSoporte.SoporteViewHolder>(options) {

    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private lateinit var mFirebase: FirebaseFirestore

    class SoporteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNum: TextView = itemView.findViewById(R.id.tvNum)
        val textViewFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val textViewEstado: TextView = itemView.findViewById(R.id.tvEstadoT)
        val textViewAsunto: TextView = itemView.findViewById(R.id.tvAsunto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoporteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_soporte, parent, false)
        return SoporteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SoporteViewHolder, position: Int, model: SoporteModel) {
        val idRestaurante = model.idRestaurante
        // Configurar los elementos de la vista con los datos del modelo
        holder.textViewNum.text = model.num.toString()
        holder.textViewFecha.text = model.fecha
        holder.textViewEstado.text = model.estado
        holder.textViewAsunto.text = model.asunto

        val context = holder.itemView.context


        holder.itemView.setOnClickListener {
            fun tieneRolRestaurante(uid: String): Boolean {
                mFirebase = FirebaseFirestore.getInstance()
                val usuariosCollection = mFirebase.collection("usuarios")
                val restaurantesCollection = mFirebase.collection("restaurantes")

                return runBlocking {
                    try {
                        // Verificar si el uid existe en la colección "usuarios"
                        val usuarioDocument = usuariosCollection.document(uid).get().await()

                        // Si el documento del usuario normal existe, entonces no tiene el rol de restaurante
                        if (usuarioDocument.exists()) {
                            return@runBlocking false
                        }

                        // Si el documento del usuario normal no existe, verificar si existe en la colección "restaurantes"
                        val restauranteDocument = restaurantesCollection.document(uid).get().await()

                        // Devolver true si el documento del restaurante existe
                        return@runBlocking restauranteDocument.exists()
                    } catch (e: Exception) {
                        // Manejar la excepción si ocurre algún error al consultar la base de datos
                        Log.e("ListPedidosActivity", "Error al verificar el rol de restaurante", e)
                        return@runBlocking false
                    }
                }
            }

            if (tieneRolRestaurante(uid)) {
                if(model.estado == "En Proceso"){
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Caso")
                    builder.setMessage("El caso ya esta siendo resuelto")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                        // Actualizar el estado en la base de datos
                        val db = FirebaseFirestore.getInstance()
                        model.soporteId?.let { it1 ->
                            db.collection("soporte").document(it1)
                                .update("estado", "Finalizado")
                                .addOnSuccessListener {
                                }
                                .addOnFailureListener { e ->
                                    // Manejar el error al actualizar en la base de datos
                                    Toast.makeText(context, "Error al actualizar el estado en la base de datos", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    builder.show()


                }else if(model.estado == "Pendiente"){
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Caso")
                    builder.setMessage("Deseas pasar este caso a Administracion")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                        // Actualizar el estado en la base de datos
                        val db = FirebaseFirestore.getInstance()
                        model.soporteId?.let { it1 ->
                            db.collection("soporte").document(it1)
                                .update("estado", "En Proceso")
                                .addOnSuccessListener {
                                }
                                .addOnFailureListener { e ->
                                    // Manejar el error al actualizar en la base de datos
                                    Toast.makeText(context, "Error al actualizar el estado en la base de datos", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    // Mostrar el diálogo
                    builder.show()

                }else{
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Caso")
                    builder.setMessage("El caso ya fue resuelto")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->

                    }
                    // Mostrar el diálogo
                    builder.show()

                }
            } else {
                if(model.estado == "En Proceso"){
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Caso")
                    builder.setMessage("Tu caso esta siendo resuelto por Administracion")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->

                        }
                    // Mostrar el diálogo
                    builder.show()
                }else if(model.estado == "Pendiente"){
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Caso")
                    builder.setMessage("El caso sera enviado a Administracion. Espere una respuesta.")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->

                        }
                    // Mostrar el diálogo
                    builder.show()
                }else {
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Caso Finalizado")
                    builder.setMessage("El caso ya fue resuelto. Gracias por su preferencia.")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                    }
                    // Mostrar el diálogo
                    builder.show()

                }
            }


        }


    }
}
