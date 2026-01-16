package com.example.subbik

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(val context: Context, val factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "app", factory, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // Таблица пользователей
        db!!.execSQL(
            "CREATE TABLE users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "login TEXT UNIQUE NOT NULL, " +
                    "email TEXT, " +
                    "pass TEXT)"
        )

        // Таблица заметок
        db.execSQL(
            "CREATE TABLE notes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "userLogin TEXT NOT NULL, " +
                    "photoPath TEXT, " +
                    "cost REAL, " +
                    "name TEXT, " +
                    "day TEXT, " +
                    "FOREIGN KEY(userLogin) REFERENCES users(login) ON DELETE CASCADE)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS notes")
        db.execSQL("DROP TABLE IF EXISTS users")
        onCreate(db)
    }

    // === USERS ===
    fun clearDatabase() {
        this.close()
        val dbFile = context.getDatabasePath("app")
        if (dbFile.exists()) {
            dbFile.delete()
        }
    }

    // ✅ НОВАЯ ФУНКЦИЯ - очищает только заметки пользователя
    fun clearUserNotes(login: String) {
        val db = this.writableDatabase
        db.delete("notes", "userLogin = ?", arrayOf(login))
        db.close()
    }

    // ✅ НОВАЯ ФУНКЦИЯ - очищает ВСЕ заметки
    fun clearAllNotes() {
        val db = this.writableDatabase
        db.delete("notes", null, null)
        db.close()
    }

    fun userExists(login: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM users WHERE login = ?", arrayOf(login))
        val exists = if (cursor.moveToFirst()) cursor.getInt(0) > 0 else false
        cursor.close()
        db.close()
        return exists
    }

    fun addUser(user: User) {
        val values = ContentValues().apply {
            put("login", user.login)
            put("email", user.email)
            put("pass", user.pass)
        }
        val db = this.writableDatabase
        db.insert("users", null, values)
        db.close()
    }

    fun getUser(login: String, pass: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE login = ? AND pass = ?", arrayOf(login, pass))
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }

    // === NOTES ===
    fun addNote(note: Note) {
        val values = ContentValues().apply {
            put("userLogin", note.userLogin)
            put("photoPath", note.photoPath)
            put("cost", note.cost)
            put("name", note.name)
            put("day", note.day)
        }
        val db = this.writableDatabase
        db.insert("notes", null, values)
        db.close()
    }

    fun getNotesByUserLogin(login: String): List<Note> {
        val db = this.readableDatabase
        val cursor = db.query(
            "notes",
            arrayOf("id", "userLogin", "photoPath", "cost", "name", "day"),
            "userLogin = ?",
            arrayOf(login),
            null,
            null,
            "day DESC"
        )

        val notes = mutableListOf<Note>()
        while (cursor.moveToNext()) {
            notes.add(
                Note(
                    id = cursor.getLong(0),
                    userLogin = cursor.getString(1),
                    photoPath = cursor.getString(2),
                    cost = cursor.getDouble(3),
                    name = cursor.getString(4),
                    day = cursor.getString(5)
                )
            )
        }
        cursor.close()
        db.close()
        return notes
    }

    fun deleteNote(id: Long) {
        val db = this.writableDatabase
        db.delete("notes", "id = ?", arrayOf(id.toString()))
        db.close()
    }
}
