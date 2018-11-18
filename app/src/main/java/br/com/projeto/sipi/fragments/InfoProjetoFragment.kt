package br.com.projeto.sipi.fragments

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.projeto.sipi.NovoProjetoActivity
import br.com.projeto.sipi.R
import br.com.projeto.sipi.viewmodel.ContatosViewModel
import br.com.projeto.sipi.viewmodel.ProjetoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

class InfoProjetoFragment : Fragment() {

    // recebe a instancia de autenticação do firebase
    private lateinit var mAuth: FirebaseAuth
    // recebe a instancia do usuário autenticado do firebase
    private var mUser: FirebaseUser? = null
    // recebe a instancia do banco de dados
    private lateinit var db: FirebaseFirestore

    // recebe  instancia a lista
    private lateinit var listView: ListView
    // recebe a instancia a componente de loading
    private lateinit var progressBar: ProgressBar
    // recebe a instancia o botão de continuar
    private lateinit var floatingActionButton: FloatingActionButton

    //  instancia a interface do fragmento
    private var mListener : InfoProjetoFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_info_projeto, container, false)

        // cria a instancia de autenticação do firebase
        mAuth = FirebaseAuth.getInstance()
        // busca o usuário autenticado atualmente
        mUser = mAuth.currentUser
        // cria a instancia do banco de dados
        db = FirebaseFirestore.getInstance()

        // adiciona o titulo a funcionalidade
        activity?.title = getString(R.string.title_novo_projeto)

        // busca os contatos a partir do fragmento
        val contatos = arguments?.getParcelableArrayList<ContatosViewModel>("contatos")
        // busca o nome dos contatos em ordem alfabetica
        val nomeContatos = contatos!!.map { it.nome }.sortedBy { it }

        // instancia o contato do usuário logado
        val contato = ContatosViewModel(mUser?.displayName!!, mUser?.phoneNumber!!)

        // adiciona o contato do usuario logado a lista de contatos
        contatos.add(contato)

        // instancia o botão de continuar
        floatingActionButton = view.findViewById(R.id.infoProjetoFloatingActionButton)
        // instancia a componente de carregamento
        progressBar = view.findViewById(R.id.infoProjetoProgressBar)
        // instancia a lista
        listView = view.findViewById(R.id.infoProjetoListView)

        // cria uma lista de strings de usuarios
        val usuarios = mutableListOf<String>()

        // busca os usuarios do banco de dados
        db.collection("usuarios").get().addOnCompleteListener {
            if(it.isSuccessful){
                // para cada usuarios
                for (document in it.result){
                    // busca o numero do usuario
                    val numero = document.getString("numero")
                    // verifica se o usuario está na lista de contatos
                    if(contatos.map { it.numero }.contains(numero)){
                        // adiciona o id do usuario a lista de usuarios
                        usuarios.add(document.id)
                    }
                }
            }
        }

        // instancia o adapter da lista
        val adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, nomeContatos)

        // instancia um header a lista, para informar o nome e situação do contato
        val header = inflater.inflate(R.layout.layout_info_projeto_list_header, listView, false)

        // adiciona o adapter a lista
        listView.adapter = adapter
        // adiciona o header a lista
        listView.addHeaderView(header)

        // busca o campo de nome do projeto no header
        val textInputLayout = header.findViewById<TextInputLayout>(R.id.infoProjetoHeaderTextInputLayoutNome)
        // busca as opções da situação no header
        val radioGroup = header.findViewById<RadioGroup>(R.id.infoProjetoHeaderRadioGroup)

        // ao clicar no botão de prosseguir
        floatingActionButton.setOnClickListener {
            // busca o nome do projeot
            val nome = textInputLayout.editText?.text

            // se estiver vazio
            if (nome.isNullOrEmpty()) {
                // mostra mensagem de erro
                Toast.makeText(context!!, getText(R.string.error_nome_projeto), Toast.LENGTH_LONG).show()
            }
            // se preenchido
            else
            {
                // busca a situação selecionada
                val situacao = when {
                    radioGroup.checkedRadioButtonId == R.id.infoProjetoHeaderRadioButtonIniciarProgresso -> "Início"
                    radioGroup.checkedRadioButtonId == R.id.infoProjetoHeaderRadioButtonEmProgresso -> "Em progresso"
                    else -> "Concluído"
                }
                // cria um mapa com o nome do projeto, situação, adiministrador do projeto, e os usuarios participando
                val projeto = hashMapOf("projeto" to nome.toString(), "situacao" to situacao, "admin" to mUser!!.uid, "usuarios" to usuarios)

                // adiciona o projeto ao banco de dados
                db.collection("projetos").add(projeto).addOnCompleteListener {
                    if(it.isSuccessful){
                        // cria o objeto do projeto
                        val result = ProjetoViewModel(nome.toString(), it.result.id, situacao)
                        // chama função de finalização de criação de projeto
                        mListener?.onCompleteCriacaoProjeto(result)
                    }
                }
            }

        }

        return view
    }

    // interface do fragmento
    interface InfoProjetoFragmentListener {
        // função chamada ao criar o projeto
        fun onCompleteCriacaoProjeto(projeto : ProjetoViewModel)
    }

    // função responsavel pela comunicação do fragmento com a activity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is NovoProjetoActivity){
            mListener = context
        }
    }

    companion object {
        // tag identificadora do fragmento
        const val TAG = "FRAGMENT_INFO_PROJETO"

        // função chamada ao instancia o fragmento a partir da activity
        fun newInstance(contatos: List<ContatosViewModel>): InfoProjetoFragment {

            // cria a instacia do fragmento
            val fragment = InfoProjetoFragment()

            // instancia o objeto de argumentos
            val args = Bundle()
            // passa os contatos para o argumento
            args.putParcelableArrayList("contatos", contatos as ArrayList<out Parcelable>)

            // adiciona os argumetos ao fragmento
            fragment.arguments = args

            // retorna o fragmento
            return fragment
        }
    }

}