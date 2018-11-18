package br.com.projeto.sipi.fragments

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import br.com.projeto.sipi.LoginActivity
import br.com.projeto.sipi.R
import br.com.projeto.sipi.databinding.FragmentNomeBinding
import br.com.projeto.sipi.viewmodel.NomeViewModel
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate

class NomeFragment : Fragment() {

    // recebe o modelo
    private lateinit var mModel: NomeViewModel
    // recebe a interface do fragmento
    private lateinit var mListener: NomeFragmentListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // instancia o layout do fragmento
        val binding: FragmentNomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_nome, container, false)
        // instancia o modelo do fragmento
        mModel = ViewModelProviders.of(this).get(NomeViewModel::class.java)

        // adiciona o vinculo do layout com o modelo
        binding.model = mModel

        // recebe o numero a partir do argumento
        val numero = arguments!!.getString("numero")

        // instancia biblioteca de validação
        val awesomeValidation = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)

        // cria validação de preenchimento de campo obrigatório
        awesomeValidation.addValidation(binding.root.findViewById<TextInputLayout>(R.id.nomeTextInputLayoutNome), RegexTemplate.NOT_EMPTY, getString(R.string.error_campo_obrigatorio))

        // busca o campo de nome
        val nome = binding.root.findViewById<EditText>(R.id.nomeEditTextNome)
        // busca o botão de salvar
        val salvar = binding.root.findViewById<Button>(R.id.nomeButtonSalvar)

        // funação chamada ao salvar
        salvar.setOnClickListener {
            // se o formulário estiver valido
            if (awesomeValidation.validate()) {
                // chama a função responsavel por finalizar o login
                mListener.onTerminoLogin(nome.text.toString(), numero)
            }
        }

        return binding.root
    }

    // interface do fragmento
    interface NomeFragmentListener {
        // cria a função de termino de login
        fun onTerminoLogin(nome: String, numero: String)
    }

    // função responsavel pela comunicação do fragmento com a activity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LoginActivity) {
            mListener = context
        }
    }

    companion object {
        // tag identificadora do fragmento
        const val TAG = "FRAGMENT_NOME"

        // função chamada ao instancia o fragmento a partir da activity
        fun newInstance(numero: String): NomeFragment {
            // cria a instacia do fragmento
            val fragment = NomeFragment()

            // instancia o objeto de argumentos
            val args = Bundle()
            // passa o numero para o objeto
            args.putString("numero", numero)

            // adiciona os argumetos ao fragmento
            fragment.arguments = args

            // retorna o fragmento
            return fragment
        }
    }

}