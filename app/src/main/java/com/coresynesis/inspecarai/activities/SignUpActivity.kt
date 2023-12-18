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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class SignUpActivity : baseActivity() {

    private lateinit var auth: FirebaseAuth
    //private var mProgressDialog: Dialog? = null
    // Para armazenar o Storage, que grava as imagens
    //lateinit var storage: FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        var txtCodOrg = findViewById<EditText>(R.id.codOrganizacao)
        //val codOrg: String = txtCodOrg.text.toString().trim { it <= ' ' }
        txtCodOrg.setText(MainActivity.codOrg)
        txtCodOrg.isEnabled = false

        var btnUsrLogin = findViewById<Button>(R.id.btnRegistrar)
        // Initialize Firebase Auth
        auth = Firebase.auth

        // Login do usuário
        btnUsrLogin.setOnClickListener {
            registerUser()
            addUserData()
        }

    }

    private fun registerUser() {

        var txtEmail = findViewById<EditText>(R.id.userEmail)
        var txtPassword = findViewById<EditText>(R.id.userPassword)
        var txtUserName = findViewById<EditText>(R.id.userLogin)


        val email: String = txtEmail.text.toString().trim { it <= ' ' }
        val password: String = txtPassword.text.toString().trim { it <= ' ' }
        val userName: String = txtUserName.text.toString().trim { it <= ' ' }



        if (validateForm(email, password, userName, MainActivity.codOrg)) {

            showCustomProgressDialog()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        hideProgressDialog()

                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                            val registeredEmail = firebaseUser.email!!

                            Toast.makeText(
                                this@SignUpActivity,
                                "Usuário registrado com sucesso: e-mail id $registeredEmail.",
                                Toast.LENGTH_SHORT
                            ).show()

                            FirebaseAuth.getInstance().signOut()

                            finish()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }


    private fun addUserData() {

        var txtUserName = findViewById<EditText>(R.id.userLogin)
        var txtCodOrg = findViewById<EditText>(R.id.codOrganizacao)
        var txtEmail = findViewById<EditText>(R.id.userEmail)

        val userName: String = txtUserName.text.toString().trim { it <= ' ' }
        val codOrg: String = txtCodOrg.text.toString().trim { it <= ' ' }
        val email: String = txtEmail.text.toString().trim { it <= ' ' }

        val userFirestore = hashMapOf(
            "userName" to userName,
            "codOrg" to MainActivity.codOrg,
            "email" to email,
            "userMaster" to false
        )

        var db = FirebaseFirestore.getInstance()

        // Grava no firestore
        db.collection("usuario").document(codOrg+"_"+userName)
            .set(userFirestore)
            .addOnSuccessListener { Log.d("Gravado", "Usuario gravado com sucesso!") }
            .addOnFailureListener { e -> Log.w("Erro DB", "Erro gravando usuario", e) }

    }


    private fun validateForm(email: String, password: String, user: String, codOrg: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this@SignUpActivity, "Por favor, informe o e-mail", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this@SignUpActivity, "Por favor, informe a senha", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(user) -> {
                Toast.makeText(this@SignUpActivity, "Por favor, informe o login do usuário", Toast.LENGTH_LONG).show()
                false
            }
            TextUtils.isEmpty(codOrg) -> {
                Toast.makeText(this@SignUpActivity, "Por favor, informe a Organização", Toast.LENGTH_LONG).show()
                false
            }
            else -> {
                true
            }
        }
    }



}