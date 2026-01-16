package com.example.subbik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper // Объявляем как поле класса

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализируем dbHelper один раз
        dbHelper = DbHelper(this, null)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userEmail: EditText = findViewById(R.id.user_email)
        val userPass: EditText = findViewById(R.id.user_pass)
        val userRpass: EditText = findViewById(R.id.user_rpass)
        val button: Button = findViewById(R.id.button_reg)
        val linkToAuth: TextView = findViewById(R.id.link_to_auth)



        linkToAuth.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()
            val rpass = userRpass.text.toString().trim()

            if (login.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (pass != rpass) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show()
                userPass.text.clear()
                userRpass.text.clear()
                return@setOnClickListener
            }

            // Проверяем, существует ли пользователь с таким логином
            if (dbHelper.userExists(login)) {
                Toast.makeText(this, "Пользователь с логином '$login' уже существует", Toast.LENGTH_LONG).show()
            } else {
                val user = User(login, email, pass)
                dbHelper.addUser(user)
                Toast.makeText(this, "Пользователь $login успешно зарегистрирован", Toast.LENGTH_LONG).show()

                // Очищаем поля
                userLogin.text.clear()
                userEmail.text.clear()
                userPass.text.clear()
                userRpass.text.clear()
            }
        }
    }
}