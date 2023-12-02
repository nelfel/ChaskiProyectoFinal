package com.example.chaskiproyectofinal.adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chaskiproyectofinal.EnvioActivity
import com.example.chaskiproyectofinal.ListPedidosActivity
import com.example.chaskiproyectofinal.ListRepartidoresActivity
import com.example.chaskiproyectofinal.R
import com.example.chaskiproyectofinal.model.PedidoModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// La clase AdapterPedidos hereda de FirestoreRecyclerAdapter, que maneja la carga de datos desde Firestore
// para poblar el RecyclerView de manera eficiente

// options: Contiene configuraciones como la consulta a Firestore, el modelo de datos (PedidoModel),
// y otras opciones relacionadas con la configuración del adaptador.
// Estas opciones personalizadas son necesarias para que FirestoreRecyclerAdapter funcione correctamente.

// PedidoModel: Representa el modelo de datos para los pedidos. Pueden ser documentos en tu colección de Firestore

// PedidoViewHolder: Clase interna que representa un ViewHolder personalizado para los elementos del RecyclerView
//Esta clase AdapterPedidos se utiliza en ListPedidosActivity
class AdapterPedidos(
    // Se pasa la instancia de FirebaseFirestore al constructor de la clase
    private val db: FirebaseFirestore,
    // Se pasan las opciones de configuración para FirestoreRecyclerAdapter al constructor
    options: FirestoreRecyclerOptions<PedidoModel>
) : FirestoreRecyclerAdapter<PedidoModel, AdapterPedidos.PedidoViewHolder>(options) {

    //uid guarda el identificador unico del usuario que se encuentra conectado
    private val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // ViewHolder para mantener las vistas de los elementos en el RecyclerView
    class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombreRestaurante: TextView = itemView.findViewById(R.id.tvNombreRes)
        val textViewEstado: TextView = itemView.findViewById(R.id.tvEstadoT)
        val textViewTotal: TextView = itemView.findViewById(R.id.tvTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int, model: PedidoModel) {
        val idRestaurante = model.restauranteId
        Log.d("ID RESTAURANTE", idRestaurante)
        Log.d("ID USE", uid)

        val nombreRestaurant=obtenerInformacionRestaurante(idRestaurante, holder)

        holder.textViewNombreRestaurante.text = nombreRestaurant.toString()
        holder.textViewTotal.text = model.total.toString()
        holder.textViewEstado.text = model.estado
        Log.d("NombreRestaurante", "Nombre del restaurante: $nombreRestaurant")

        val context = holder.itemView.context
        // Establecer el listener de clic en el elemento de la vista, clic sobre cualquier elemento
        holder.itemView.setOnClickListener {
            //si el identificado es igual al identificador del restaurante entonces compara el model.estado
            //del item en el que se hizo clic si esta pendiente,en proceso o finalizado
            //se imprimira un DialogSgow verlo como una ventana emergente con datos
            // tambien esta la parte del else si no es restaurante tiene otras opciones igual dependiendo del estado
            // Y tambien se hace modificacion del estado en la bd, dependiendo si es restaurante o usuario
            if (uid == idRestaurante) {
                //solo se imprime dialog,
                if(model.estado == "En Proceso"){
                    val builder = AlertDialog.Builder(context)
                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Pedido")
                    builder.setMessage("El pedido ya esta en camino")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                    }
                    // Mostrar el diálogo
                    builder.show()

                }else if(model.estado == "Pendiente"){
                    //Se imprime dialog y tambie se modifica el estado en la bd
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Pedido")
                    builder.setMessage("Selecciona un repartidor para Enviar el pedido")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                        model.estado = "En Proceso"
                        // Actualizar el estado en la base de datos
                        val db = FirebaseFirestore.getInstance()
                        db.collection("pedidos").document(model.pedidoId)
                            .update("estado", "En Proceso")
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                                // Manejar el error al actualizar en la base de datos
                                Toast.makeText(context, "Error al actualizar el estado en la base de datos", Toast.LENGTH_SHORT).show()
                            }
                        val intent = Intent(context, ListRepartidoresActivity::class.java)
                        context.startActivity(intent)
                    }
                    // Mostrar el diálogo
                    builder.show()

                }else{
                    //solo se imprime dialog
                    val builder = AlertDialog.Builder(context)
                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Pedido")
                    builder.setMessage("El pedido ya esta fue entregado")
                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                    }
                    // Mostrar el diálogo
                    builder.show()
                }
            } else {

                if(model.estado == "En Proceso"){
                    //se imprime dialog y se modifica la bd el model.estado
                    val builder = AlertDialog.Builder(context)
                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Pedido")
                    builder.setMessage("Tu pedido se encuentra en camino, sigue el proceso de envio")
                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                        model.estado = "Finalizado"
                        val db = FirebaseFirestore.getInstance()
                        db.collection("pedidos").document(model.pedidoId)
                            .update("estado", "Finalizado")
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                                // Manejar el error al actualizar en la base de datos
                                Toast.makeText(context, "Error al actualizar el estado en la base de datos", Toast.LENGTH_SHORT).show()
                            }
                        val intent = Intent(context, EnvioActivity::class.java)
                        context.startActivity(intent)
                    }
                    // Mostrar el diálogo
                    builder.show()
                }else if(model.estado == "Pendiente"){
                    //solo se imprime dialog
                    val builder = AlertDialog.Builder(context)

                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Pedido en proceso")
                    builder.setMessage("El pedido ya está en proceso. Espere a que el restaurante envíe la comida.")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->

                    }
                    // Mostrar el diálogo
                    builder.show()
                }else {
                    //solo se imprime dialog
                    val builder = AlertDialog.Builder(context)
                    // Establecer el título y el mensaje del diálogo
                    builder.setTitle("Pedido Finalizado")
                    builder.setMessage("El pedido ya Finalizo. Gracias por su preferencia.")

                    // Establecer un botón de aceptar
                    builder.setPositiveButton("Aceptar") { dialog, which ->
                    }
                    // Mostrar el diálogo
                    builder.show()
                }
            }

        }
    }
    //Aqui se obtiene el nombre del estaurante
    private fun obtenerInformacionRestaurante(idRestaurante: String, holder: PedidoViewHolder) {
        db.collection("restaurantes").document(idRestaurante)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Aquí obtienes la información del restaurante desde documentSnapshot
                    val nombreRestaurante = documentSnapshot.getString("nombre")
                    // ... Obtén otros datos necesarios del restaurante

                    // Actualiza las vistas en el ViewHolder con la información del restaurante
                    holder.textViewNombreRestaurante.text = nombreRestaurante
                }
            }
            .addOnFailureListener { e ->
                // Manejar errores
                // Puedes imprimir un mensaje de log o mostrar un Toast, según tus necesidades
            }
    }

}
