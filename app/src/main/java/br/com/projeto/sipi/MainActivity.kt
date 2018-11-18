package br.com.projeto.sipi

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import br.com.projeto.sipi.adapters.ProjetosAdapter
import br.com.projeto.sipi.utils.RecyclerItemClickListener
import br.com.projeto.sipi.viewmodel.ProjetoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    // recebe a instancia do banco de dados
    private lateinit var db : FirebaseFirestore
    // recebe a instancia de autenticação do firebase
    private lateinit var mAuth: FirebaseAuth
    // recebe a instancia do usuário autenticado do firebase
    private var mUser: FirebaseUser? = null

    // recebe a lista de projetos do usuario
    private var projetos : MutableList<ProjetoViewModel> = mutableListOf()

    private lateinit var recyclerView : RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // cria a instancia de autenticação do firebase
        mAuth = FirebaseAuth.getInstance()
        // busca o usuário autenticado atualmente
        mUser = mAuth.currentUser

        // se não possuir usário logado
        if (mUser == null) {
            // cria a intent para redirecionamento para a funcionalidade de login
            val intent = Intent(this, LoginActivity::class.java)
            // inicia a funcionalidade de login
            startActivity(intent)
            // finaliza esta funcionalidade
            finish()
        }

        // cria a instancia do banco de dados
        db = FirebaseFirestore.getInstance()

        // busca a recyclerview de projetos
        recyclerView = findViewById(R.id.mainRecyclerView)
        // busca o botão de criar projetos
        floatingActionButton = findViewById(R.id.mainFloatingActionButton)
        // busca a barra de progresso
        progressBar = findViewById(R.id.mainProgressBar)
        // busca a componente de carregamento
        swipeRefreshLayout = findViewById(R.id.mainSwipeRefreshLayout)

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        // instancia a forma como a lista irá ser exibida
        val linearLayoutManager = LinearLayoutManager(this)
        // atribui a orientação vertical a lista
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

        // adiciona as propriedades a lista
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.itemAnimator = DefaultItemAnimator()

        // atribui a função de click ao  botão de adicionar novo projeto
        floatingActionButton.setOnClickListener {
            // cria a intent de redirecionamento para a funcionalidade de criação de projeto
            val intent = Intent(this, NovoProjetoActivity::class.java)
            // inicia esta funcionalidade
            startActivity(intent)
        }

        // faz a chamada da função de busca de projetos deste usuário
        atualizar()

        // atribui a função de deslizar para baixo para atualizar a lista de projetos
        swipeRefreshLayout.setOnRefreshListener {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

            atualizar()
        }

        // adiciona a função de click para cada item da lista de projetos
        recyclerView.addOnItemTouchListener(RecyclerItemClickListener(this, recyclerView, object : RecyclerItemClickListener.OnItemClickListener {
            // ao fazer um click
            override fun onItemClick(view: View, position: Int) {
                // busca o projeto selecionado pela posição dele na lista de projetos
                val projeto = projetos[position]

                // cria a intent para a funcionalidade de chat do projeto selecionado
                val intent = Intent(this@MainActivity, ProjetoActivity::class.java)
                // manda o projeto selecionado para a funcionalidade de chat
                intent.putExtra("projeto", projeto)
                // inicializa o chat
                startActivity(intent)
            }

            // ao fazer um click longo
            override fun onLongItemClick(view: View, position: Int) {
                // nã faz nada
            }
        }))

    }

    // função responsavel por atualizar a lista de projetos
    private fun atualizar(){
        // busca aa coleção de projetos do banco
        db.collection("projetos").get().addOnCompleteListener {
            // se a busca ocorrer com sucesso
            if(it.isSuccessful){

                // limpa todos os projetos da lista de projetos
                projetos.clear()

                // para cada projeto achado
                for(document in it.result){
                    // busca a lista de usuários participantes do projeto
                    val usuarios = document.get("usuarios") as ArrayList<*>
                    // verifica se o usuário atual está nesta lista
                    if(usuarios.contains(mUser?.uid)){
                        // se estiver, recupera as informações necessárias do projeto
                        val nome = document.getString("projeto")
                        val situacao = document.getString("situacao")
                        val id = document.id

                        // adiciona o projeto a lista de projetos
                        projetos.add(ProjetoViewModel(nome!!, id, situacao!!))
                    }
                }

                // insttancia do adapter da lsita de projetos
                val adapter = ProjetosAdapter(this, projetos)
                recyclerView.adapter = adapter

                // se ainda estiver mostrando o loading
                if(swipeRefreshLayout.isRefreshing){
                    // remove ele
                    swipeRefreshLayout.isRefreshing = false
                }

                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

            }
        }
    }
}
