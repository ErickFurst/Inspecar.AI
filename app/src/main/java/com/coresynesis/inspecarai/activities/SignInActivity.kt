package com.coresynesis.inspecarai.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.coresynesis.inspecarai.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignInActivity : baseActivity() {

    private lateinit var auth: FirebaseAuth
    private var mProgressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        var btnUsrLogin = findViewById<Button>(R.id.btnRegistrar)
        // Inicializa o Firebase Auth
        auth = Firebase.auth

        // Login do usuário
        btnUsrLogin.setOnClickListener {
            signInUser()
        }
    }

    // Faz o Signin do usuário
    private fun signInUser(){

        var txtUsrLogin = findViewById<EditText>(R.id.userEmail)
        var txtPassword = findViewById<EditText>(R.id.userPassword)

        var usrLogin: String = txtUsrLogin.text.toString().trim {it <= ' '}
        val usrPassword: String = txtPassword.text.toString().trim{it <= ' '}


        /*

        // Código para autenticação por nome de usuário

        // Leitura Firebase - Conecta à Instância
        var db = FirebaseFirestore.getInstance()

        FirebaseAuth.getInstance().signInAnonymously()

        db.collection("usuario")
            .whereEqualTo("userName", usrLogin)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    var usrEmail = document.getString("email").toString()

                    if (validateForm(usrEmail, usrPassword)){
                        showCustomProgressDialog()
                        // Sign-In com FirebaseAuth
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(usrEmail, usrPassword)
                            .addOnCompleteListener { task ->
                                hideProgressDialog()
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this@SignInActivity,
                                        "Signed in com sucesso.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                                } else {
                                    Toast.makeText(
                                        this@SignInActivity,
                                        task.exception!!.message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    } // Fim if formulario valido

                }
            }
            .addOnFailureListener { exception ->
                //Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(
                    this@SignInActivity,
                    exception!!.message,
                    Toast.LENGTH_LONG
                ).show()
            }


         */


        if (validateForm(usrLogin, usrPassword)){
            showCustomProgressDialog()
            // Sign-In com FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(usrLogin, usrPassword)
                .addOnCompleteListener { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@SignInActivity,
                            "Signed in com sucesso.",
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@SignInActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        } // Fim if formulario valido


    }


/*
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        //updateUI(currentUser)
    }

 */

    /*
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

     */

/*

    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
 */


    private fun validateForm(email: String, password: String) : Boolean {
        return when {
            TextUtils.isEmpty(email)->{
                //showErrorSnackBar("Informe o email do usuário")
                Toast.makeText(this, "Por favor, informe o usuário", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(password)->{
                Toast.makeText(this, "Por favor, informe a senha", Toast.LENGTH_LONG).show()
                false
            } else -> {
                true
            }
        }
    } // Fim - validateForm



}