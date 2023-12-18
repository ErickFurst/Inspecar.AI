package com.coresynesis.inspecarai.activities

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.coresynesis.inspecarai.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AddNewCarData : baseActivity(){

    lateinit var storage: FirebaseStorage
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_car_data)

        // Criação botões
        var btnRegistrar = findViewById(R.id.btnRegistrar) as Button
        var btnNovoCarro = findViewById(R.id.btnNewCar) as FloatingActionButton

        // Criação de Caixas de edição
        var txtPlaca = findViewById<EditText>(R.id.carroPlaca)
        var txtCategoria = findViewById<EditText>(R.id.carroCategoria)
        var txtAnoFabricacao = findViewById<EditText>(R.id.anoFabricacao)
        var txtAnoModelo = findViewById<EditText>(R.id.anoModelo)
        var txtMarca = findViewById<EditText>(R.id.carroMarca)
        var txtModelo = findViewById<EditText>(R.id.carroModelo)
        var txtChassi = findViewById<EditText>(R.id.carroChassi)
        var txtRenavam = findViewById<EditText>(R.id.carroRenavam)
        var txtCor = findViewById<EditText>(R.id.carroCor)

        var db = FirebaseFirestore.getInstance()

        btnNovoCarro.setOnClickListener {

            // Limpa caixas de edição
            txtPlaca.text.clear()
            txtCategoria.text.clear()
            txtAnoFabricacao.text.clear()
            txtAnoModelo.text.clear()
            txtMarca.text.clear()
            txtModelo.text.clear()
            txtChassi.text.clear()
            txtRenavam.text.clear()
            txtCor.text.clear()

            // Ativa edição após salvamento
            txtPlaca.isEnabled = true
            txtCategoria.isEnabled = true
            txtAnoFabricacao.isEnabled = true
            txtAnoModelo.isEnabled = true
            txtMarca.isEnabled = true
            txtModelo.isEnabled = true
            txtChassi.isEnabled = true
            txtRenavam.isEnabled = true
            txtCor.isEnabled = true

            // Ativa o botão
            btnRegistrar.isEnabled = true
        }

        // Botão registrar usuário
        btnRegistrar.setOnClickListener {

            showCustomProgressDialog()

            // Limpeza de dados
            val placa: String = txtPlaca.text.toString().trim { it <= ' ' }
            val categoria: String = txtCategoria.text.toString().trim { it <= ' ' }
            val anoFabricacao: String = txtAnoFabricacao.text.toString().trim { it <= ' ' }
            val anoModelo: String = txtAnoModelo.text.toString().trim { it <= ' ' }
            val marca: String = txtMarca.text.toString().trim { it <= ' ' }
            val modelo: String = txtModelo.text.toString().trim { it <= ' ' }
            val chassi: String = txtChassi.text.toString().trim { it <= ' ' }
            val renavam: String = txtRenavam.text.toString().trim { it <= ' ' }
            val cor: String = txtCor.text.toString().trim { it <= ' ' }

            // Validação de dados preenchidos e gravação
            if (validateForm(placa, categoria, anoFabricacao, anoModelo, marca, modelo, chassi, renavam, cor)) {

                // hashMap para gravar os dados no firestore
                val veiculoFirestore = hashMapOf(
                    "carroPlaca" to placa,
                    "anoFabricacao" to anoFabricacao,
                    "anoModelo" to anoModelo,
                    "carroCategoria" to categoria,
                    "carroChassi" to chassi,
                    "carroCor" to cor,
                    "carroMarca" to marca,
                    "carroModelo" to modelo,
                    "carroRenavam" to renavam,
                    "codOrg" to MainActivity.codOrg
                )

                // Grava no firestore
                db.collection("veiculo").document(placa)
                    .set(veiculoFirestore)
                    .addOnSuccessListener { Log.d("Gravado", "DocumentSnapshot gravado com sucesso!")
                        Toast.makeText(
                            this,
                            "Veículo adicionado/atualizado com sucesso: Placa:  $placa.",
                            Toast.LENGTH_SHORT
                        ).show()

                        hideProgressDialog()

                        // Desativa edição após salvamento
                        txtPlaca.isEnabled = false
                        txtCategoria.isEnabled = false
                        txtAnoFabricacao.isEnabled = false
                        txtAnoModelo.isEnabled = false
                        txtMarca.isEnabled = false
                        txtModelo.isEnabled = false
                        txtChassi.isEnabled = false
                        txtRenavam.isEnabled = false
                        txtCor.isEnabled = false

                        btnRegistrar.isEnabled = false
                        // Sai da tela
                        //onBackPressed()

                    }
                    .addOnFailureListener { e -> Log.w("Erro DB", "Erro gravando documento", e)
                        Toast.makeText(
                            this,
                            "Erro ao adicionar veículo: Placa:  $placa.",
                            Toast.LENGTH_SHORT
                        ).show()

                        hideProgressDialog()

                    }

            } // Fim If validação

        } // Fim botão registrar

    } //Fim CREATE


    // Verifica se os dados estão preenchidos
    private fun validateForm(placa: String, categoria: String, anoFabricacao: String, anoModelo: String, marca: String, modelo: String, chassi: String, renavam: String, cor: String): Boolean {
        return when {
            TextUtils.isEmpty(placa) -> {
                Toast.makeText(this, "Por favor, informe a placa", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(categoria) -> {
                Toast.makeText(this, "Por favor, informe a categoria", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(anoFabricacao) -> {
                Toast.makeText(this, "Por favor, informe o ano de fabricação", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(anoModelo) -> {
                Toast.makeText(this, "Por favor, informe o ano do modelo", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(marca) -> {
                Toast.makeText(this, "Por favor, informe a marca", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(modelo) -> {
                Toast.makeText(this, "Por favor, informe o modelo", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(chassi) -> {
                Toast.makeText(this, "Por favor, informe o chassi", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(renavam) -> {
                Toast.makeText(this, "Por favor, informe o renavam", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            TextUtils.isEmpty(cor) -> {
                Toast.makeText(this, "Por favor, informe a cor", Toast.LENGTH_LONG).show()
                hideProgressDialog()
                false
            }
            else -> {
                true
            }
        }
    }



}