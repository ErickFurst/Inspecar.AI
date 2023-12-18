package com.coresynesis.inspecarai.firebase

import com.google.firebase.firestore.FirebaseFirestore



class Firestoreclass {

    // Access a Cloud Firestore instance from your Activity
    //val db = Firebase.firestore
    var db = FirebaseFirestore.getInstance()


    fun leFirestore (){
        val docRef = db.collection("veiculo").document("7l8d0iGcnBRpmW8GWzfQ")
        docRef.get()

    }


}