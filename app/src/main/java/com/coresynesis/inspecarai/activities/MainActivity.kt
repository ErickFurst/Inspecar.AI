package com.coresynesis.inspecarai.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.coresynesis.inspecarai.R
import com.coresynesis.inspecarai.activities.baseActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.opencv.android.OpenCVLoader

class MainActivity : baseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var auth: FirebaseAuth

    // Variaveis para configurar permissao e selecionar camera traseira
    companion object {
        const val CAMERA_PERMISSION_CODE =1
        const val CAMERA_REQUEST_CODE =2
        const val IMAGE_DIRECTORY = "InspecarAI"
        const val REAL_TIME = 99
        lateinit var codOrg: String
        var userMaster: Boolean = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicia o Firebase Auth
        auth = Firebase.auth

        // Inicia Firebase
        var db = FirebaseFirestore.getInstance()

        // Retorna a organização através do e-mail de Login
        db.collection("usuario").whereEqualTo("email",getCurrentUserEmail())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    codOrg = document.getString("codOrg").toString()
                    userMaster = document.get("userMaster") as Boolean

                    Log.d("Dado encontrado", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Não Encontrado", "Error getting documents: ", exception)
            }


        // Inicia a toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Inicia o Drawer (Permite o menu flutuante)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)

        // Menu flutuante
        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        // Menu home marcado por default
        navView.setCheckedItem(R.id.nav_home)

        // Botao flutuante para adicionar uma nova inspecao de carro
        var btnaddcar = findViewById<FloatingActionButton>(R.id.btnAddInspectCar)

        btnaddcar.setOnClickListener {

            // Se GPS estiver desabilitado, requisita que ligue
            // Habilitar o GPS
            if (!CheckPermission()) {
                hideProgressDialog()
                RequestPermission() // Solicita permissão no GPS
                return@setOnClickListener // Sai da função, cancelando a gravação
            } else if (!isLocationEnabled()) {
                hideProgressDialog()
                Toast.makeText(this, "Ative o GPS, antes de inspecionar", Toast.LENGTH_LONG).show()
                gpsActivate() // Ativa o GPS
                return@setOnClickListener // Sai da função, cancelando a gravação
            }

            // Abre a tela de inspeção.
            val intent = Intent(this, AddCar::class.java)
            startActivity(intent)
        }

    }

    // Controla Menus
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        when (p0.itemId) {
            R.id.nav_home -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_car -> {
                // Se GPS estiver desabilitado, requisita que ligue
                // Habilitar o GPS
                if (!CheckPermission()) {
                    hideProgressDialog()
                    RequestPermission() // Solicita permissão no GPS
                    return false  // Sai da função, cancelando a gravação
                } else if (!isLocationEnabled()) {
                    hideProgressDialog()
                    Toast.makeText(this, "Ative o GPS, antes de inspecionar", Toast.LENGTH_LONG).show()
                    gpsActivate() // Ativa o GPS
                    return false // Sai da função, cancelando a gravação
                } else {
                    val intent = Intent(this, AddCar::class.java)
                    startActivity(intent)
                }
                }
            R.id.nav_ncar -> {
                val intent = Intent(this, AddNewCarData::class.java)
                startActivity(intent)
            }
            R.id.nav_user -> {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_rlt -> {
                val intent = Intent(this, QueryInspectRealTimeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_inspec -> {
                val intent = Intent(this, QueryInspectActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_conf -> {
                val intent = Intent(this, ConfigActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                makeText(this,"Logout",Toast.LENGTH_LONG).show()
                Firebase.auth.signOut()
                this.finishAffinity()
            }

            else -> super.onOptionsItemSelected(p0)
        }

        return true
    }


}