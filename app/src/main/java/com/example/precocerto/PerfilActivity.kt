package com.example.precocerto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PerfilActivity : AppCompatActivity() {

    private lateinit var tvNome: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDataNascimento: TextView
    private lateinit var tvEndereco: TextView
    private lateinit var btnTrocarSenha: Button
    private lateinit var btnExcluirConta: Button  // Inicialize o botão de Excluir Conta
    private lateinit var databaseHelper: DatabaseHelper  // Referência ao DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // Inicializando os TextViews
        tvNome = findViewById(R.id.tvUsuarioNome)
        tvEmail = findViewById(R.id.tvUsuarioEmail)
        tvDataNascimento = findViewById(R.id.tvUsuarioDataNascimento)
        tvEndereco = findViewById(R.id.tvUsuarioEndereco)

        // Inicializando os botões
        btnTrocarSenha = findViewById(R.id.btnTrocarSenha)
        btnExcluirConta = findViewById(R.id.btnExcluirConta)  // Inicializando o botão "Excluir Conta"

        // Inicializando o DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Recebendo o e-mail da Activity anterior (InicioActivity)
        val emailDoUsuario = intent.getStringExtra("usuario_email")

        // Verifica se o e-mail foi passado corretamente
        if (!emailDoUsuario.isNullOrEmpty()) {
            // Buscar os dados do usuário com o e-mail
            buscarDadosPerfil(emailDoUsuario)
        } else {
            Toast.makeText(this, "E-mail não encontrado", Toast.LENGTH_SHORT).show()
        }

        // Ação do botão "Trocar Senha"
        btnTrocarSenha.setOnClickListener {
            val intent = Intent(this, TrocarSenhaActivity::class.java)
            intent.putExtra("usuario_email", emailDoUsuario) // Passa o e-mail
            startActivity(intent)
        }

        // Ação do botão "Excluir Conta"
        btnExcluirConta.setOnClickListener {
            // Obter o e-mail do usuário
            val emailDoUsuario = tvEmail.text.toString()

            // Inicia a nova Activity de confirmação de exclusão e passa o e-mail
            val intent = Intent(this, ConfirmacaoExclusaoActivity::class.java)
            intent.putExtra("usuario_email", emailDoUsuario) // Passa o e-mail para a Intent
            startActivity(intent)
        }
    }

    // Função para buscar os dados do usuário com base no e-mail
    private fun buscarDadosPerfil(email: String) {
        // Aqui você chama o método para buscar os dados do banco real
        val dadosUsuario = databaseHelper.buscarUsuarioPorEmail(email)

        // Verifica se os dados foram encontrados
        if (dadosUsuario != null) {
            // Exibindo os dados do usuário na tela
            tvNome.text = dadosUsuario.nome
            tvEmail.text = dadosUsuario.email
            tvDataNascimento.text = dadosUsuario.dataNascimento
            tvEndereco.text = dadosUsuario.endereco
        } else {
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }
}
