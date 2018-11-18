package br.com.projeto.sipi

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import br.com.projeto.sipi.fragments.ContatosFragment
import br.com.projeto.sipi.fragments.InfoProjetoFragment
import br.com.projeto.sipi.viewmodel.ContatosViewModel
import br.com.projeto.sipi.viewmodel.ProjetoViewModel
import java.util.jar.Manifest

class NovoProjetoActivity : AppCompatActivity(), ContatosFragment.ContatosFragmentListener, InfoProjetoFragment.InfoProjetoFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_projeto)

        // adiciona o botão de voltar na toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // verifica se a aplicação possui a permissão para leitura de contatos
        val permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)

        // se a permissão for concedida
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // se não possuir instancias de estados salvas
            if (savedInstanceState == null) {
                // busca instancia do framento de contatos
                val contatosFragment = supportFragmentManager.findFragmentByTag(ContatosFragment.TAG)
                // se não estiver instanciado
                if (contatosFragment == null) {
                    // abre a transação de fragmentos
                    val transaction = supportFragmentManager.beginTransaction()
                    // instancia o fragmento de contatos
                    transaction.add(R.id.novoProjetoFrameLayout, ContatosFragment.newInstance(), ContatosFragment.TAG)
                    // faz o commit das alterações
                    transaction.commit()
                }
            }
        }
        // se não possuir permissão
        else
        {
            // solicita a permissão, passando o codigo da permissão do fragmento de contatos
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CONTACTS), ContatosFragment.PERMISSIONS_REQUEST_READ_CONTACTS)
        }

    }

    // função chamada ao termino da solicitação de permissão
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            // se o codigo de permissão for do fragmento de contatos
            ContatosFragment.PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // se possuir alguma permissão e ela for garantida
                if (grantResults.count() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // busca instancia do framento de contatos
                    val contatosFragment = supportFragmentManager.findFragmentByTag(ContatosFragment.TAG)
                    // se não estiver instanciado
                    if (contatosFragment == null) {
                        // abre a transação de fragmentos
                        val transaction = supportFragmentManager.beginTransaction()
                        // instancia o fragmento de contatos
                        transaction.add(R.id.novoProjetoFrameLayout, ContatosFragment.newInstance(), ContatosFragment.TAG)
                        // faz o commit das alterações
                        transaction.commit()
                    }
                }
                // se a permissão for negada
                else
                {
                    // finaliza a funcionalidade atual
                    finish()
                }
            }
        }
    }

    // função chamada ao seleecionar os contatos
    override fun onCompleteSelecionarContatos(contatos: List<ContatosViewModel>) {
        // abre a transação de fragmentos
        val transaction = supportFragmentManager.beginTransaction()
        // instancia o fragmento de informações do projeto, passando os contatos por parametro
        transaction.replace(R.id.novoProjetoFrameLayout, InfoProjetoFragment.newInstance(contatos), InfoProjetoFragment.TAG).addToBackStack(null)
        // faz o commit das alterações
        transaction.commit()
    }

    // função chamada ao finalizar a criação do projeto
    override fun onCompleteCriacaoProjeto(projeto: ProjetoViewModel) {
        // cria a intent para redirecionamento para a funcionalidade de chat
        val intent = Intent(this, ProjetoActivity::class.java)
        // passa o projeto por parametros
        intent.putExtra("projeto", projeto)
        // inicializa esta funcionalidade
        startActivity(intent)
        // finaliza a funcionalidade atual
        finish()
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
