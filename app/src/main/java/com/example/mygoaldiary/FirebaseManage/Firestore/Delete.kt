package com.example.mygoaldiary.FirebaseManage.Firestore

class Delete : Firestore(){

    companion object{

        fun deleteData(collectionName: String, documentName: String, successFun: () -> Unit, failFunction: () -> Unit){
            firebase.collection(collectionName).document(documentName).delete().addOnSuccessListener {
                successFun()
            }.addOnFailureListener {
                failFunction()
            }
        }

        fun deleteData(collectionName: String, documentName: String, collection2 : String, documents2 : String,
                       successFun: () -> Unit, failFunction: () -> Unit
        ){
            firebase.collection(collectionName).document(documentName).collection(collection2).document(documents2).delete().addOnSuccessListener {
                successFun()
            }.addOnFailureListener {
                failFunction()
            }
        }

    }

}