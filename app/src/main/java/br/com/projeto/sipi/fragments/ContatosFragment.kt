package br.com.projeto.sipi.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Toast
import br.com.projeto.sipi.NovoProjetoActivity
import br.com.projeto.sipi.R
import br.com.projeto.sipi.adapters.ContatosAdapter
import br.com.projeto.sipi.utils.RecyclerItemClickListener
import br.com.projeto.sipi.viewmodel.ContatosViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ContatosFragment : Fragment() {

    // recebe a instancia do banco de dados
    private lateinit var db: FirebaseFirestore
    // recebe  instancia a lista de contatos
    private lateinit var recyclerView: RecyclerView
    // recebe a instancia do botão de prosseguir
    private lateinit var floatingActionButton: FloatingActionButton
    // recebee a instancia da componente de carregamento
    private lateinit var progressBar: ProgressBar
    // recebe a instacia da interface do fragmento
    private var mListener: ContatosFragmentListener? = null
    // recebe a instancia lista de contatos
    private var listaContatos: MutableList<ContatosViewModel> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contatos, container, false)

        // cria a instancia do banco de dados
        db = FirebaseFirestore.getInstance()

        // adiciona o titulo a funcionalidade
        activity?.title = getString(R.string.title_contatos)

        progressBar = view.findViewById(R.id.contatosProgressBar)
        progressBar.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.contatosRecyclerView)
        recyclerView.visibility = View.GONE

        floatingActionButton = view.findViewById(R.id.contatosFloatingActionButton)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

        // recebe o componente de aplicação
        val contentResolver = context!!.contentResolver
        // instancia o cursor para busca de contatos do telefone
        val contatos = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        // se possuir contatos
        if (contatos.count > 0) {
            // para cada um
            while (contatos!!.moveToNext()) {
                // busca nome do contato
                val nome = contatos.getString(contatos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                // busca numero do contato
                var numero = contatos.getString(contatos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                // formata o numero
                val regex = Regex("[^A-Za-z0-9]")
                numero = regex.replace(numero, "")

                val codigo = numero.substring(0, 2)
                // se ja possuir o codigo 55
                if (codigo == "55") {
                    // cria contato
                    val contato = ContatosViewModel(nome, "+$numero")
                    // adiciona a lista
                    listaContatos.add(contato)
                } else {
                    if (numero.length == 11) {
                        // cria contato
                        val contato = ContatosViewModel(nome, "+55$numero")
                        // adiciona a lista
                        listaContatos.add(contato)
                    }
                }
            }

            // fecha o cursor
            contatos.close()

            // busca a coleção de usuarios do banco
            val docRef = db.collection("usuarios")

            // faz a busca para cada usuario
            docRef.get().addOnCompleteListener {
                if (it.isSuccessful) {

                    // instancia lista auxiliar de contatos
                    val listaAux = mutableListOf<ContatosViewModel>()

                    // para cada usuario
                    for (document in it.result) {
                        // busca nome
                        val nome = document.getString("nome")
                        // numero
                        val numero = document.getString("numero")
                        // cria instancia de contato
                        val contato = ContatosViewModel(nome!!, numero!!)

                        // adiciona a lista auxiliar
                        listaAux.add(contato)
                    }

                    // filtra somente os contatos da agenda que possuam cadastro no sistema
                    listaContatos = listaContatos.filter { listaAux.map { it.numero }.contains(it.numero) }.distinctBy { it.numero }.sortedBy { it.nome }.toMutableList()

                    // instancia o adapter da lista
                    val adapter = ContatosAdapter(this, listaContatos)
                    // adiciona o adapter a lista
                    recyclerView.adapter = adapter

                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE

                }
            }
        }

        // adiciona função de click a lista
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(context!!, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                // busca o contato a partir da posição
                val contato = listaContatos[position]
                // busca a checbox
                val checkbox: CheckBox = view.findViewById(R.id.contatoItemCheckbox)

                // muda a situação do contato, para selecionado ou não
                when {
                    contato.checked -> {
                        contato.checked = false
                        checkbox.isChecked = false
                    }
                    else -> {
                        contato.checked = true
                        checkbox.isChecked = true
                    }
                }

            }

            override fun onLongItemClick(view: View, position: Int) {

            }
        }))

        // ao clicar no botão para prosseguir
        floatingActionButton.setOnClickListener {
            // busca todos os contatos selecionados
            val contatosSelecionados = listaContatos.filter { it.checked }

            // se possuir algum
            if (contatosSelecionados.count() > 0) {
                // chama função para prosseguir no cadastro
                mListener?.onCompleteSelecionarContatos(contatosSelecionados)
            }
            // se não possuir nenhum
            else
            {
                // mostra erro
                Toast.makeText(context!!, getString(R.string.error_selecao_contato), Toast.LENGTH_LONG).show()
            }

        }

        return view
    }

    // interface do fragmento
    interface ContatosFragmentListener {
        // função chamada ao selecionar os contatos
        fun onCompleteSelecionarContatos(contatos: List<ContatosViewModel>)
    }

    // função responsavel pela comunicação do fragmento com a activity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is NovoProjetoActivity) {
            mListener = context
        }
    }

    companion object {
        // tag de idenficação do fragmento
        const val TAG = "FRAGMENT_CONTATO"
        // codigo de permissão de acesso ao contato
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100

        fun newInstance(): ContatosFragment {
            return ContatosFragment()
        }

    }

}