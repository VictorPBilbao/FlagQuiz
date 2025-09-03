package br.ufpr.flagquiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun StartGame(view: View) {
        val usernameInput = findViewById<EditText>(R.id.username)
        val username = usernameInput.text?.toString()?.trim() ?: ""

        if (username.isBlank()) {
            usernameInput.error = "Informe seu nome"
            usernameInput.requestFocus()
            return
        }

        val intent = Intent(this, Game::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }
}