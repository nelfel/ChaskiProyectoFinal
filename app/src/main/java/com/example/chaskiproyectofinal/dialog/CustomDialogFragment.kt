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
 * Clase que representa un DialogFragment personalizado para mostrar un diálogo de calificacion de repartidores.
 * Proporciona opciones para evaluar y comentar, con eventos notificados a través de la interfaz DialogListener.
 * Es igual que CustomDialogComida, ver comentarios alli
 *  Estas clases se llaman en envioActivity
 */
class CustomDialogFragment : DialogFragment() {

    interface DialogListener {
        fun onEnviarClicked(rating: Float, comment: String)
        fun onOmitirClicked()
    }

    private var listener: DialogListener? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_layout, null)

        builder.setView(view)
            .setTitle("Calificacion")

        // Configurar los botones
        view.findViewById<Button>(R.id.enviarButton).setOnClickListener {
            val rating = view.findViewById<RatingBar>(R.id.ratingBar).rating
            val comment = view.findViewById<EditText>(R.id.etComentario).text.toString()
            listener?.onEnviarClicked(rating, comment)
            dismiss()
        }

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
