package com.coresynesis.inspecarai.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.coresynesis.inspecarai.R
import com.google.android.gms.location.LocationRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Activity base para reaproveitamento de código
open class baseActivity : AppCompatActivity(){

    private var mProgressDialog: Dialog? = null
    val PERMISSION_ID = 1010

    // GPS - Verifica permissões
    fun CheckPermission():Boolean{
        //true: Se houver permissão
        //false: Se não houver permissão
        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }


    // GPS - Requisita permissão
    fun RequestPermission(){
        // Solicita permissão para acesso ao GPS
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    // // GPS Ativado
    fun isLocationEnabled():Boolean{
        // Retorna o estaod do serviço de localização
        // Se o GPS ou network provider estiver ativo retorna true senão false
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    // Abre tela para ativar GPS
    fun gpsActivate() {
        var intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent1)
    }

    // Monta Barra de Progresso
    fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        //Carrega o layout
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        // Inicia o dialogo na tela
        mProgressDialog!!.show()
    }

    // Esconde Barra de Progresso
    fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getCurrentUserEmail(): String{
        return FirebaseAuth.getInstance().currentUser!!.email
    }


}