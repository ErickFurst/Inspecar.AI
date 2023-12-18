package com.coresynesis.inspecarai.models

import android.graphics.Bitmap
import java.sql.Blob

data class InspecaoModel(
    val inspPlaca: String,
    val inspDataHora: String,
    val inspPredDano: String,
    val inspPredTipo: String,
    val inspPredSev: String,
    val inspPredImg: ByteArray?
    )

data class PredicaoModel(
    val predicao: String,
    val predicaotipo: String,
    val predicaosev: String,
    val predicaocarro: String,
    val estilocarro: String,
    val imagempred: ByteArray,
    var discorda: String
)

data class InspecaoModelYolo(
    val inspPlaca: String,
    val inspDataHora: String,
    val inspPredTipo: String,
    val inspPredConf: String,
    val inspPredImg: ByteArray?
)

//Modelo para gravar e ler dados do Tiny Yolo
data class PredicaoYoloModel(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
    val tipodano: String,
    val confianca: String,
    val imagem: ByteArray,
    var discorda: String,
    var usrtipodano: String)



data class VeiculoModel(
    val anoFabricacao: Int,
    val anoModelo: Int,
    val categoria: String,
    val chassi: String,
    val cor: String,
    val marca: String,
    val modelo: String,
    val placa: String,
    val renavam: String )



data class InspecarAiModel (
    val idCarro: Int,
    val placaCarro: String,
    val dataInspecao: String,
    val localidadeInspc: String,
    val latitude: Double,
    val longitude: Double,
    val imgFrente: String,
    val imgFrenteEsq: String,
    val imgLateralEsq: String,
    val imgTrasEsq: String,
    val imgTras: String,
    val imgTrasDir: String,
    val imgLateralDir: String,
    val imgFrenteDir: String,
    // Campos de identificacao de veiculo
    val confirmaVeiculo: String,
    val tipoCarro: String,
    // Campos de identificacao de dano
    val danoPos1: String,
    val danoPos1Val: Double,
    val danoPos2: String,
    val danoPos2Val: Double,
    val danoPos3: String,
    val danoPos3Val: Double,
    val danoPos4: String,
    val danoPos4Val: Double,
    val danoPos5: String,
    val danoPos5Val: Double,
    val danoPos6: String,
    val danoPos6Val: Double,
    val danoPos7: String,
    val danoPos7Val: Double,
    val danoPos8: String,
    val danoPos8Val: Double,
    // Campos de Tipo de Dano
    val tipoDanoPos1: String,
    val tipoDanoPos1Val: Double,
    val tipoDanoPos2: String,
    val tipoDanoPos2Val: Double,
    val tipoDanoPos3: String,
    val tipoDanoPos3Val: Double,
    val tipoDanoPos4: String,
    val tipoDanoPos4Val: Double,
    val tipoDanoPos5: String,
    val tipoDanoPos5Val: Double,
    val tipoDanoPos6: String,
    val tipoDanoPos6Val: Double,
    val tipoDanoPos7: String,
    val tipoDanoPos7Val: Double,
    val tipoDanoPos8: String,
    val tipoDanoPos8Val: Double,
    // Campos de severidade de dano
    val graveDanoPos1: String,
    val graveDanoPos1Val: Double,
    val graveDanoPos2: String,
    val graveDanoPos2Val: Double,
    val graveDanoPos3: String,
    val graveDanoPos3Val: Double,
    val graveDanoPos4: String,
    val graveDanoPos4Val: Double,
    val graveDanoPos5: String,
    val graveDanoPos5Val: Double,
    val graveDanoPos6: String,
    val graveDanoPos6Val: Double,
    val graveDanoPos7: String,
    val graveDanoPos7Val: Double,
    val graveDanoPos8: String,
    val graveDanoPos8Val: Double

    )
