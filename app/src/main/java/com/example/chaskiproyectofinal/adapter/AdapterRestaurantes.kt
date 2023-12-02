package com.example.chaskiproyectofinal.adapter

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
import com.example.chaskiproyectofinal.ListProductsActivity
import com.example.chaskiproyectofinal.R
import com.example.chaskiproyectofinal.model.RestaurantModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

//Ver info en AdapterPedidos es lo mismo, lo unico que cambia es cuano se hace clic sobre un elemento
//Aqui cuando alguien hace clic sobre un restaurante lo manda a la lista de productos de ese restaurante
//AdapterRestaurantes se utiliza en PrincipalActivity

class AdapterRestaurantes(
    private val db: FirebaseFirestore,
    options: FirestoreRecyclerOptions<RestaurantModel>
) : FirestoreRecyclerAdapter<RestaurantModel, AdapterRestaurantes.RestauranteViewHolder>(options) {

    // Lista para almacenar los restaurantes filtrados
    private var restaurantesFiltrados: List<RestaurantModel> = snapshots.toList()

    // Clase ViewHolder para hacer referencia a los componentes de la interfaz de usuario
    class RestauranteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewRestaurante: ImageView = itemView.findViewById(R.id.imgLogoR)
        val imageComidaRestaurante: ImageView = itemView.findViewById(R.id.imgCom)
        val textViewNombreRestaurante: TextView = itemView.findViewById(R.id.tvNomrest)
        val textViewTiempoDemora: TextView = itemView.findViewById(R.id.tvTiempo)
        val ratingBarRestaurante: RatingBar = itemView.findViewById(R.id.rbarRest)
    }

    // Crear el diseño para un solo elemento de restaurante y devuelve una instancia de RestauranteViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestauranteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurante, parent, false)
        return RestauranteViewHolder(itemView)
    }

    // Vincula los datos a los componentes de interfaz de usuario de ViewHolder
    override fun onBindViewHolder(holder: RestauranteViewHolder, position: Int, model: RestaurantModel) {
        // Carga la imagen del restaurante utilizando Glide
        Glide.with(holder.itemView)
            .load(model.urlImagenRestaurant)
            .into(holder.imageViewRestaurante)

        Glide.with(holder.itemView)
            .load(model.urlComidaRest)
            .into(holder.imageComidaRestaurante)

        holder.textViewNombreRestaurante.text = model.nombre

        holder.textViewTiempoDemora.text = model.tiempoDeDemora

        holder.ratingBarRestaurante.rating = model.rating.toFloat()

        val context = holder.itemView.context

        // Establece el listener de clic en el elemento de la vista
        holder.itemView.setOnClickListener {
            val restaurantId = snapshots.getSnapshot(position).id

            Log.d("ID del Restaurante", restaurantId)

            // Crea un intent para la otra actividad
            val intent = Intent(context, ListProductsActivity::class.java)
            intent.putExtra("restaurantId", restaurantId)
            context.startActivity(intent)
        }
    }
}
