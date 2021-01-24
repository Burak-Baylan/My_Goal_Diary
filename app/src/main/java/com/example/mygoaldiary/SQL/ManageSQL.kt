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
            println("openOrCreate error (context or activity null)")
            null
        }
    }

    fun tableCreator (sql : SQLiteDatabase?, tableName : String, variables : String){
        try {
            sql?.execSQL("CREATE TABLE IF NOT EXISTS $tableName ($variables)")
            println("table created: $tableName")
        }catch (e : java.lang.Exception){
            println("table could not be created : $e")
        }
    }

    fun adder (sql : SQLiteDatabase?, tableName : String, variablesName : String, variables : String) : Boolean{
        return if (sql != null){
            try {
                sql.execSQL("INSERT INTO $tableName ($variablesName) VALUES ($variables)")
                true
            }
            catch (e : Exception){
                println("1. Hata ${e.localizedMessage}")
                false
            }
        }else{
            false
        }
    }

    fun manager (sqlDatabase : SQLiteDatabase?, sql : String) : Boolean{
        return if (sqlDatabase != null) {
            try {
                sqlDatabase.execSQL(sql)
                true
            } catch (e: Exception) {
                println("2. Hata ${e.localizedMessage}")
                false
            }
        }
        else{
            false
        }
    }

    /*fun get(sql : SQLiteDatabase, getColumnName : String, vararg columns : String): ArrayList<Any> {
        val cursor = sql.rawQuery("SELECT * FROM $getColumnName", null)

    }*/

    /*fun get(sql : SQLiteDatabase, getColumnName : String, vararg columnIndex : String): ArrayList<ArrayList<Any>>{
        val cursor = sql.rawQuery("SELECT * FROM $getColumnName", null)
        val variablesSize = columnIndex.size
        val returnArray = ArrayList<ArrayList<Any>>(variablesSize - 1)
        val arrayHere = ArrayList<Any>(variablesSize - 1)
        while (cursor.moveToNext()){

            for (i in columnIndex) {
                try {
                    arrayHere.add(cursor.getString(cursor.getColumnIndex(i)))
                } catch (e: Exception) {
                    try {
                        arrayHere.add(cursor.getInt(cursor.getColumnIndex(i)))
                    } catch (e: Exception) {
                        try {
                            arrayHere.add(cursor.getFloat(cursor.getColumnIndex(i)))
                        } catch (e: Exception) {
                            throw e
                        }
                    }
                }
            }

            for (i in arrayHere){
                returnArray.add(arrayHere)
            }

        }
        cursor.close()
        return returnArray
    }*/
}

/*
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
 */

/**
 *                  val title = cursor.getString(cursor.getColumnIndex("title"))
                    val projectColor = cursor.getInt(cursor.getColumnIndex("projectColor"))
                    val yearDate = cursor.getString(cursor.getColumnIndex("yearDate"))
                    val time = cursor.getString(cursor.getColumnIndex("time"))
 */

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