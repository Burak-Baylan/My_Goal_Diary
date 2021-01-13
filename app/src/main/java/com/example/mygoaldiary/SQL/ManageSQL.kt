package com.example.mygoaldiary.SQL

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class ManageSQL(private val context: Context?, private val activity: Activity?){

    fun createSqlVariable(sqlName : String): SQLiteDatabase? {
        return if (context != null && activity != null) {
            try {
                println("openOrCreate success")
                context.openOrCreateDatabase(sqlName, Context.MODE_PRIVATE, null)
            } catch (e: Exception) {
                println("openOrCreate error with:\n$e")
                null
            }
        }
        else{
            println("openOrCreate error")
            null
        }
    }

    fun tableCreator (sql : SQLiteDatabase?, tableName : String, variables : String){
        try {
            sql?.execSQL("CREATE TABLE IF NOT EXISTS $tableName ($variables)")
            println("table created")
        }catch (e : java.lang.Exception){
            println("table could not be created")
        }
    }

    fun adder (sql : SQLiteDatabase?, tableName : String, variablesName : String, variables : String){
        try{
            sql?.execSQL("INSERT INTO $tableName ($variablesName) VALUES ($variables)")
        }catch (e : Exception){
            e.localizedMessage
        }
    }

    fun get(sql : SQLiteDatabase, getColumnName : String, vararg columns : String): ArrayList<Any> {
        val cursor = sql.rawQuery("SELECT * FROM $getColumnName", null)
        val variablesSize = columns.size
        val returnArray = ArrayList<Any>(variablesSize - 1)
        val arrayHere = ArrayList<Int>(variablesSize - 1)
        var counter = 0
        println("while'ın dışında")
        while (cursor.moveToNext()){
            println("while'ın içinde")
            for (i in columns){
                println("for'un içinde Bu kadar: ${cursor.count} Burada:$i")
                println("Cursor index: $i")
                arrayHere.add(cursor.getColumnIndex(i))
            }

            for (i in 0..columns.size){
                println("for'un içinde Bu kadar: ${cursor.count} Burada:$i")
                //arrayHere.add(cursor.getColumnIndex("$i"))
            }

            returnArray.add(
                    try {
                        println("String aldı ${cursor.getString(arrayHere[counter])}")
                        SQLVariablesModel(cursor.getString(arrayHere[counter]))
                    }catch (e : Exception){
                        try {
                            println("Int aldı")
                            SQLVariablesModel(cursor.getInt(arrayHere[counter]))
                        }catch (e : Exception) {
                            println("Float aldı")
                            SQLVariablesModel(cursor.getFloat(arrayHere[counter]))
                        }
                    }
            )
            counter++
        }
        cursor.close()
        return returnArray
    }
}

/*
returnArray.add(
try {
    println("String aldı ${cursor.getString(arrayHere[counter])}")
    cursor.getString(arrayHere[counter])
}catch (e : Exception){
    try {
        println("Int aldı")
        cursor.getInt(arrayHere[counter])
    }catch (e : Exception) {
        println("Float aldı")
        cursor.getFloat(arrayHere[counter])
    }
}
)*/
