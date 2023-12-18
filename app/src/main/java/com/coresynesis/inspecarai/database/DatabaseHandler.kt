package com.coresynesis.inspecarai.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.coresynesis.inspecarai.models.InspecarAiModel
import com.coresynesis.inspecarai.models.PredicaoYoloModel

class IviDatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Versao do BD, mudar se adicionar ou alterar colunas
        private const val DATABASE_NAME = "dbIvi" // Nome do BD
        private const val TABLE_NAME = "tbIvi" // Nome da Tabela

        // Colunas, o _ indica que a coluna é Primary Key
        private const val KEY_ID_PRED = "_idPred"
        private const val KEY_LEFT = "leftCln"
        private const val KEY_TOP = "topCln"
        private const val KEY_RIGHT = "rightCln"
        private const val KEY_BOTTOM = "bottomCln"
        private const val KEY_TIPODANO = "tipodano"
        private const val KEY_CONFIANCA = "confianca"
        private const val KEY_IMGPATH = "imagem"
        private const val KEY_DISCORDA = "discorda"
        private const val KEY_USRTPDANO = "usrtipodano"


    }

    // Cria a tabela
    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_IVI_TABLE = ("CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID_PRED + " INTEGER PRIMARY KEY,"
                + KEY_LEFT + " INTEGER,"
                + KEY_TOP + " INTEGER,"
                + KEY_RIGHT + " INTEGER,"
                + KEY_BOTTOM + " INTEGER,"
                + KEY_TIPODANO + " TEXT,"
                + KEY_CONFIANCA + " TEXT,"
                + KEY_IMGPATH + " BLOB,"
                + KEY_DISCORDA + " TEXT,"
                + KEY_USRTPDANO + " TEXT)"
                )
        db?.execSQL(CREATE_IVI_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addInspIvi(tabIvi: PredicaoYoloModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_LEFT, tabIvi.left)
        contentValues.put(KEY_TOP, tabIvi.top)
        contentValues.put(KEY_RIGHT, tabIvi.right)
        contentValues.put(KEY_BOTTOM, tabIvi.bottom)
        contentValues.put(KEY_TIPODANO, tabIvi.tipodano)
        contentValues.put(KEY_CONFIANCA, tabIvi.confianca)
        contentValues.put(KEY_IMGPATH, tabIvi.imagem)
        contentValues.put(KEY_DISCORDA, tabIvi.discorda)
        contentValues.put(KEY_USRTPDANO, tabIvi.usrtipodano)

        // Insere registro
        val result = db.insert(TABLE_NAME, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Fecha a conexão do BD
        return result
    }

    fun readInspIvi(): Cursor? {
        // Abre o SQLite para leitura
        val iviDB = this.readableDatabase

        /*
        val projecao = arrayOf(KEY_LEFT, KEY_TOP, KEY_RIGHT,
            KEY_BOTTOM, KEY_TIPODANO, KEY_CONFIANCA, KEY_IMGPATH)
         */

        val cursor = iviDB.query(
            TABLE_NAME,
            null, // Nulo para retornar todas as colunas
            null,
            null,
            null,
            null,
            null
        )

        return cursor

    }

    fun delInspIvi(){
        val db = this.writableDatabase
        val deletedRows = db.delete(
            TABLE_NAME,
            null,
            null
        )
    }


}
