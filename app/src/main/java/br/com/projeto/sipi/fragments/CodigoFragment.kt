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
import br.com.projeto.sipi.R
import br.com.projeto.sipi.databinding.FragmentCodigoBinding
import br.com.projeto.sipi.viewmodel.CodigoViewModel
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseException
import android.widget.Button
import android.widget.EditText
import br.com.projeto.sipi.LoginActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.google.firebase.auth.PhoneAuthCredential

class CodigoFragment : Fragment() {

    // recebe o modelo
    private lateinit var mModel: CodigoViewModel
    // recebe o campo de informação de codigo
    private lateinit var mEditTextCodigo: EditText
    // recebe a instancia da interface do fragmento
    private lateinit var mListener: CodigoFragmentListener
    // recebe o codigo de verificação
    private var mVerificationId: String = ""
    // recebe o numero
    private var mNumero: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // instancia o layout do fragmento
        val binding: FragmentCodigoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_codigo, container, false)
        // instancia o modelo do fragmento
        mModel = ViewModelProviders.of(this).get(CodigoViewModel::class.java)

        // adiciona o vinculo do layout com o modelo
        binding.model = mModel

        // guarda o numero recebido do argumento
        mNumero = arguments!!.getString("numero")

        // manda o codigo de verificação
        mandarCodigoVerifiacao(mNumero)

        // instancia o campo de informação de codigo
        mEditTextCodigo = binding.root.findViewById(R.id.codigoEditTextCodigo)

        // instancia biblioteca de validação
        val awesomeValidation = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)

        // cria validação de preenchimento de campo obrigatório
        awesomeValidation.addValidation(binding.root.findViewById<TextInputLayout>(R.id.codigoTextInputLayoutCodigo), RegexTemplate.NOT_EMPTY, getString(R.string.error_campo_obrigatorio))

        // instancia o botão de verificaçao de codigo
        val verificar = binding.root.findViewById<Button>(R.id.codigoButtonVerificar)

        // ao clicar no botão
        verificar.setOnClickListener {
            // valida o formulario
            if (awesomeValidation.validate()) {
                // busca o codigo digitado
                val codigo = mEditTextCodigo.text.toString()
                // chama a função de verificação
                verificarCodigo(codigo)
            }
        }

        return binding.root
    }

    // função responsavel por enviar o codigo de verificação
    private fun mandarCodigoVerifiacao(numero: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(numero, 60, TimeUnit.SECONDS, activity!!, mCallbacks)
    }

    // função chamada para receber o codigo automaticamente
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            // busca o codigo recebeido por sms
            val codigo = phoneAuthCredential.smsCode

            // se possuir
            if (codigo != null) {
                // adiciona ao campo
                mEditTextCodigo.setText(codigo)
                // chama função de verificação
                verificarCodigo(codigo)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {

        }

        // ao enviar o codigo
        override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(s, forceResendingToken)
            // guarda o codigo de verificação
            mVerificationId = s
        }
    }

    // verifica o codigo digitado
    private fun verificarCodigo(codigo: String) {
        // busca a credencial de autentição
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId, codigo)
        // chama a função de credenciamento
        mListener.onCodigoVerificado(credential, mNumero)
    }

    // interface do fragmento
    interface CodigoFragmentListener {
        // função chamada ao terminar o credenciamento do usuario
        fun onCodigoVerificado(credential: PhoneAuthCredential, numero: String)
    }

    // função responsavel pela comunicação do fragmento com a activity
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LoginActivity) {
            mListener = context
        }
    }

    companion object {
        // tag de idenficação do fragmento
        const val TAG = "FRAGMENT_CODIGO"

        // função chamada ao instancia o fragmento a partir da activity
        fun newInstance(numero: String): CodigoFragment {
            // cria a instacia do fragmento
            val fragment = CodigoFragment()

            // instancia o objeto de argumentos
            val args = Bundle()
            // passa o numero para o argumento
            args.putString("numero", numero)

            // adiciona os argumetos ao fragmento
            fragment.arguments = args

            // retorna o fragmento
            return fragment
        }
    }

}