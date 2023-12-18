package com.coresynesis.inspecarai.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout.VERTICAL
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coresynesis.inspecarai.R
import com.coresynesis.inspecarai.adapter.QueryAdapterRealTime
import com.coresynesis.inspecarai.models.InspecaoModelYolo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*


class QueryInspectRealTimeActivity : baseActivity(){

    companion object {
        var inspPlaca: String = ""
        var inspDataHora: String = ""
        var inspPredTipo: String = ""
        var inspPredConf: String = ""
        var inspPredImg: String = ""
        var dataListRealTime = ArrayList<InspecaoModelYolo>()
        var inspecaoPredImg : ByteArray? = null
    }

    //lateinit var storage: FirebaseStorage
    var storage = Firebase.storage

    @SuppressLint("LongLogTag", "WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_inspection)

        // Cria o botão de consulta
        var btn_consulta = findViewById(R.id.imgBtnConsulta) as ImageButton

        // Cria o Spinner
        val spinnerPlaca: Spinner = findViewById(R.id.spinnerPlacaConsulta)

        // Cria a lista mutável (Array não permite adição)
        var placasArr: MutableList<String> = ArrayList()

        //Inicia a lista
        placasArr.add("BRA0000")

        // Inicia Firebase
        var db = FirebaseFirestore.getInstance()


        // Retorna as placas no lista mutável
        db.collection("veiculo").whereEqualTo("codOrg",MainActivity.codOrg)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    placasArr.add(document.getString("carroPlaca").toString())
                    Log.d("Dado encontrado", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Não Encontrado", "Error getting documents: ", exception)
            }

        // Adaptador para listar os dados no Spinner - Adiciona as placas ao Spinner (Dropdown)
        val adapterPlaca: ArrayAdapter<String> =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, placasArr)
        // Cria dropdown
        adapterPlaca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Armazena os dados no spinner
        spinnerPlaca.setAdapter(adapterPlaca)


        // Botão de realizar a consulta
        btn_consulta.setOnClickListener {

            // Limpa a lista de exibição
            dataListRealTime.clear()

            // Barra de progresso
            showCustomProgressDialog()

            // Cria o RecyclerView
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPesquisa)

            // Limpa a lista exibida
            dataListRealTime.clear()
            recyclerView?.adapter?.notifyDataSetChanged()

            // Configura o layout do RecyclerView
            recyclerView.layoutManager =
                LinearLayoutManager(
                    this,
                    VERTICAL,
                    false
                )

            // Armazena a placa selecionada
            val placaDoc: String = spinnerPlaca.getSelectedItem().toString()

            // Faz a pesquisa no FireStore pela placa selecionada
            db.collection("inspecao")
                .whereEqualTo("codOrg",MainActivity.codOrg)
                .whereEqualTo("tipoModelo", "RLT")
                .whereEqualTo("placa", placaDoc)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("Dado recebido com sucesso", "${document.id} => ${document.data}")

                        var inspPlaca = document.data.getValue("placa").toString()
                        var inspDataHora = document.data.getValue("dataHora").toString()
                        var inspPredTipo = document.data.getValue("predicaoDano").toString()
                        var inspPredConf = document.data.getValue("predicaoConf").toString()


                        var inspPredImg = document.data.getValue("PredicaoImg").toString()

                        // Cria a referência para o Firebase Storage
                        val storageRef = storage.reference

                        // Cria a referência para a imagem armazenada
                        var carInspRef = storageRef.child( inspPredImg )

                        // Determina o tamanho máximo do download
                        val Num_MEGABYTE: Long = 6L * 1024 * 1024

                        // Faz o download da imagem
                        carInspRef.getBytes(Num_MEGABYTE)
                            .addOnSuccessListener { bytes ->
                                inspecaoPredImg = bytes
                                //println("inspecaoPredImg 1: " + inspecaoPredImg)

                                // Adiciona a image mao data list para projeção, junto aos outros dados
                                dataListRealTime.add(InspecaoModelYolo(inspPlaca, inspDataHora,
                                    inspPredTipo, inspPredConf, inspecaoPredImg) )

                                // Chama o adapter para exibir os dados
                                var queryAdapter = QueryAdapterRealTime(dataListRealTime)
                                recyclerView.adapter = queryAdapter

                                // Exconde a barra de progresso
                                //hideProgressDialog()
                        }.addOnFailureListener {
                                // Exconde a barra de progresso
                                hideProgressDialog()
                                Toast.makeText(this,
                                    "Falha no download da imagem, erro:$it.message",
                                    Toast.LENGTH_LONG
                                ).show()
                        }


                    } // fim For
                }
                .addOnFailureListener { exception ->
                    Log.w("Falha ao obter dados", "Erro ao receber documentos: ", exception)
                    Toast.makeText(
                        this,
                        "Falha ao obter dados:  $exception",
                        Toast.LENGTH_LONG
                    ).show()

                    hideProgressDialog()
                }
                .addOnCompleteListener {
                    hideProgressDialog()
                }

            } // Fim btn Consulta

    } // Fim OnCreate

}

