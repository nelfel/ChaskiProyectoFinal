package com.example.chaskiproyectofinal.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.chaskiproyectofinal.R

/**
 * Clase que representa un DialogFragment personalizado para mostrar un diálogo de formulario para soporte.
 * Proporciona opciones para evaluar y comentar, con eventos notificados a través de la interfaz DialogListener.
 * Es igual que CustomDialogComida, ver comentarios alli
 *  Estas clases se llaman en soporteActivity
 */
class CustomDialogSoporte : DialogFragment() {

    interface DialogListener {
        fun onEnviarClicked(editTextAsunto: String, editTextMensaje: String)
    }

    private var listener: DialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_soporte, null)

        builder.setView(view)
            .setTitle("Formulario Soporte")

        val btnEnviar = view.findViewById<Button>(R.id.btnEnviar)

        btnEnviar.setOnClickListener {
            val editTextAsunto = view.findViewById<EditText>(R.id.editTextAsunto).text.toString()
            val editTextMensaje = view.findViewById<EditText>(R.id.editTextMensaje).text.toString()

            listener?.onEnviarClicked(editTextAsunto, editTextMensaje)
            dismiss()
        }

        return builder.create()
    }

    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }
}
