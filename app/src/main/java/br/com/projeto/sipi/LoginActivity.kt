package br.com.projeto.sipi

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.projeto.sipi.fragments.CodigoFragment
import br.com.projeto.sipi.fragments.NomeFragment
import br.com.projeto.sipi.fragments.NumeroFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity(), NumeroFragment.NumeroFragmentListener, CodigoFragment.CodigoFragmentListener, NomeFragment.NomeFragmentListener {

    // recebe a instancia de autenticação do firebase
    private lateinit var mAuth: FirebaseAuth
    // recebe a instancia do usuário autenticado do firebase
    private var mUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // cria a instancia de autenticação do firebase
        mAuth = FirebaseAuth.getInstance()
        // busca o usuário autenticado atualmente
        mUser = mAuth.currentUser

        // se não possuir usário logado
        if (mUser == null) {
            // busca o fragmento de informação de número para autenticação
            val numeroFragment = supportFragmentManager.findFragmentByTag(NumeroFragment.TAG)
            // se o fragmento não estiver instanciado
            if (numeroFragment == null) {
                // abre a transação de fragmentos
                val transaction = supportFragmentManager.beginTransaction()
                // instancia o fragmento de informação de número para autenticação
                transaction.replace(R.id.loginFrameLayout, NumeroFragment.newInstance(), NumeroFragment.TAG)
                // faz o commit das alterações
                transaction.commit()
            }
        }

    }

    // função chamada a partir do fragmento de numero para o envio de codigo de autenticação
    override fun onNumeroValido(numero: String) {

        // formata o numero informado
        val numeroformatado = "+55$numero"

        // abre a transação de fragmentos
        val transaction = supportFragmentManager.beginTransaction()
        // instancia o fragmento de informação do codigo de autenticação
        transaction.replace(R.id.loginFrameLayout, CodigoFragment.newInstance(numeroformatado), CodigoFragment.TAG)
        // faz o commit das alterações
        transaction.commit()

    }

    // função chamada a partir do framgneto de informação de codigo de autenticação, para autenticar o usuário
    override fun onCodigoVerificado(credential: PhoneAuthCredential, numero: String) {
        // tenta fazer o login do usuário com as credenciais informadas
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            // se conseguir
            if (task.isSuccessful) {
                // abre a transação de fragmentos
                val transaction = supportFragmentManager.beginTransaction()
                // instancia o fragmento de informação do nome de contato
                transaction.replace(R.id.loginFrameLayout, NomeFragment.newInstance(numero), NomeFragment.TAG)
                // faz o commit das alterações
                transaction.commit()
            }
        }
    }

    // funação chamada a partir do fragmento de informação de nome de contato, para atualizar as informações do usuario
    // e redirecionar para a tela principal
    override fun onTerminoLogin(nome: String, numero: String) {
        // busca o usuario logado
        val mUser: FirebaseUser? = mAuth.currentUser

        // se possuir
        if (mUser != null) {
            // instancia o helper para atualização de perfil, passando o nome do usuário
            val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build()

            // atualiza o perfil, e faz a chamada da função ao terminar a tarefa
            mUser.updateProfile(profileUpdate)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            // cria a instancia do banco de dados
                            val db: FirebaseFirestore = FirebaseFirestore.getInstance()

                            // cria um map com nome e numero do usuario
                            val usuario = hashMapOf("nome" to nome, "numero" to numero)
                            val snapshot: Map<String, String> = HashMap(usuario)

                            // adiciona as informações do usuário a coleção de usuários no banco
                            db.collection("usuarios").document(mUser.uid).set(snapshot)
                                    .addOnCompleteListener {
                                        // se ocorer com sucesso
                                        if (task.isSuccessful) {
                                            // cria a intent para redirecionamento para a funcionalidade principal
                                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                            // instancia esta funcionalidade
                                            startActivity(intent)
                                            // finaliza a funcionalidade atual
                                            finish()
                                        }
                                    }

                        }
                    }
        }

    }

}
