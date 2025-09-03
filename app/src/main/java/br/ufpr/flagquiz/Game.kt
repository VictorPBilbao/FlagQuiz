package br.ufpr.flagquiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import java.text.Normalizer

class Game : AppCompatActivity() {

    // Lista embaralhada com 5 perguntas e índice atual
    private lateinit var selectedQuestions: List<FlagQuestion>
    private var currentIndex: Int = 0

    // Referências de UI
    private lateinit var imgFlag: ImageView
    private lateinit var answerInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle = intent.extras
        if (bundle != null) {
            val username = bundle.getString("username")
            Log.v("username", username.toString())
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_game)

        // Views
        imgFlag = findViewById(R.id.bandeiraAtual)
        answerInput = findViewById(R.id.answer)

        // Banco de perguntas (nomes devem bater com drawables em res/drawable)
        val questions: List<FlagQuestion> = listOf(
            FlagQuestion("brasil"),
            FlagQuestion("bolivia"),
            FlagQuestion("argentina"),
            FlagQuestion("paraguai"),
            FlagQuestion("uruguai"),
            FlagQuestion("chile"),
            FlagQuestion("peru"),
            FlagQuestion("venezuela"),
            FlagQuestion("colombia"),
            FlagQuestion("mexico"),
            FlagQuestion("albania")
        )

        // Seleciona 5 aleatórias e mostra a primeira ao entrar na tela
        selectedQuestions = questions.shuffled().take(5)
        currentIndex = 0
        showCurrentFlag()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun submitAnswer(view: View) {
        if (!::selectedQuestions.isInitialized) return
        if (currentIndex >= selectedQuestions.size) {
            Toast.makeText(this, "Fim do quiz", Toast.LENGTH_SHORT).show()
            return
        }

        val currentQuestion = selectedQuestions[currentIndex]
        val guessRaw = (answerInput.text?.toString() ?: "").trim()
        if (guessRaw.isBlank()) {
            answerInput.error = "Digite o nome do país"
            answerInput.requestFocus()
            return
        }

        val guess = normalize(guessRaw)
        val correct = normalize(currentQuestion.countryName)

        // Guarda resposta e verifica
    currentQuestion.userAnswer = guessRaw
    currentQuestion.isCorrect = guess == correct

        // Feedback simples (opcional)
        val msg = if (currentQuestion.isCorrect) "Correto!" else "Errado: ${currentQuestion.countryName}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

        // Próxima pergunta
        currentIndex++
        answerInput.setText("")

        if (currentIndex < selectedQuestions.size) {
            showCurrentFlag()
        } else {
            // Terminou as 5 perguntas -> calcula pontuação e abre tela de resultado
            val score = selectedQuestions.count { it.isCorrect } * 20
            val username = intent.extras?.getString("username") ?: "Jogador"
            val resultIntent = Intent(this, ResultActivity::class.java)
            resultIntent.putExtra("username", username)
            resultIntent.putExtra("score", score)
            resultIntent.putParcelableArrayListExtra(
                "answers",
                ArrayList(selectedQuestions)
            )
            startActivity(resultIntent)
            finish()
        }
    }

    private fun showCurrentFlag() {
        if (currentIndex !in selectedQuestions.indices) return
        val imageName = selectedQuestions[currentIndex].countryName
        val resId = resources.getIdentifier(imageName, "drawable", packageName)
        if (resId != 0) {
            imgFlag.setImageResource(resId)
        } else {
            Log.e("GameActivity", "Drawable not found for: $imageName")
        }
    }

    private fun normalize(input: String): String {
        // Remove acentos/diacríticos e normaliza para comparação
        val temp = Normalizer.normalize(input.lowercase(), Normalizer.Form.NFD)
        return temp.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }
}