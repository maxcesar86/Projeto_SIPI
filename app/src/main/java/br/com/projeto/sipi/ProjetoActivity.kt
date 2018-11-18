package br.com.projeto.sipi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.projeto.sipi.fragments.ChatFragment
import br.com.projeto.sipi.fragments.SituacaoDialogFragment
import br.com.projeto.sipi.viewmodel.ProjetoViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class ProjetoActivity : AppCompatActivity(), SituacaoDialogFragment.SituacaDialogFragmentListener {

    // recebe a instancia de autenticação do firebase
    private lateinit var mAuth: FirebaseAuth
    // recebe a instancia do usuário autenticado do firebase
    private var mUser: FirebaseUser? = null
    // recebe a instancia do banco de dados
    private lateinit var db: FirebaseFirestore
    // recebe a instancia do projeto selecionado
    private var projeto: ProjetoViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_projeto)

        // adiciona o botão de voltar na toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // cria a instancia de autenticação do firebase
        mAuth = FirebaseAuth.getInstance()
        // busca o usuário autenticado atualmente
        mUser = mAuth.currentUser
        // cria a instancia do banco de dados
        db = FirebaseFirestore.getInstance()

        // busca o projeto selecionado
        projeto = intent.getParcelableExtra("projeto")

        // atribui o titulo da funcionalidade
        title = "${projeto!!.nome} - ${projeto!!.situacao}"

        // se não possuir instancias de estados salvas
        if (savedInstanceState == null) {
            // busca instancia do framento de chat
            val chatFragment = supportFragmentManager.findFragmentByTag(ChatFragment.TAG)
            // se não estiver instanciado
            if (chatFragment == null) {
                // abre a transação de fragmentos
                val transaction = supportFragmentManager.beginTransaction()
                // instancia o fragmento de chat, passando o projeto por parametro
                transaction.add(R.id.projetoFrameLayout, ChatFragment.newInstance(projeto!!), ChatFragment.TAG)
                // faz o commit das alterações
                transaction.commit()
            }
        }

    }

    // funação responsável por criar o menu de opções da funcionalidade
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // infla o menu da funcionalidade
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // função chamada ao selecionar um item de menu
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item!!.itemId) {
            // se for chamado o menu de situações
            R.id.menuItemSituacao -> {
                // cria a instancia do framgmento de dialogo para seleção de situação
                val dialogFragment = SituacaoDialogFragment()
                // mostra o dialogo
                dialogFragment.show(supportFragmentManager, SituacaoDialogFragment.TAG)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }

    }

    // função chamada a partir do fragmento de dialogo de situações
    override fun onSelecionarSituacao(situacao: String) {
        // atualiza a situação do projeto atual
        db.collection("projetos").document(projeto!!.id).update("situacao", situacao)
                .addOnCompleteListener {
                    // se ocorrer com sucesso
                    if (it.isSuccessful) {
                        // altera o titulo do chat
                        title = "${projeto!!.nome} - $situacao"
                        // mostra mensagem de sucesso
                        Toast.makeText(this, "Situação do projeto atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    // se ocorrer algum erro
                    else
                    {
                        // mostra mensagem de erro
                        Toast.makeText(this, "Não foi possível atualizar a situação do projeto no momento. Por favor, tente mais tarde.", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    // função chamada ao pressionar o botão de voltar
    override fun onBackPressed() {
        // se possuir fragmentos anteriores
        if (supportFragmentManager.backStackEntryCount > 0) {
            // retorna para o fragmento anterior
            supportFragmentManager.popBackStack()
        }
        // se não possuir
        else
        {
            // retorna para a activity anterior
            super.onBackPressed()
        }
    }

    // função chamada ao pressionar o botão de voltar da toolbar
    override fun onSupportNavigateUp(): Boolean {
        // chama a função de voltar padrão
        onBackPressed()
        return true
    }

}
