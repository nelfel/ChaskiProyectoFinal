package com.example.chaskiproyectofinal.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaskiproyectofinal.R
import com.example.chaskiproyectofinal.model.ProductModel
import com.google.android.material.card.MaterialCardView

//Adapter productos no extrae los datos de firebase, se los pasamos por medio de una lista de tipo<ProductModel>
// Solo pasamos el context(this), la lista, y el recycler que utilizamoss
// Este adapter es utilizado por CarritoActivity
class AdapterCarritoProducts(private val context: Context, var listaProductos: List<ProductModel>) : RecyclerView.Adapter<AdapterCarritoProducts.ViewHolder>() {

    // Lista para almacenar los totales calculados
    private val listaTotales: MutableList<Double> = MutableList(listaProductos.size) { 0.0 }
    //Para calcular los totales por aparte hicimos una lista que guarde esos totales
    // Función llamada para crear la vista del elemento en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_platillos_carrito, parent, false)
        return ViewHolder(view)
    }

    // Función llamada para asociar los datos a la vista del elemento en el RecyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = listaProductos[position]

        // Asignar valores a los elementos de la vista del elemento
        holder.tvNombrePlatilloDC.text = producto.nombre
        holder.tvPrecioPDC.text = producto.precio.toString()

        // Cargar la imagen del producto utilizando Glide
        Glide.with(holder.itemView.context)
            .load(producto.urlImagenProduct)
            .into(holder.imgPlatilloDC)

        // Manejar el evento de clic en el botón "Eliminar del carrito"
        holder.btnEliminarPCarrito.setOnClickListener {
            // Remover el producto de la lista y notificar al RecyclerView
            val nuevaLista = listaProductos.toMutableList()
            nuevaLista.removeAt(position)
            listaProductos = nuevaLista
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listaProductos.size)

        }

        // Manejar eventos de clic en los botones de incrementar y decrementar cantidad
        holder.btnDecrease.setOnClickListener {
            val cantidadActual = holder.edtCantidad.text.toString().toInt()
            val nuevaCantidad = maxOf(cantidadActual - 1, 0)
            holder.edtCantidad.setText(nuevaCantidad.toString())
            holder.edtCantidad.requestLayout()
            notifyDataSetChanged()
            actualizarTotales(holder.adapterPosition, nuevaCantidad)
        }

        holder.btnAdd.setOnClickListener {
            var cantidadActual = holder.edtCantidad.text.toString().toInt()
            cantidadActual++
            holder.edtCantidad.setText(cantidadActual.toString())
            holder.edtCantidad.requestLayout()
            notifyDataSetChanged()
            actualizarTotales(holder.adapterPosition, cantidadActual)
        }

        // Manejar cambios en la cantidad mediante un TextWatcher
        holder.edtCantidad.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar este método
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar este método
            }

            override fun afterTextChanged(editable: Editable?) {
                if (!editable.isNullOrBlank()) {
                    val nuevaCantidad = editable.toString().toIntOrNull() ?: 0
                    actualizarTotales(holder.adapterPosition, nuevaCantidad)
                }
            }
        })
    }

    // Función para actualizar los totales y notificar cambios al RecyclerView
    private fun actualizarTotales(position: Int, nuevaCantidad: Int) {
        if (position >= 0 && position < listaProductos.size && position < listaTotales.size) {
            // Actualizar la cantidad en el modelo
            listaProductos[position].cantidad = nuevaCantidad

            // Calcular el total y almacenarlo en la listaTotales
            val total = nuevaCantidad * listaProductos[position].precio
            listaTotales[position] = total

            // Notificar al RecyclerView que se ha actualizado un elemento
            notifyItemChanged(position)

        } else {
            // Manejar caso de posición inválida
        }
    }

    // Función para obtener la cantidad de elementos en el RecyclerView
    override fun getItemCount(): Int {
        return listaProductos.size
    }

    // Clase interna que representa la vista de cada elemento en el RecyclerView
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvMisCompras: MaterialCardView = itemView.findViewById(R.id.cvMisCompras)
        val imgPlatilloDC: ImageView = itemView.findViewById(R.id.imgPlatilloDC)
        val tvNombrePlatilloDC: TextView = itemView.findViewById(R.id.tvNombrePlatilloDC)
        val btnEliminarPCarrito: ImageView = itemView.findViewById(R.id.btnEliminarPCarrito)
        val tvPrecioPDC: TextView = itemView.findViewById(R.id.tvPrecioPDC)
        val btnDecrease: ImageView = itemView.findViewById(R.id.btnDecrease)
        val edtCantidad: EditText = itemView.findViewById(R.id.edtCantidad)
        val btnAdd: ImageView = itemView.findViewById(R.id.btnAdd)
    }

    // Función para obtener la lista de totales
    fun getListaTotales(): List<Double> {
        return listaTotales
    }

    // Función para obtener la lista de productos
    fun obtenerListaProductos(): List<ProductModel> {
        return listaProductos
    }
}
