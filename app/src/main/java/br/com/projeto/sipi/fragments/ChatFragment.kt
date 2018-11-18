package br.com.projeto.sipi.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import br.com.projeto.sipi.R
import br.com.projeto.sipi.adapters.ChatAdapter
import br.com.projeto.sipi.viewmodel.MensagensViewModel
import br.com.projeto.sipi.viewmodel.ProjetoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class ChatFragment : Fragment() {

    // recebe  instancia a lista de contatos
    private lateinit var recyclerView: RecyclerView
    // recebe a instancia do campo de mensagens
    private lateinit var editText: EditText
    // recebe a instancia do botão de enviar
    private lateinit var imageButton: ImageButton

    // recebe a instancia de autenticação do firebase
    private lateinit var mAuth: FirebaseAuth
    // recebe a instancia do usuário autenticado do firebase
    private var mUser: FirebaseUser? = null
    // recebe a instancia do banco de dados
    private lateinit var db: FirebaseFirestore

    // recebe a isntancia da lista de mensagens
    private var mensagens: MutableList<MensagensViewModel> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        // cria a instancia do banco de dados
        db = FirebaseFirestore.getInstance()

        // busca as informações do projeto do argumento
        val projeto = arguments?.getParcelable<ProjetoViewModel>("projeto")

        // cria a instancia de autenticação do firebase
        mAuth = FirebaseAuth.getInstance()
        // busca o usuário autenticado atualmente
        mUser = mAuth.currentUser

        recyclerView = view.findViewById(R.id.chatRecyclerView)
        editText = view.findViewById(R.id.chatEditTextMensagem)
        imageButton = view.findViewById(R.id.chatImageButtonEnviar)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.stackFromEnd = true

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)

        // instancia o adapter do chat passando o id do usuarios e as mensagens
        val adapter = ChatAdapter(this, mUser!!.uid, mensagens)
        // adiciona o adapter a lista
        recyclerView.adapter = adapter

        // buscca no banco de dados, as mensagens do projeto ordenadas por data/hora
        db.collection("projetos").document(projeto!!.id).collection("mensagens").orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { documentSnapshot, _ ->

                    // se possuir dados
                    if (documentSnapshot != null) {
                        // para cada mensagem
                        for (item in documentSnapshot.documentChanges) {
                            // instancia o objeto de mensagem
                            val mensagem = item.document.toObject(MensagensViewModel::class.java)
                            // se a mensagem ainda não estiver na lista de mensagens
                            if (!mensagens.any { m -> m.documento == mensagem.documento }) {
                                // adiciona
                                this.mensagens.add(mensagem)
                            }
                        }
                        // move a lista para a ultima posição
                        recyclerView.smoothScrollToPosition(recyclerView.adapter.itemCount)
                        // atualiza a lista
                        adapter.notifyDataSetChanged()
                    }
                }

        // ao clicar no botão de enviar mensagem
        imageButton.setOnClickListener {
            // se o campo de texto não estiver vazio
            if (!editText.text.isNullOrEmpty()) {

                // cria o objeto de mensagens
                val mensagem = MensagensViewModel(mUser!!.uid, mUser!!.displayName.toString(), Date(), editText.text.toString(), "mensagem${mensagens.count()}")

                // adiciona a mensagem ao banco de dados
                db.collection("projetos").document(projeto.id)
                        .collection("mensagens")
                        .document("mensagem${mensagens.count()}")
                        .set(mensagem)

                // adiciona a mensagem a lista de mensagens
                mensagens.add(mensagem)

                // atuliaza a lista
                adapter.notifyDataSetChanged()

                // limpa o campo
                editText.text.clear()

            }
        }

        return view
    }

    companion object {
        // tag de identificação do fragmento
        const val TAG = "FRAGMENT_CHAT"

        // função chamada ao instancia o fragmento a partir da activity
        fun newInstance(projeto: ProjetoViewModel): ChatFragment {
            // cria a instacia do fragmento
            val fragment = ChatFragment()

            // instancia o objeto de argumentos
            val args = Bundle()
            // adiciona o projeto ao argumento
            args.putParcelable("projeto", projeto)

            // adiciona os argumetos ao fragmento
            fragment.arguments = args

            // retorna o fragmento
            return fragment
        }

    }

}