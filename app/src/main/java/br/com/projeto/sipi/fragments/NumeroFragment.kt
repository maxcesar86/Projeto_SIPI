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
import br.com.projeto.sipi.databinding.FragmentNumeroBinding
import br.com.projeto.sipi.viewmodel.NumeroViewModel
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate

class NumeroFragment : Fragment() {

    // recebe o modelo
    private lateinit var mModel: NumeroViewModel
    // recebe a interface do fragmento
    private var mListener: NumeroFragmentListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // instancia o layout do fragmento
        val binding: FragmentNumeroBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_numero, container, false)
        // instancia o modelo do fragmento
        mModel = ViewModelProviders.of(this).get(NumeroViewModel::class.java)

        // adiciona o vinculo do layout com o modelo
        binding.model = mModel

        // instancia biblioteca de validação
        val awesomeValidation = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)

        // cria validação de preenchimento de campo obrigatório
        awesomeValidation.addValidation(binding.root.findViewById<TextInputLayout>(R.id.numeroTextInputLayoutNumero), RegexTemplate.NOT_EMPTY, getString(R.string.error_campo_obrigatorio))

        // instancia o botão de confirmação
        val confirmar = binding.root.findViewById<Button>(R.id.numeroButtonContinuar)

        // função chamada ao pressionar o botão de confirmação
        confirmar.setOnClickListener {
            // se estiver valido
            if (awesomeValidation.validate()) {
                // busca o numero digitado
                val numero = binding.root.findViewById<EditText>(R.id.numeroEditTextNumero).text.toString()
                // chama a função de validação do numero
                mListener?.onNumeroValido(numero)
            }
        }

        return binding.root
    }

    // interface do fragmento
    interface NumeroFragmentListener {
        // função chamda ao termino de informação do numero
        fun onNumeroValido(numero: String)
    }

    // função responsavel pela comunicação do fragmento com a activity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LoginActivity) {
            mListener = context
        }
    }

    companion object {
        // tag de identificação do fragmento
        const val TAG = "FRAGMENT_NUMERO"

        fun newInstance(): NumeroFragment {
            return NumeroFragment()
        }
    }

}