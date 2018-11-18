package br.com.projeto.sipi.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.projeto.sipi.R
import br.com.projeto.sipi.viewmodel.ProjetoViewModel

class ProjetosAdapter(var context: Context, var items: List<ProjetoViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_projeto, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val viewHolder = holder as ViewHolder

        viewHolder.projeto.text = "${item.nome} - ${item.situacao}"

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var projeto: TextView = itemView.findViewById(R.id.projetoItemTextViewProjeto)
    }

}