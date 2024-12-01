package com.example.precocerto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ConfirmacaoExclusaoActivity : AppCompatActivity() {

    private lateinit var btnConfirmar: Button
    private lateinit var btnCancelar: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacao_exclusao)

        // Inicializar os botões
        btnConfirmar = findViewById(R.id.btnConfirmar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Inicializar o DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Ação do botão "Confirmar"
        btnConfirmar.setOnClickListener {
            // Lógica para excluir a conta
            excluirConta()
        }

        // Ação do botão "Cancelar"
        btnCancelar.setOnClickListener {
            // Apenas finaliza a activity e retorna à anterior
            finish()
        }
    }

    private fun excluirConta() {
        // Obter o e-mail do usuário logado
        val email = obterEmailUsuarioLogado()

        // Verifica se o e-mail foi obtido corretamente
        if (email.isBlank()) {
            Toast.makeText(this, "Não foi possível identificar o usuário logado.", Toast.LENGTH_SHORT).show()
            return
        }

        // Chama o método para excluir os dados do usuário no banco
        val isDeleted = databaseHelper.excluirContaPorEmail(email)

        if (isDeleted) {
            Toast.makeText(this, "Conta excluída com sucesso!", Toast.LENGTH_SHORT).show()

            // Redireciona para a tela de login ou outra tela relevante
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a activity de confirmação
        } else {
            Toast.makeText(this, "Erro ao excluir a conta.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obterEmailUsuarioLogado(): String {
        // Tenta obter o e-mail da Intent
        val emailDaIntent = intent.getStringExtra("usuario_email")
        if (!emailDaIntent.isNullOrBlank()) {
            return emailDaIntent
        }

        // Se não encontrar, tenta no SharedPreferences
        val sharedPreferences = getSharedPreferences("login_pref", MODE_PRIVATE)
        val emailDoSharedPreferences = sharedPreferences.getString("usuario", "")
        if (!emailDoSharedPreferences.isNullOrBlank()) {
            return emailDoSharedPreferences
        }

        // Retorna vazio e notifica o usuário se falhar
        Toast.makeText(this, "Erro ao identificar o usuário logado.", Toast.LENGTH_SHORT).show()
        return ""
    }
}
