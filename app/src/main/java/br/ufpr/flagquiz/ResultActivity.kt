package br.ufpr.flagquiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("username") ?: "Jogador"
        val totalScore = intent.getIntExtra("score", 0)
        val answers = intent.getParcelableArrayListExtra<FlagQuestion>("answers") ?: arrayListOf()

        val title = findViewById<TextView>(R.id.resultTitle)
        val scoreText = findViewById<TextView>(R.id.scoreText)
        val list = findViewById<RecyclerView>(R.id.answersList)
        val playAgain = findViewById<Button>(R.id.playAgain)

        scoreText.text = "Sua pontuação: $totalScore"


        title.text = if (totalScore > 0) "Parabéns, $username!" else "Que pena 😥, $username!"

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = ResultAdapter(answers)

        playAgain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}

private class ResultAdapter(private val items: List<FlagQuestion>) : RecyclerView.Adapter<ResultViewHolder>() {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ResultViewHolder {
        val view = layoutInflater(parent).inflate(R.layout.item_answer, parent, false)
        return ResultViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        holder.bind(items[position])
    }

    private fun layoutInflater(parent: android.view.ViewGroup): android.view.LayoutInflater =
        android.view.LayoutInflater.from(parent.context)
}

private class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val countryText: TextView = itemView.findViewById(R.id.countryText)
    private val userAnswerText: TextView = itemView.findViewById(R.id.userAnswerText)
    private val correctnessText: TextView = itemView.findViewById(R.id.correctnessText)

    fun bind(item: FlagQuestion) {
        countryText.text = item.countryName.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        userAnswerText.text = if (item.userAnswer.isBlank()) "—" else item.userAnswer
        correctnessText.text = if (item.isCorrect) "Correto" else "Correto: ${item.countryName}"
        correctnessText.setTextColor(
            if (item.isCorrect) 0xFF2E7D32.toInt() else 0xFFC62828.toInt()
        )
    }
}
