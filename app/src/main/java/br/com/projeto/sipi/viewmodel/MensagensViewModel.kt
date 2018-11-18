package br.com.projeto.sipi.viewmodel

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class MensagensViewModel(var userId: String = "", var nome: String = "", @ServerTimestamp var timestamp: Date? = null, var mensagem: String = "", var documento: String = "")