package com.example.precocerto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CadastroActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        // Inicializar o banco de dados
        databaseHelper = DatabaseHelper(this)

        // Referências aos campos do layout
        val nomeSobrenome = findViewById<EditText>(R.id.nome_sobrenome)
        val email = findViewById<EditText>(R.id.email)
        val dataNascimento = findViewById<EditText>(R.id.data_nascimento)
        val endereco = findViewById<EditText>(R.id.endereco)
        val senha = findViewById<EditText>(R.id.senha)
        val confirmarSenha = findViewById<EditText>(R.id.confirmar_senha)
        val criarContaButton = findViewById<Button>(R.id.criar_conta_button)

        // Aplicar máscara ao campo de data de nascimento
        dataNascimento.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return

                val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                val formattedString = StringBuilder()
                var i = 0

                for (char in mask) {
                    if (char == '#' && i < cleanString.length) {
                        formattedString.append(cleanString[i])
                        i++
                    } else if (i < cleanString.length) {
                        formattedString.append(char)
                    }
                }

                isUpdating = true
                dataNascimento.setText(formattedString.toString())
                dataNascimento.setSelection(formattedString.length)
                isUpdating = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Configuração do botão "Criar Conta"
        criarContaButton.setOnClickListener {
            // Capturar os dados inseridos
            val nome = nomeSobrenome.text.toString()
            val emailStr = email.text.toString()
            val dataNasc = dataNascimento.text.toString()
            val end = endereco.text.toString()
            val pass = senha.text.toString()
            val confirmPass = confirmarSenha.text.toString()

            // Validações básicas
            if (nome.isEmpty() || emailStr.isEmpty() || dataNasc.isEmpty() || end.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Todos os campos devem ser preenchidos!", Toast.LENGTH_SHORT).show()
            } else if (pass != confirmPass) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
            } else {
                // Inserir no banco de dados
                val resultado = databaseHelper.inserirUsuario(nome, emailStr, dataNasc, end, pass)
                if (resultado != -1L) {
                    Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                    // Direcionar para a próxima tela
                    val intent = Intent(this, InicioActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao criar conta. Tente novamente.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
