package br.com.projeto.sipi.adapters

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.projeto.sipi.R
import br.com.projeto.sipi.viewmodel.MensagensViewModel
import java.text.DateFormat
import java.util.*

class ChatAdapter(var fragment: Fragment, var userId: String, var items: MutableList<MensagensViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        val viewHolder: RecyclerView.ViewHolder
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ENVIADA -> {
                view = inflater.inflate(R.layout.layout_mensagem_enviada, parent, false)
                viewHolder = EnviadaViewHolder(view)
            }
            else -> {
                view = inflater.inflate(R.layout.layout_mensagem_recebida, parent, false)
                viewHolder = RecebidaViewHolder(view)
            }
        }

        return viewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ENVIADA -> {
                val item = items[position]
                val viewHolder = holder as EnviadaViewHolder

                viewHolder.mensagem.text = item.mensagem

                val time = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(item.timestamp)

                viewHolder.horario.text = time

            }
            RECEBIDA -> {
                val item = items[position]
                val viewHolder = holder as RecebidaViewHolder

                viewHolder.nome.text = item.nome
                viewHolder.mensagem.text = item.mensagem

                val time = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).format(item.timestamp)

                viewHolder.horario.text = time

            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item.userId == userId) {
            ENVIADA
        } else {
            RECEBIDA
        }
    }


    class RecebidaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = itemView.findViewById<TextView>(R.id.mensagemRecebidaTextViewNome)
        val mensagem = itemView.findViewById<TextView>(R.id.mensagemRecebidaTextViewMensagem)
        val horario = itemView.findViewById<TextView>(R.id.mensagemRecebidaTextViewHorario)
    }

    class EnviadaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mensagem = itemView.findViewById<TextView>(R.id.mensagemEnviadaTextViewMensagem)
        val horario = itemView.findViewById<TextView>(R.id.mensagemEnviadaTextViewHorario)
    }

    companion object {
        private const val ENVIADA = 0
        private const val RECEBIDA = 1
    }

}