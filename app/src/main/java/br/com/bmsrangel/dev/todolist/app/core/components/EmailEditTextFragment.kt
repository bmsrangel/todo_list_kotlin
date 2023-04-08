package br.com.bmsrangel.dev.todolist.app.core.components

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import br.com.bmsrangel.dev.todolist.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmailEditTextFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var editText: EditText
    private lateinit var emailErrorText: TextView
    private var errorColor: Int? = null
    private lateinit var colorStateList: ColorStateList
    private val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_email_edit_text, container, false)
        editText = view.findViewById(R.id.editTextEmailFragment)
        emailErrorText = view.findViewById(R.id.emailErrorText)
        errorColor = ContextCompat.getColor(requireActivity(), R.color.red)
        colorStateList = ColorStateList.valueOf(errorColor!!)
        return view
    }

    fun getText(): String = editText.text.toString()

    fun validate(): Boolean {
        resetErrorMessage()
        val email = editText.text.toString()
        if (email.isEmpty()) {
            setErrorMessage(getString(R.string.mandatoryEmailErrorText))
            return false
        }
        if (!email.matches(emailRegex)) {
            setErrorMessage(getString(R.string.invalidEmailErrorText))
            return false
        }
        return true
    }

    private fun resetErrorMessage() {
        emailErrorText.text = null
        ViewCompat.setBackgroundTintList(editText, null)
        emailErrorText.visibility = View.GONE
    }

    private fun setErrorMessage(errorMessage: String) {
        emailErrorText.text = errorMessage
        ViewCompat.setBackgroundTintList(editText, colorStateList)
        emailErrorText.visibility = View.VISIBLE
    }

}