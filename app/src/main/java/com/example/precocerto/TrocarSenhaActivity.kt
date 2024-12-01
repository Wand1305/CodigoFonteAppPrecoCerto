package com.example.precocerto

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TrocarSenhaActivity : AppCompatActivity() {

    // Inicialização direta das variáveis de EditText e Button
    private var senhaAtualEditText: EditText? = null
    private var novaSenhaEditText: EditText? = null
    private var confirmarSenhaEditText: EditText? = null
    private var btnConfirmarTrocaSenha: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trocar_senha)

        // Inicialização dos componentes
        senhaAtualEditText = findViewById(R.id.senha_atual)
        novaSenhaEditText = findViewById(R.id.nova_senha)
        confirmarSenhaEditText = findViewById(R.id.confirmar_senha)
        btnConfirmarTrocaSenha = findViewById(R.id.btnConfirmarTrocaSenha)

        // Ação do botão de confirmar
        btnConfirmarTrocaSenha?.setOnClickListener {
            if (validarCampos()) {
                // Lógica para atualizar a senha
                trocarSenha()
            }
        }
    }

    // Validação dos campos de senha
    private fun validarCampos(): Boolean {
        val senhaAtual = senhaAtualEditText?.text.toString().trim()
        val novaSenha = novaSenhaEditText?.text.toString().trim()
        val confirmarSenha = confirmarSenhaEditText?.text.toString().trim()

        // Verifica se os campos estão vazios
        if (senhaAtual.isNullOrBlank()) {
            senhaAtualEditText?.error = "A senha atual é obrigatória."
            return false
        }

        if (novaSenha.isNullOrBlank()) {
            novaSenhaEditText?.error = "A nova senha é obrigatória."
            return false
        }

        if (confirmarSenha.isNullOrBlank()) {
            confirmarSenhaEditText?.error = "A confirmação da nova senha é obrigatória."
            return false
        }

        // Verifica se as senhas coincidem
        if (novaSenha != confirmarSenha) {
            confirmarSenhaEditText?.error = "As senhas não coincidem."
            return false
        }

        // Verifica a complexidade da nova senha (exemplo de mínimo de 6 caracteres)
        if (novaSenha.length < 6) {
            novaSenhaEditText?.error = "A senha deve ter pelo menos 6 caracteres."
            return false
        }

        return true
    }

    private fun trocarSenha() {
        val senhaAtual = senhaAtualEditText?.text.toString().trim()
        val novaSenha = novaSenhaEditText?.text.toString().trim()

        // Validar os campos
        if (!validarCampos()) return

        // Obter o e-mail do usuário logado
        val email = obterEmailUsuarioLogado()

        // Verificar se o e-mail foi recuperado corretamente
        if (email.isBlank()) {
            Toast.makeText(this, "Não foi possível identificar o usuário logado.", Toast.LENGTH_SHORT).show()
            return
        }

        // Verifique a senha atual no banco de dados usando o e-mail
        val dbHelper = DatabaseHelper(this)

        if (dbHelper.verificarSenhaAtualPorEmail(email, senhaAtual)) {
            // Se a senha atual estiver correta, atualize a nova senha
            if (dbHelper.atualizarSenhaPorEmail(email, novaSenha)) {
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar a senha.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Senha atual incorreta
            Toast.makeText(this, "A senha atual está incorreta.", Toast.LENGTH_SHORT).show()
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
