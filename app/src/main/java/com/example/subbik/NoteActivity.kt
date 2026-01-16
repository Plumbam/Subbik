package com.example.subbik

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    private lateinit var dbHelper: DbHelper
    private lateinit var currentLogin: String
    private lateinit var previewImage: ImageView
    private lateinit var iconSpinner: Spinner
    private var selectedIcon = "netflix" // по умолчанию

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        dbHelper = DbHelper(this, null)

        currentLogin = intent.getStringExtra("LOGIN_KEY") ?: run {
            Toast.makeText(this, "Ошибка: не передан логин пользователя", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initViews()
        setupSpinner()
        setupClickListeners()
    }

    private fun initViews() {
        previewImage = findViewById(R.id.previewImage)
        iconSpinner = findViewById(R.id.iconSpinner)
        findViewById<EditText>(R.id.etCost).setText("0.0")
        findViewById<EditText>(R.id.etDay).setText(getTodayDate())
        updatePreviewImage()
    }

    private fun setupSpinner() {
        // Список доступных иконок
        val icons = arrayOf("netflix", "okko", "default_icon", "note_icon")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, icons)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        iconSpinner.adapter = adapter

        iconSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedIcon = icons[position]
                updatePreviewImage()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updatePreviewImage() {
        val imageId = resources.getIdentifier(
            selectedIcon,
            "drawable",
            packageName
        )
        previewImage.setImageResource(
            if (imageId != 0) imageId else android.R.drawable.ic_menu_gallery
        )
    }

    private fun setupClickListeners() {
        val etCost = findViewById<EditText>(R.id.etCost)
        val etName = findViewById<EditText>(R.id.etName)
        val etDay = findViewById<EditText>(R.id.etDay)

        val btnCancel = findViewById<Button>(R.id.btnCancel)
        val btnCreate = findViewById<Button>(R.id.btnCreate)



        btnCancel.setOnClickListener {
            finish()
        }

        btnCreate.setOnClickListener {
            val costStr = etCost.text.toString().trim()
            val name = etName.text.toString().trim()
            val day = etDay.text.toString().trim()

            if (name.isEmpty() || costStr.isEmpty() || day.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val cost = try {
                costStr.toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Неверный формат стоимости", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (!isValidDate(day)) {
                Toast.makeText(this, "Неверный формат даты. Используйте: ГГГГ-ММ-ДД", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val note = Note(
                userLogin = currentLogin,
                cost = cost,
                name = name,
                day = day,
                photoPath = null
            )
            dbHelper.addNote(note)

            val resultIntent = Intent()
            resultIntent.putExtra("ITEM_ID", System.currentTimeMillis().toInt())
            resultIntent.putExtra("ITEM_NAME", name)
            resultIntent.putExtra("ITEM_COST", costStr)
            resultIntent.putExtra("ITEM_DAY", day)
            resultIntent.putExtra("ITEM_IMAGE", selectedIcon) // ✅ Передаем выбранную иконку

            setResult(RESULT_OK, resultIntent)
            Toast.makeText(this, "Заметка '$name' добавлена в список!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun isValidDate(dateStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(dateStr)
            true
        } catch (e: Exception) {
            false
        }
    }
}
