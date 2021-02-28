package com.example.mygoaldiary.SQL

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class ManageSQL(private val context: Context?, private val activity: Activity?) {

    fun createSqlVariable(sqlName: String): SQLiteDatabase? {
        return if (context != null && activity != null) {
            try {
                println("openOrCreate success")
                context.openOrCreateDatabase(sqlName, Context.MODE_PRIVATE, null)
            } catch (e: Exception) {
                println("openOrCreate error with:\n$e")
                null
            }
        } else {
            println("openOrCreate error (context or activity null)")
            null
        }
    }

    fun tableCreator(sql: SQLiteDatabase?, tableName: String, variables: String) : Boolean {
        return try {
            sql?.execSQL("CREATE TABLE IF NOT EXISTS $tableName ($variables)")
            println("table created: $tableName")
            true
        } catch (e: java.lang.Exception) {
            println("table could not be created : $e")
            false
        }
    }

    fun adder(sql: SQLiteDatabase?, tableName: String, variablesName: String, variables: String): Boolean {
        return if (sql != null) {
            try {
                sql.execSQL("INSERT INTO $tableName ($variablesName) VALUES ($variables)")
                true
            } catch (e: Exception) {
                println("1. Hata ${e.localizedMessage}")
                false
            }
        } else {
            false
        }
    }

    fun manager(sqlDatabase: SQLiteDatabase?, sql: String): Boolean {
        return if (sqlDatabase != null) {
            try {
                sqlDatabase.execSQL(sql)
                true
            } catch (e: Exception) {
                println("2. Hata ${e.localizedMessage}")
                false
            }
        } else {
            false
        }
    }
}