package com.coresynesis.inspecarai.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import android.widget.LinearLayout.VERTICAL
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coresynesis.inspecarai.R
import com.coresynesis.inspecarai.activities.MainActivity.Companion.codOrg
import com.coresynesis.inspecarai.adapter.CarAdapter
import com.coresynesis.inspecarai.adapter.RealTimeAdapter
import com.coresynesis.inspecarai.database.IviDatabaseHandler
import com.coresynesis.inspecarai.http.Methods
import com.coresynesis.inspecarai.http.RetrofitClient
import com.coresynesis.inspecarai.models.PredicaoModel
import com.coresynesis.inspecarai.models.PredicaoYoloModel
import com.google.android.gms.location.*
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextRecognizer
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import android.widget.Toast.makeText as makeText1


@Suppress("UNREACHABLE_CODE")
class AddCar : baseActivity() {
    companion object {
        // Dados predição
        var carro: String = "Não"
        var carroDano: String = "NA"
        var carroTipo: String = "NA"
        var carroSev: String = "NA"
        var carroEst: String = "NA"
        // Armazena na lista para envio ao adapter de visualização
        var dataList = ArrayList<PredicaoModel>()
        // Armazena retorno do Yolo Real time para envio ao Adapter
        var ydataList = ArrayList<PredicaoYoloModel>()
        // Armazena o retorno do OCR para envio ao adapter
//        var OcrdataList = ArrayList<OcrModel>()

        var imgPredbytearrayBound: ByteArray? = null

        // Dados GPS
        var ufName: String = ""
        var countryCode: String = ""
        var paisName: String = ""
        var numLocalName: String = ""
        var cidadeName:String = ""
        var cepName:String = ""
        var bairroName:String = ""
        var ruaName:String = ""
        var enderecoCompleto: String = ""
        var latitudeNumero:String = ""
        var longitudeNumero:String = ""

        // URL Servidor
        var URL: String = ""

        //Valor do Threshold
        var valThreshold:String = ""
    }

    // Placa lida pelo OCR
    var txtPlacaLida: String = ""

    // Cria a lista mutável (Array não permite adição)
    var placasArr: MutableList<String> = ArrayList()

    // Inicia BD local
    var iviDB = IviDatabaseHandler(this)

    // Local da foto
    lateinit var currentPhotoPath: String
    // Para armazenar o Storage, que grava as imagens
    lateinit var storage: FirebaseStorage

    //Variáveis para retorno de dados GPS
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest

    private val EXTERNAL_STORAGE_PERMISSION_CODE = 23

    @SuppressLint("MissingPermission", "WrongConstant")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_car)

        // GPS, inicia o Client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Chama o botão do design
        var btn_camera = findViewById(R.id.btnFotoCarro) as ImageButton
        var btn_cancelar = findViewById(R.id.btnCancelar) as ImageButton
        var btn_salvar = findViewById(R.id.btnSalvar) as ImageButton
        var btn_novo = findViewById(R.id.btnNovo) as ImageButton
        var btn_real_time = findViewById(R.id.btnCamRealTime) as ImageButton


        // Cria visual para o endereço
        var TxtEndereco = findViewById(R.id.textVwEndereco) as TextView
        // Cria visual para a placa lida
        //var txt_exibe_placa = findViewById(R.id.textVwExibePlaca) as TextView
        //txt_exibe_placa.text = txtPlacaLida

        // Cria o Spinner
        val spinnerPlaca: Spinner = findViewById(R.id.spinnerPlacaConsulta)


        // Cria a lista mutável (Array não permite adição)
        //var placasArr: MutableList<String> = ArrayList()

        // Variável para armazenar a url de upload da imagem no Storage do Firebase
        var downloadUriGrv: String = "no url"

        //Inicia a lista
        placasArr.add("BRA0000")

        // Leitura Firebase - Conecta à Instância
        var db = FirebaseFirestore.getInstance()

        // Conecta ao Firebase Storage
        storage = Firebase.storage

        // Cria uma referência ao Storage
        var storageRef = storage.reference

        // Retorna as placas na lista mutável
        db.collection("veiculo")
            .whereEqualTo("codOrg",MainActivity.codOrg)
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

        adapterPlaca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPlaca.setAdapter(adapterPlaca)

        // Retorna Configurações do Firebase
        db.collection("configuracao")
            .whereEqualTo("codOrg",MainActivity.codOrg)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    valThreshold = document.getString("valThreshold").toString()
                    Log.d("Dado encontrado", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Não Encontrado", "Doc. não encontrado ", exception)
            }


        // Adiciona espera até que o GPS esteja ativado
        if (!isLocationEnabled()) {
            makeText1(this, "Ativando o GPS", Toast.LENGTH_LONG).show()
            showCustomProgressDialog()
            do {
                Thread.sleep(2000)
            } while (!isLocationEnabled())
            hideProgressDialog()
        }


        // Click no botão para tirar a foto - *********************
        btn_camera.setOnClickListener {

            //Remover essas duas linhas para reabilitar o botão.
            //Toast.makeText(this, "Botão desativado", Toast.LENGTH_LONG).show()
            //return@setOnClickListener

            // Se GPS estiver desabilitado, requisita que ligue
            // Habilitar o GPS
            if (!CheckPermission()) {
                hideProgressDialog()
                RequestPermission() // Solicita permissão no GPS
                return@setOnClickListener // Sai da função, cancelando a gravação
            } else if (!isLocationEnabled()) {
                hideProgressDialog()
                makeText1(this, "Ative o GPS, antes de inspecionar", Toast.LENGTH_LONG).show()
                gpsActivate() // Ativa o GPS
                return@setOnClickListener // Sai da função, cancelando a gravação
            } else {
                // Recupera a última localização do dispositivo
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->

                    latitudeNumero = location?.latitude.toString()
                    longitudeNumero = location?.longitude.toString()

                    var (ufName, countryCode, paisName, cepName, cidadeName, bairroName, ruaName, numLocalName, enderecoCompleto) =
                        getEndereco(latitudeNumero.toDouble(), longitudeNumero.toDouble())

                    // Mostra o endereço na tela
                    TxtEndereco.text = Companion.enderecoCompleto

                    // Mostra a placa Lida pelo OCR
                    //txt_exibe_placa.setText(txtPlacaLida)

                }

            }

            // Permissao e abre a Camera
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Abre a camera
                dispatchTakePictureIntent()
            } else {
                requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    MainActivity.CAMERA_PERMISSION_CODE
                )
            }

        } // Fim Btn_Camera


        // Botão Cancelar - *********************
        btn_cancelar.setOnClickListener {
            dataList.clear()
            ydataList.clear()
            onBackPressed()
        }


        // Botão Camera Real Time - *********************
        btn_real_time.setOnClickListener {

            // Deleta registros gravados anteriormente na base local temporária
            try {
                iviDB.delInspIvi()
            } catch (e: IOException) {
                Log.i("ERRO", "Erro deleção SQLite: " + e)
            }

            // Limpa o Data List
            try {
                ydataList.clear()
            } catch (e: IOException) {
                Log.i("ERRO", "Erro clear DataList: " + e)
            }


            // Se GPS estiver desabilitado, requisita que ligue
            // Habilitar o GPS
            if (!CheckPermission()) {
                hideProgressDialog()
                RequestPermission() // Solicita permissão no GPS
                return@setOnClickListener // Sai da função, cancelando a gravação
            } else if (!isLocationEnabled()) {
                hideProgressDialog()
                makeText1(this, "Ative o GPS, antes de inspecionar", Toast.LENGTH_LONG).show()
                gpsActivate() // Ativa o GPS
                return@setOnClickListener // Sai da função, cancelando a gravação
            } else {
                // Recupera a última localização do dispositivo
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->

                    latitudeNumero = location?.latitude.toString()
                    longitudeNumero = location?.longitude.toString()

                    var (ufName, countryCode, paisName, cepName, cidadeName, bairroName, ruaName, numLocalName, enderecoCompleto) =
                        getEndereco(latitudeNumero.toDouble(), longitudeNumero.toDouble())

                    // Mostra o endereço na tela
                    TxtEndereco.text = Companion.enderecoCompleto
                }
            }


            //Verifica se já há permisão para gravar arquivo
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_STORAGE_PERMISSION_CODE
                )
                return@setOnClickListener // Sai da função
            } else {
                // Abre a camera do OpenCV (Real time)
                val intent = Intent(this, CameraActivity::class.java)
                // Envia para a Activity o valor lido do Firebase em CREATE
                intent.putExtra("valThreshold", valThreshold)
                //Inicia a Activity com a opção de retorno na função onActivityResult
                // Permite pegar o resultado da outra Activity.
                startActivityForResult(intent, MainActivity.REAL_TIME)
            }


        } // Fim Camera Real Time - *********************


        // Botão novo - *********************
        btn_novo.setOnClickListener {
            // Limpa data list de retorno da API
            dataList.clear()
            // Limpa data list de retorno do Real Time
            ydataList.clear()
            // Reabre a tela de inspeção
            val intent = Intent(this, AddCar::class.java)
            startActivity(intent)
            //Fecha a tela
            finish()
        }


        // Botão salvar - *********************
        btn_salvar.setOnClickListener {

            //Remover essas duas linhas para reabilitar o botão.
            //Toast.makeText(this, "Botão desativado", Toast.LENGTH_LONG).show()
            //return@setOnClickListener

            // *******************************************************************

            // Exibe a barra de progresso
            showCustomProgressDialog()

            // Placa selecionada
            val placaSel: String = spinnerPlaca.getSelectedItem().toString()

            // Verifica se a placa está preenchida
            if (placaSel != "BRA0000" && placaSel != null) {

                // Modelo Tiny Yolo - Local
                // Verifica se há inspeção
                if (!ydataList.isEmpty()) {
                    // Loop para passar em cada item do arraylist para gravação
                    for (i in ydataList.indices) {
                        // Data Hora da Inspeção
                        var dataHora = LocalDateTime.now().toString()
                        // Placa do veículo
                        val placaDoc: String = spinnerPlaca.getSelectedItem().toString()
                        // Concatenação para formar o nome do documento
                        val docRegistro = "$codOrg$i$placaDoc$dataHora"

                        // Cria a referência no Firebase
                        val inspecarRef = storageRef.child(docRegistro + ".jpg")

                        //ydataList[i].imagem
                        // Convert ByteArray em imagem
                        val bmp = BitmapFactory.decodeByteArray(
                            ydataList[i].imagem,
                            0,
                            ydataList[i].imagem.size
                        )

                        // Converte para stream de bytes
                        val baos = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()

                        // Upload da imagem para o Storage do Firebase
                        var uploadTask = inspecarRef.putBytes(data)

                        uploadTask.addOnFailureListener {
                            makeText1(this, "Falha no Firestore Upload ", Toast.LENGTH_LONG)
                                .show()
                        }.addOnSuccessListener { taskSnapshot ->
                            hideProgressDialog()
                            makeText1(this, "upload Firestore com sucesso", Toast.LENGTH_LONG)
                                .show()
                        }

                        // hashMap para gravar os dados do arraylist yolo e variáveis no firestore
                        val yoloFirestore = hashMapOf(
                            "placa" to placaDoc,
                            "dataHora" to dataHora,
                            "predicaoDano" to ydataList[i].tipodano.toString(),
                            "predicaoConf" to ydataList[i].confianca.toString(),
                            "UsrDiscorda" to ydataList[i].discorda.toString(),
                            "PosLeft" to ydataList[i].left.toString(),
                            "PosTop" to ydataList[i].top.toString(),
                            "PosRight" to ydataList[i].right.toString(),
                            "PosBottom" to ydataList[i].bottom.toString(),
                            "usrTipoDano" to ydataList[i].usrtipodano.toString(),
                            "PredicaoImg" to docRegistro + ".jpg",
                            // Dados GPS
                            "ufName" to ufName,
                            "countryCode" to countryCode,
                            "paisNome" to paisName,
                            "numLocalName" to numLocalName,
                            "cidadeNome" to cidadeName,
                            "cepNome" to cepName,
                            "bairroNome" to bairroName,
                            "ruaNome" to ruaName,
                            "latitudeNum" to latitudeNumero,
                            "longitudeNum" to longitudeNumero,
                            // Usuário
                            "userID" to getCurrentUserID(),
                            "userEmail" to getCurrentUserEmail(),
                            // Organização
                            "codOrg" to MainActivity.codOrg,
                            // API ou RealTime
                            "tipoModelo" to "RLT",
                            // Grava Threshold da avaliação
                            "valThreshold" to valThreshold
                        )

                        // Grava no firestore
                        db.collection("inspecao").document(docRegistro)
                            .set(yoloFirestore)
                            .addOnSuccessListener {
                                Log.d(
                                    "Gravado",
                                    "DocumentSnapshot successfully written!"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "Erro DB",
                                    "Error writing document",
                                    e
                                )
                            }

                        makeText1(this, "REGISTRO GRAVADO", Toast.LENGTH_LONG).show()

                    } // FIM - For Loop - Gravação Yolo no Firebase

                } // FIM IF - Modelo Yolo


                // Modelo pela API
                // Verifica se há inspeção
                if (!dataList.isEmpty()) {

                    // Loop para passar em cada item do arraylist para gravação
                    for (i in dataList.indices) {

                        // Data Hora da Inspeção
                        var dataHora = LocalDateTime.now().toString()
                        val placaDoc: String = spinnerPlaca.getSelectedItem().toString()
                        // Concatenação para formar o nome do documento
                        val docRegistro = "$i$placaDoc$dataHora"

                        // Cria a referência
                        val inspecarRef = storageRef.child(docRegistro + ".jpg")

                        // Convert ByteArray em imagem
                        val bmp = BitmapFactory.decodeByteArray(
                            dataList[i].imagempred,
                            0,
                            dataList[i].imagempred.size
                        )
                        // Converte para stream de bytes
                        val baos = ByteArrayOutputStream()
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()

                        // Upload da imagem para o Storage do Firebase
                        var uploadTask = inspecarRef.putBytes(data)
                        uploadTask.addOnFailureListener {
                            makeText1(this, "Falha no Firestore Upload ", Toast.LENGTH_LONG)
                                .show()
                        }.addOnSuccessListener { taskSnapshot ->
                            hideProgressDialog()
                            makeText1(this, "upload Firestore com sucesso", Toast.LENGTH_LONG)
                                .show()
                        }

                        // hashMap para gravar os dados do arraylist e variáveis no firestore
                        val predicaoFirestore = hashMapOf(
                            "placa" to placaDoc,
                            "dataHora" to dataHora,
                            "predicaoDano" to dataList[i].predicao.toString(),
                            "predicaoTipo" to dataList[i].predicaotipo.toString(),
                            "predicaoSev" to dataList[i].predicaosev.toString(),
                            "UsrDiscorda" to dataList[i].discorda.toString(),
                            "PredicaoImg" to docRegistro + ".jpg",
                            // Dados GPS
                            "ufName" to ufName,
                            "countryCode" to countryCode,
                            "paisNome" to paisName,
                            "numLocalName" to numLocalName,
                            "cidadeNome" to cidadeName,
                            "cepNome" to cepName,
                            "bairroNome" to bairroName,
                            "ruaNome" to ruaName,
                            "latitudeNum" to latitudeNumero,
                            "longitudeNum" to longitudeNumero,
                            // Usuário
                            "userID" to getCurrentUserID(),
                            "userEmail" to getCurrentUserEmail(),
                            // Organização
                            "codOrg" to MainActivity.codOrg,
                            // API ou RealTime
                            "tipoModelo" to "API",
                            // Grava Threshold da avaliação
                            "valThreshold" to valThreshold
                        )

                        // Grava no firestore
                        db.collection("inspecao").document(docRegistro)
                            .set(predicaoFirestore)
                            .addOnSuccessListener {
                                Log.d(
                                    "Gravado",
                                    "DocumentSnapshot successfully written!"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w(
                                    "Erro DB",
                                    "Error writing document",
                                    e
                                )
                            }

                        makeText1(this, "REGISTRO GRAVADO", Toast.LENGTH_LONG).show()

                    } // Fim - For Loop - Gravação

                } // Fim If verificação de dados preenchidos API

                // Se não houver inspeção
                if (ydataList.isEmpty() && dataList.isEmpty()) {
                    hideProgressDialog()
                    makeText1(this, "Faça uma inspeção antes de salvar", Toast.LENGTH_LONG)
                        .show()
                }


            } // FIM - IF - Veificação de placa preenchida
            else {
                hideProgressDialog()
                makeText1(this, "Informe a placa antes de salvar", Toast.LENGTH_LONG).show()
            }

        } // Fim - btnSalvar



    } // Fim OnCreate


    // Se não houver permissão, a função de requisição de permissão (alterada) é acionada. CAMERA
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Abre a camera
                dispatchTakePictureIntent()
            } else {
                makeText1(this, "Camera permission was denied.", Toast.LENGTH_LONG).show()
            }
        }
    }


    // Visualização da imagem salva de ambos os metodos de aplicação do modelo
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Retorno para a Activity Real Time
        if (requestCode == MainActivity.REAL_TIME && resultCode == RESULT_CANCELED) {
            // Exibe a barra de progresso
            showCustomProgressDialog()
            //Lê e exibe na tela resultado da Predição Real Time
            readLocalDBRealTime()
            // Oculta a barra de progresso
            hideProgressDialog()
        }

        // Retorno para a Activity Inspecionar (Usa API)
        if (requestCode == MainActivity.CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            // f = arquivo salvo da foto tirada
            val f = File(currentPhotoPath)
            println("Código currentPhotoPath: " + currentPhotoPath)
            //iv_image.setImageURI(Uri.fromFile(f))

            // Grava caminho da imagem no log
            Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f))

            // Convert para byte array
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath, options)
            val imgPred = getBytesFromBitmap(bitmap)

            // Chamada da API de predicao
            /*
            if (imgPred != null) {
                getPredFromAPI(f, imgPred)
            }
             */

            // VOLTAR AQUI
            txtPlacaLida = getTextFromImage(bitmap).toString()
            // Cria visual para a placa lida
            //var txt_exibe_placa = findViewById(R.id.textVwExibePlaca) as TextView
            //txt_exibe_placa.text = txtPlacaLida

            // Adiciona a placa lida por OCR
            placasArr.add(txtPlacaLida)

            // Adaptador para listar os dados no Spinner - Adiciona as placas ao Spinner (Dropdown)
            val adapterPlaca: ArrayAdapter<String> =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, placasArr)

            // Cria o Spinner
            val spinnerPlaca: Spinner = findViewById(R.id.spinnerPlacaConsulta)
            adapterPlaca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPlaca.setAdapter(adapterPlaca)
            spinnerPlaca.setSelection(adapterPlaca.getPosition(txtPlacaLida))


        } //Fim - if Camera OK
    } // Fim - onActivityResult - Camera



    // Lê o BD local para recuperar os dados da análise Real Time e projeta na tela
    @SuppressLint("WrongConstant")
    private fun readLocalDBRealTime(){

        // Cria o Recycle View
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPesquisa)
        recyclerView.layoutManager =
            LinearLayoutManager(
                this,
                VERTICAL,
                false
            )

        // Lê os dados do banco local
        val cursorIvi = iviDB.readInspIvi()

        //val itemIds = mutableListOf<PredicaoYoloModel>()
        with(cursorIvi) {
            while (this?.moveToNext()!!) {
                val itemId = this?.getLong(this?.getColumnIndexOrThrow("_idPred"))
                val itemLeft = this?.getInt(this?.getColumnIndexOrThrow("leftCln"))
                val itemTop = this?.getInt(this?.getColumnIndexOrThrow("topCln"))
                val itemRight = this?.getInt(this?.getColumnIndexOrThrow("rightCln"))
                val itemBottom = this?.getInt(this?.getColumnIndexOrThrow("bottomCln"))
                val itemtipoDano = this?.getString(this?.getColumnIndexOrThrow("tipodano"))
                val itemConfianca = this?.getString(this?.getColumnIndexOrThrow("confianca"))
                val itemImgPath = this?.getBlob(this?.getColumnIndexOrThrow("imagem"))
                val itemDiscorda = this?.getString(this?.getColumnIndexOrThrow("discorda"))
                val itemusrTipoDano = this?.getString(this?.getColumnIndexOrThrow("usrtipodano"))

                //var imagemGrv = Imgcodecs.imread(itemImgPath, Imgproc.COLOR_RGB2RGBA)

                // Lê imagem gravada localmente
                // Convert para byte array
                //val options = BitmapFactory.Options()
                //options.inPreferredConfig = Bitmap.Config.ARGB_8888
                //val iviBitmap = BitmapFactory.decodeFile(itemImgPath, options)
                //var bmpblob = getBytesFromBitmap(iviBitmap)


                // Armazena dados lidos no Data List
                ydataList.add(
                    PredicaoYoloModel(itemLeft, itemTop, itemRight, itemBottom,
                        itemtipoDano, itemConfianca, itemImgPath!!, itemDiscorda, itemusrTipoDano)
                )

                // Preenche dados na tela
                var realAdapter = RealTimeAdapter(ydataList)
                recyclerView.adapter = realAdapter

            } // Fim While Loop
        } // Fim With Cursor

        // Fechar o cursor
        cursorIvi?.close()

    }



    // Chama a API, recebe o arquivo de imagem como parametro
    @SuppressLint("WrongConstant")
    private fun getPredFromAPI(f: File, imgPredbytearray: ByteArray) {

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPesquisa)
        recyclerView.layoutManager =
            LinearLayoutManager(
                this,
                VERTICAL,
                false
            )

        // Prepara o corpo para ser enviado para o metodo POST
        var requestBody = f.asRequestBody("image/jpeg".toMediaTypeOrNull())

        // Monta a imagem para envio à API
        var filePart = MultipartBody.Part.createFormData(
            "image",
            f.name,
            requestBody
        )

        // Cria client Web - Conexão com a API de Machine Learning
        //val URL = "http://192.168.0.5:80"
        // Inicia Firebase
        var db = FirebaseFirestore.getInstance()

        // Retorna Configurações do Firebase
        db.collection("configuracao")
            .whereEqualTo("codOrg",MainActivity.codOrg)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    URL = document.getString("urlAPI").toString()
                    Log.d("Dado encontrado", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Não Encontrado", "Doc. não encontrado ", exception)
            }

        //val URL = "http://ec2-18-116-159-153.us-east-2.compute.amazonaws.com:80"

        val retrofitClient = RetrofitClient.getRetrofitInstance(URL)
        //Service
        val endpoint = retrofitClient.create(Methods::class.java)
        val callbackPost = endpoint.uploadImage(filePart, requestBody)

        // Barra de progresso
        showCustomProgressDialog()

        callbackPost.enqueue(object : Callback<PredicaoModel> {
            override fun onFailure(call: Call<PredicaoModel>, t: Throwable) {
                hideProgressDialog()
                makeText1(baseContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<PredicaoModel>, response: Response<PredicaoModel>) {
                // Esconde a barra de progresso
                hideProgressDialog()

                try {

                    // Carro danificado
                    if (response.body()?.predicao?.toFloat()!! <= .5) {
                        carroDano = "Sim"
                        // Modelo Tipo Dano
                        //d = {0: 'Arranhão', 1: 'Batida leve', 2: 'Batida Grave'
                        if (response.body()?.predicaotipo?.toString()!! == "0") {
                            carroTipo = "Arranhão"
                        } else if (response.body()?.predicaotipo?.toString()!! == "1" ||
                            response.body()?.predicaotipo?.toString()!! == "2"
                        ) {
                            carroTipo = "Batida"
                        }
                        // Modelo Severidade
                        //    d = {0: 'Pequeno', 1: 'Médio', 2: 'Grave'}
                        if (response.body()?.predicaosev?.toString()!! == "0") {
                            carroSev = "Pequeno"
                        } else if (response.body()?.predicaosev?.toString()!! == "1"
                        ) {
                            carroSev = "Médio"
                        } else if (response.body()?.predicaosev?.toString()!! == "2") {
                            carroSev = "Grave"
                        }
                    } else {
                        carroDano = "Não"
                        carroTipo = "NA"
                        carroSev = "NA"
                    }

                    dataList.add(
                        PredicaoModel(carroDano, carroTipo, carroSev, carro, carroEst,
                            imgPredbytearray,"NA")
                    )

                    var carAdapter = CarAdapter(dataList)
                    recyclerView.adapter = carAdapter

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }) // Fim - callbackGet.enqueue

    } // Fim getPredFromAPI



    // Função para retornar o texto de uma imagem - OCR - Reconhecimento da placa
    private fun getTextFromImage(bitmap: Bitmap): String {
        val recognizer = TextRecognizer.Builder(this).build()
        val stringBuilder = StringBuilder()
        if (!recognizer.isOperational) {
            Toast.makeText(this, "Ocorreu um ERRO", Toast.LENGTH_SHORT).show()
        } else {
            val frame = Frame.Builder().setBitmap(bitmap).build()
            val textBlockSparseArray = recognizer.detect(frame)
            for (i in 0 until textBlockSparseArray.size()) {
                val textBlock = textBlockSparseArray.valueAt(i)
                stringBuilder.append(textBlock.value)
                stringBuilder.append("\n")
            }
            return stringBuilder.toString()
        }
        return stringBuilder.toString()
    }


    // Converte de bitmap para byte array
    fun getBytesFromBitmap(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }


    // Salva a imagem para diretorio interno - Somente a aplicação acessa o arquivo
    private fun saveImageToInternalStorage(jpeg: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(MainActivity.IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            jpeg.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Retorna a imagem pela uri
        return Uri.parse(file.absolutePath)

    } // Fim - Salva a imagem


    //https://developer.android.com/training/camera/photobasics#kotlin
    // Salva na galeria ou sem proteção no diretorio interno
    @Suppress("DEPRECATION")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        // Salva desprotegido - Pasta com acesso do usuário
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        // Salva na galeria
        //val storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    // Chama a intent da camera e tira a foto
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Garante que há uma activity para a câmera
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Cria um arquivo para a foto
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Erro ao criar o arquivo
                    ex.printStackTrace()
                    null
                }
                // Continua se o arquivo foi criado com sucesso
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.coresynesis.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, MainActivity.CAMERA_REQUEST_CODE)
                }
            }
        }
    }


    // Classe para múltiplos retornos na função que recupera o endereço
    data class dadosEndereco(var ufName: String,
                             var countryCode: String,
                             var paisName: String,
                             var cepName: String,
                             var cidadeName: String,
                             var bairroName: String,
                             var ruaName: String,
                             var numLocalName: String,
                             var enderecoCompleto: String)

    // Pega endereço dado latitude e longitude
    fun getEndereco(latitude: Double, longitude: Double): dadosEndereco {
        // Retorna o endereço, 3 resultados
        var geoCoder = Geocoder(this, Locale.getDefault())
        var Adress = geoCoder.getFromLocation(latitude,longitude,3)

        // Indíce 1, pega o segundo resultado, no primeiro o CEP vem incompleto.
        ufName =  Adress.get(0).adminArea.toString() // UF
        countryCode = Adress.get(0).countryCode.toString() // BR
        paisName =  Adress.get(0).countryName.toString() // País
        cepName =  Adress.get(0).postalCode.toString() // CEP
        cidadeName =  Adress.get(0).subAdminArea.toString() // Cidade
        bairroName =  Adress.get(0).subLocality.toString() // Bairro
        ruaName =  Adress.get(0).thoroughfare.toString() //Rua
        numLocalName = Adress.get(0).subThoroughfare.toString() // Número Rua

        enderecoCompleto = ruaName + ", n.º " + numLocalName + ", " + bairroName + ", " + cidadeName + "/" + ufName +
                ", CEP. " +  cepName + ", " + paisName

        return dadosEndereco(ufName, countryCode, paisName, cepName, cidadeName, bairroName, ruaName, numLocalName, enderecoCompleto)
    }



}


