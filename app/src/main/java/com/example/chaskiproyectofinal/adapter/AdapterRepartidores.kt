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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaskiproyectofinal.EnvioActivity
import com.example.chaskiproyectofinal.ListPedidosActivity
import com.example.chaskiproyectofinal.ListProductsActivity
import com.example.chaskiproyectofinal.R
import com.example.chaskiproyectofinal.model.RepartidorModel
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

//Ver info en AdapterPedidos es lo mismo, lo unico que cambia es cuano se hace clic sobre un elemento
//AdapterRepartidores se utiliza en ListRepartidoresActivity
class AdapterRepartidores(
    private val db: FirebaseFirestore,
    options: FirestoreRecyclerOptions<RepartidorModel>
) : FirestoreRecyclerAdapter<RepartidorModel, AdapterRepartidores.RepartidorViewHolder>(options) {

    // Clase interna para representar la vista de cada elemento en el RecyclerView
    class RepartidorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgFoto: ImageView = itemView.findViewById(R.id.imgRepartidor)
        val txtNombre: TextView = itemView.findViewById(R.id.txtRepartidor)
        val ratingBarRepartidor: RatingBar = itemView.findViewById(R.id.rbarRepartidor)
    }

    // Función llamada para crear la vista del elemento en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepartidorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_repartidores, parent, false)
        return RepartidorViewHolder(itemView)
    }

    // Función llamada para asociar los datos a la vista del elemento en el RecyclerView
    override fun onBindViewHolder(holder: RepartidorViewHolder, position: Int, model: RepartidorModel) {
        // Cargar la imagen del repartidor utilizando Glide
        Glide.with(holder.itemView)
            .load(model.urlFoto)
            .into(holder.imgFoto)

        // Asignar el nombre del repartidor a la vista correspondiente
        holder.txtNombre.text = model.nombre

        // Asignar la calificación del repartidor a la barra de calificación
        holder.ratingBarRepartidor.rating = model.rating.toFloat()

        val context = holder.itemView.context

        // Establecer el listener de clic en el elemento de la vista
        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)

            // Establecer el título y el mensaje del diálogo
            builder.setTitle("Repartidor Seleccionado")
            builder.setMessage("Listo, has seleccionado un repartidor.")

            // Establecer un botón de aceptar
            builder.setPositiveButton("Aceptar") { dialog, which ->
                // Puedes realizar acciones adicionales después de seleccionar un repartidor
                // Por ejemplo, iniciar una actividad relacionada con los pedidos
                val intent = Intent(context, ListPedidosActivity::class.java)
                context.startActivity(intent)
            }

            // Mostrar el diálogo
            builder.show()
        }
    }
}
