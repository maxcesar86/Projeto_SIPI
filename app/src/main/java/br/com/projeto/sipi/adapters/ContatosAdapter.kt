package br.com.projeto.sipi.adapters

import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import br.com.projeto.sipi.R
import br.com.projeto.sipi.viewmodel.ContatosViewModel

class ContatosAdapter(var fragment: Fragment, var items: List<ContatosViewModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_contato, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val viewHolder = holder as ViewHolder

        viewHolder.nome.text = item.nome
        viewHolder.numero.text = item.numero

        viewHolder.checkbox.setOnClickListener {
            item.checked = viewHolder.checkbox.isChecked
            Toast.makeText(fragment.context, item.checked.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nome: TextView = itemView.findViewById(R.id.contatoItemTextViewNome)
        var numero: TextView = itemView.findViewById(R.id.contatoItemTextViewNumero)
        var checkbox : CheckBox = itemView.findViewById(R.id.contatoItemCheckbox)
    }

}