package com.example.chaskiproyectofinal.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chaskiproyectofinal.R
import com.example.chaskiproyectofinal.model.ProductModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

//Ver info en AdapterPedidos es lo mismo, lo unico que cambia es cuano se hace clic sobre un elemento
//Aqui se crea la lista local e productos que se utiliza en AdaptercarritoProduct
//AdapterProducts se utiliza en ListProduxtActivity
class AdapterProducts(
    private val db: FirebaseFirestore,
    options: FirestoreRecyclerOptions<ProductModel>
) : FirestoreRecyclerAdapter<ProductModel, AdapterProducts.ProductViewHolder>(options) {

    // Clase interna para representar la vista de cada elemento en el RecyclerView
    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewProducto: ImageView = itemView.findViewById(R.id.imgProduct)
        val textViewNombreProducto: TextView = itemView.findViewById(R.id.txtName)
        val textViewDescripcion: TextView = itemView.findViewById(R.id.txtDescription)
        val txtPrecioProducto: TextView = itemView.findViewById(R.id.txtPrice)
    }

    // Lista mutable local para almacenar productos seleccionados
    val listaProductos = mutableListOf<ProductModel>()

    // Función llamada para crear la vista del elemento en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(itemView)
    }

    // Función llamada para asociar los datos a la vista del elemento en el RecyclerView
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: ProductModel) {
        // Cargar la imagen del producto utilizando Glide
        Glide.with(holder.itemView)
            .load(model.urlImagenProduct)
            .into(holder.imageViewProducto)

        // Asignar los datos del producto a las vistas en el ViewHolder
        holder.textViewNombreProducto.text = model.nombre
        holder.textViewDescripcion.text = model.descripcion
        holder.txtPrecioProducto.text = model.precio.toString()

        val context = holder.itemView.context

        // Establecer el listener de clic en el elemento de la vista
        holder.itemView.setOnClickListener {
            // Mostrar un cuadro de diálogo para preguntar al usuario si desea comprar el producto
            AlertDialog.Builder(context).apply {
                setTitle("¿Desea comprar este producto?")
                setMessage("El producto " + model.nombre + " cuesta " + model.precio + ".")
                setPositiveButton("Sí", DialogInterface.OnClickListener { dialog, which ->
                    // Obtener el producto seleccionado
                    val productoSeleccionado = getItem(position)

                    // Verificar si el producto ya está en la lista
                    if (listaProductos.contains(productoSeleccionado)) {
                        // Mostrar un mensaje al usuario si el producto ya está en la lista
                        AlertDialog.Builder(context).apply {
                            setTitle("Ya agregaste este producto al carrito")
                            setMessage("El producto " + productoSeleccionado.nombre + " ya está en tu carrito.")
                            setPositiveButton("Aceptar", null)
                            show()
                        }
                    } else {
                        // Agregar el producto a la lista local
                        listaProductos.add(
                            ProductModel(
                                model.nombre,
                                model.descripcion,
                                model.precio,
                                model.urlImagenProduct,
                                model.restauranteId,
                                model.cantidad
                            )
                        )
                    }
                })
                setNegativeButton("No", null)
                show()
            }
        }
    }
}
