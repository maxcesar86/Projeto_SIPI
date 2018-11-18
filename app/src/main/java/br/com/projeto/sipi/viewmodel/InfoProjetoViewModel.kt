package br.com.projeto.sipi.viewmodel

import android.arch.lifecycle.ViewModel

class InfoProjetoViewModel(var nome : String = "", var situacao : Boolean? = null, var contatos : List<ContatosViewModel>) : ViewModel()