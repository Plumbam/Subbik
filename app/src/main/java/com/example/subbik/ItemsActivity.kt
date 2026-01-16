package com.example.subbik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemsActivity : AppCompatActivity() {
    private lateinit var currentLogin: String
    private lateinit var itemsList: RecyclerView
    private lateinit var dbHelper: DbHelper
    private var items = arrayListOf<Item>()

    private val addNoteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val id = data?.getIntExtra("ITEM_ID", 0) ?: 0
            val name = data?.getStringExtra("ITEM_NAME") ?: ""
            val cost = data?.getStringExtra("ITEM_COST") ?: ""
            val day = data?.getStringExtra("ITEM_DAY")
            val image = data?.getStringExtra("ITEM_IMAGE") ?: "netflix"

            if (id > 0) {
                val newItem = Item(id, name, cost, day, image = image)
                items.add(0, newItem)
                itemsList.adapter?.notifyItemInserted(0)
                itemsList.scrollToPosition(0)
                loadNotesFromDatabase()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_items)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DbHelper(this, null)
        currentLogin = intent.getStringExtra("LOGIN_KEY") ?: run {
            finish()
            return
        }

        setupRecyclerView()
        loadNotesFromDatabase()
        setupClickListeners()
    }

    private fun loadNotesFromDatabase() {
        val notes = dbHelper.getNotesByUserLogin(currentLogin)
        items.clear()

        notes.forEach { note ->
            items.add(
                Item(
                    id = note.id?.toInt() ?: 0,
                    name = note.name,
                    cost = note.cost.toString(),
                    day = note.day,
                    image = "netflix"
                )
            )
        }

        itemsList.adapter?.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        itemsList = findViewById(R.id.itemList)
        itemsList.layoutManager = LinearLayoutManager(this)
        itemsList.adapter = ItemsAdapter(items, this)
    }

    private fun setupClickListeners() {
        // ✅ Кнопка НАЗАД в MainActivity
        val buttonLeft: Button = findViewById(R.id.buttonLeft)
        buttonLeft.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Закрываем ItemsActivity
        }

        // ✅ Кнопка создания заметки
        val buttonRight: Button = findViewById(R.id.buttonRight)
        buttonRight.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra("LOGIN_KEY", currentLogin)
            addNoteLauncher.launch(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotesFromDatabase()
    }
}
