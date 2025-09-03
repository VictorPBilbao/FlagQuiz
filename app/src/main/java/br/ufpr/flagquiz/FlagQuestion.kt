package br.ufpr.flagquiz

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FlagQuestion(
    val countryName: String,
    var userAnswer: String = "",
    var isCorrect: Boolean = false
) : Parcelable