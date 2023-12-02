package com.example.chaskiproyectofinal.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.example.chaskiproyectofinal.R
/**
 * Clase que representa un DialogFragment personalizado para mostrar un diálogo de calificacion de comida.
 * Proporciona opciones para evaluar y comentar, con eventos notificados a través de la interfaz DialogListener.
 *Estas clases se llaman en envioActivity
 */

class CustomDialogComida : DialogFragment() {

    // Interfaz que define los métodos de escucha para los eventos del diálogo
    interface DialogListener {
        // Llamado cuando se hace clic en el botón "Enviar"
        fun onEnviarClicked(rating: Float, comment: String)
        // Llamado cuando se hace clic en el botón "Omitir"
        fun onOmitirClicked()
    }
    // Objeto que escuchará los eventos del diálogo
    private var listener: DialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Crear el constructor del diálogo
        val builder = AlertDialog.Builder(requireActivity())
        // Inflar el diseño personalizado del diálogo
        val inflater = requireActivity().layoutInflater
        //Referencia en las vistas o layout dialog_comida
        val view = inflater.inflate(R.layout.dialog_comida, null)
        //Titulo
        builder.setView(view)
            .setTitle("Calificaciones")

        // Configurar los botones
        //Cuando se hace clic en el boton enviar
        view.findViewById<Button>(R.id.enviarButton).setOnClickListener {
            val rating = view.findViewById<RatingBar>(R.id.ratingBar).rating
            val comment = view.findViewById<EditText>(R.id.etComentario).text.toString()
            listener?.onEnviarClicked(rating, comment)
            dismiss()
        }

        //Cuando se hace clic en el boton omitir
        view.findViewById<Button>(R.id.omitirButton).setOnClickListener {
            listener?.onOmitirClicked()
            dismiss()
        }

        return builder.create()
    }

    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }
}