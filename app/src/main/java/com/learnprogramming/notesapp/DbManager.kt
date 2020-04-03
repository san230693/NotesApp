package com.learnprogramming.notesapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbManager {
    //DB Name
    var dbName = "MyNotes"
    //Table name
    var dbTable ="Notes"

    //column
    var colId = "ID"
    var colTitle = "Title"
    var colDes = "Description"

    //db version
    var dbVersion = 1

    //Create table if not exists MyNotes(ID Integer Primary Key,name Text,Description Text)
    var sqlCreateTable = "CREATE TABLE IF NOT EXISTS "+dbTable+" ("+colId+" INTEGER PRIMARY KEY,"+colTitle+" TEXT, "+ colDes+" TEXT)"

    var sqlDB:SQLiteDatabase? = null

    constructor(context: Context){
        var db = DatabaseHelperNotes(context)
        sqlDB = db.writableDatabase
    }

    inner class DatabaseHelperNotes:SQLiteOpenHelper{
        var context: Context?=null
        constructor(context: Context):super(context,dbName,null,dbVersion){
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(this.context,"Database Created ..",Toast.LENGTH_SHORT).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("Drop table if exists" + dbTable)
        }
    }

    fun insert(values: ContentValues):Long{
        val ID = sqlDB!!.insert(dbTable,"",values)
        return ID
    }

    fun Query(projection:Array<String>,selection:String,seletionArgs:Array<String>,sorOrder:String):Cursor{
        val qb = SQLiteQueryBuilder();
        qb.tables = dbTable
        val cursor = qb.query(sqlDB,projection,selection,seletionArgs,null,null,sorOrder)
        return cursor
    }

    fun delete(selection: String,selectionArgs:Array<String>):Int{
        val count = sqlDB!!.delete(dbTable,selection,selectionArgs)
        return count
    }

    fun update(values: ContentValues,selection: String,selectionArgs: Array<String>):Int{
        val count = sqlDB!!.update(dbTable,values,selection,selectionArgs)
        return count
    }
}

/*onCreate is called for the first time when creation of tables are needed.
 We need to override this method where we write the script for table creation which is
 executed by SQLiteDatabase, execSQL method. After executing in first time deployment, this method will not be called onwards.

 onUpgrade This method is called when database version is upgraded.
 Suppose for the first time deployment , database version was 1 and in second deployment
 there was change in database structure like adding extra column in table.
  Suppose database version is 2 now. */