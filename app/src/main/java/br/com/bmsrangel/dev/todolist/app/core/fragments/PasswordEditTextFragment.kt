package br.com.bmsrangel.dev.todolist.app.core.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import br.com.bmsrangel.dev.todolist.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordEditTextFragment : Fragment() {
    private lateinit var editText: EditText
    private lateinit var passwordStrengthText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_password_edit_text, container, false)
        editText = view.findViewById(R.id.editTextPasswordFragment)
        passwordStrengthText = view.findViewById(R.id.passwordStrengthText)
        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val password = p0.toString()

                val hasUppercase = password.matches(".*[A-Z].*".toRegex())
                val hasLowercase = password.matches(".*[a-z].*".toRegex())
                val hasNumber = password.matches(".*\\d.*".toRegex())
                val hasSymbol = password.matches(".*[!@#\$%^&*()].*".toRegex())
                val isLongEnough = password.length >= 8

                if (!isLongEnough || password.isEmpty()) {
                    passwordStrengthText.text = getString(R.string.weakPasswordText)
                }
                if (isLongEnough) {
                    passwordStrengthText.text = getString(R.string.mediumPasswordText)
                    passwordStrengthText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.yellow))
                }
                if (isLongEnough && hasUppercase && hasLowercase) {
                    passwordStrengthText.text = getString(R.string.strongPasswordText)
                    passwordStrengthText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.blue))
                }
                if (isLongEnough && hasUppercase && hasLowercase && hasNumber && hasSymbol) {
                    passwordStrengthText.text = getString(R.string.veryStrongPasswordText)
                    passwordStrengthText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.green))
                }
                passwordStrengthText.visibility = View.VISIBLE
            }

        })
        return view
    }

    fun getText(): String = editText.text.toString()
}