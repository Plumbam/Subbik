package com.example.subbik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var userLogin: EditText
    private lateinit var userPass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        userLogin = findViewById(R.id.user_login_auth)
        userPass = findViewById(R.id.user_pass_auth)
    }

    private fun setupClickListeners() {
        // Переход на регистрацию
        val linkToReg: TextView = findViewById(R.id.link_to_reg)
        linkToReg.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        // Авторизация → ItemsActivity с LOGIN_KEY
        val button: Button = findViewById(R.id.button_auth)
        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (login == "" || pass == "") {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val db = DbHelper(this, null)
            val isAuth = db.getUser(login, pass)

            if (isAuth) {
                Toast.makeText(this, "Пользователь $login авторизован", Toast.LENGTH_LONG).show()

                val intent = Intent(this, ItemsActivity::class.java)
                intent.putExtra("LOGIN_KEY", login)

                userLogin.text.clear()
                userPass.text.clear()

                startActivity(intent)
            } else {
                Toast.makeText(this, "Пользователь $login НЕ авторизован", Toast.LENGTH_LONG).show()
            }
        }
    }
}
