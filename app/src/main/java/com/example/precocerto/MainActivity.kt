package com.example.precocerto

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Campos de Usuário, Senha e Switch "Lembrar-me"
        val usuario: EditText = findViewById(R.id.usuario)
        val senha: EditText = findViewById(R.id.senha)
        val switchLembrarMe: Switch = findViewById(R.id.switchLembrarMe)

        // Botão Entrar
        val entrarButton: Button = findViewById(R.id.entrar_button)

        // Recuperar dados salvos, se houver
        recuperarCredenciais()

        // Ação ao clicar no botão Entrar
        entrarButton.setOnClickListener {
            val usuarioText = usuario.text.toString().trim()
            val senhaText = senha.text.toString().trim()

            // Validação simples
            if (usuarioText.isEmpty() || senhaText.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else if (!usuarioText.matches("^[A-Za-z0-9+_.-]+@(.+)$".toRegex())) {
                // Validação de e-mail com regex
                Toast.makeText(this, "Por favor, insira um e-mail válido!", Toast.LENGTH_SHORT).show()
            } else {
                // Verificar se o usuário e senha estão no banco de dados
                val dbHelper = DatabaseHelper(this)

                // Dentro do método 'entrarButton.setOnClickListener'
                if (dbHelper.verificarLogin(usuarioText, senhaText)) {
                    // Se o login for válido
                    Toast.makeText(this, "Bem-vindo, $usuarioText!", Toast.LENGTH_SHORT).show()

                    // Salvar dados de login no SharedPreferences, se "Lembrar-me" estiver ativado
                    val sharedPreferences = getSharedPreferences("login_pref", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    if (switchLembrarMe.isChecked) {
                        // Salvar o e-mail e senha, se "Lembrar-me" estiver ativado
                        editor.putString("usuario_email", usuarioText)
                        editor.putString("senha", senhaText)  // Salvar a senha também
                    } else {
                        // Limpar as credenciais, se "Lembrar-me" estiver desmarcado
                        editor.remove("usuario_email")
                        editor.remove("senha")
                    }

                    // Salvar o status de login
                    editor.putBoolean("is_logged_in", true) // Usuário está logado
                    editor.apply()

                    // Redirecionar para a tela principal e passar o e-mail do usuário
                    val intent = Intent(this, InicioActivity::class.java)
                    intent.putExtra("usuario_email", usuarioText) // Passando o e-mail do usuário
                    startActivity(intent)
                    finish() // Finaliza a Activity de login para evitar que o usuário volte
                } else {
                    // Se o login for inválido
                    Toast.makeText(this, "E-mail ou senha incorretos!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Texto "Não tem conta? Cadastre-se"
        val cadastroTexto: TextView = findViewById(R.id.cadastro_texto)
        cadastroTexto.setOnClickListener {
            // Redireciona para a tela de cadastro
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        // Garantir que os campos tenham foco ao inicializar a tela
        usuario.requestFocus() // Foco no campo de usuário ao abrir a tela
    }

    // Função para recuperar as credenciais salvas
    private fun recuperarCredenciais() {
        val sharedPreferences: SharedPreferences = getSharedPreferences("login_pref", MODE_PRIVATE)

        // Recuperar os dados salvos
        val usuarioSalvo = sharedPreferences.getString("usuario_email", null)
        val senhaSalva = sharedPreferences.getString("senha", null)

        // Se houver dados salvos, preencher os campos
        if (usuarioSalvo != null && senhaSalva != null) {
            val usuario: EditText = findViewById(R.id.usuario)
            val senha: EditText = findViewById(R.id.senha)
            val switchLembrarMe: Switch = findViewById(R.id.switchLembrarMe)

            usuario.setText(usuarioSalvo)
            senha.setText(senhaSalva)
            switchLembrarMe.isChecked = true  // Marcar o Switch de "Lembrar-me"
        }
    }
}
