package com.coresynesis.inspecarai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.coresynesis.inspecarai.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList
import android.widget.Toast.makeText as makeTextIvi

class ConfigActivity : baseActivity() {

    companion object {
        // Variável que guarda o valor da Confiança/Threshold
        var ThresholdValue: Float = 0.0f
    }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        // Cria o Seek bar, Text view e Spinner
        var seekBarVar = findViewById(R.id.seekBar) as SeekBar
        var txtThresholdValue = findViewById(R.id.textViewThresholdValue) as TextView
        var txtServer = findViewById(R.id.textViewExibeServer) as TextView

        // Se o usuário for Master habilita a edição
        seekBarVar.isEnabled = MainActivity.userMaster

        // Inicia Firebase
        var db = FirebaseFirestore.getInstance()

        // Retorna Configurações do Firebase
        db.collection("configuracao")
            .whereEqualTo("codOrg",MainActivity.codOrg)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    txtThresholdValue.text = "Confiança:" + document.getString("valThreshold").toString() + "%"
                    txtServer.text = document.getString("urlAPI").toString()
                    Log.d("Dado encontrado", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Não Encontrado", "Doc. não encontrado ", exception)
            }


            seekBarVar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                // Display the current progress of SeekBar
                ThresholdValue = (i/100.00).toFloat()
                txtThresholdValue.text = "Confiança: $ThresholdValue" + "%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

                // hashMap para gravar os dados no firestore
                val ConfFirestore = hashMapOf(
                    "codOrg" to MainActivity.codOrg,
                    "urlAPI" to txtServer.text,
                    "valThreshold" to String.format("%.2f", ThresholdValue)
                )

                // Grava no firestore
                db.collection("configuracao").document(MainActivity.codOrg)
                    .set(ConfFirestore)
                    .addOnSuccessListener {
                        Log.d(
                            "Gravado",
                            "Documento gravado com sucesso!"
                        )
                    }
                    .addOnFailureListener { e ->
                        Log.w(
                            "Erro DB",
                            "Erro ao gravar documento",
                            e
                        )
                    }

            } // Fim - onStopTrackingTouch

        }) // FIM - seekBarVar.setOnSeekBarChangeListener


    }

}

