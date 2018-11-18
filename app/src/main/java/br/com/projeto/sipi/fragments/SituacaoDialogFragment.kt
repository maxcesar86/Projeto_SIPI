package br.com.projeto.sipi.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import br.com.projeto.sipi.ProjetoActivity
import br.com.projeto.sipi.R

class SituacaoDialogFragment : DialogFragment(){

    // cria instancia da interface deste fragmento
    private var mListener: SituacaDialogFragmentListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // inicializa a criação do dialogo
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)

        // atribui as informações do dialogo, passando o titulo e os itens
        builder.setTitle(R.string.title_situacao)
               .setItems(R.array.array_situacoes) { _, i ->
                   val array = resources.getStringArray(R.array.array_situacoes)
                   val situacao = array[i]
                   mListener!!.onSelecionarSituacao(situacao)
               }

        // cria o dialogo
        return builder.create()
    }


    // função responsavel pela comunicação do fragmento com a activity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is ProjetoActivity){
            mListener =  context
        }
    }

    // interface do fragmento
    interface SituacaDialogFragmentListener{
        // função chamada ao selecionar uma situação
        fun onSelecionarSituacao(situacao : String)
    }

    companion object {
        // tag identificadora do fragmento
        const val TAG = "SITUACAO_DIALOG_FRAGMENT"
    }

}