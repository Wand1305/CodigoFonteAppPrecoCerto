package com.example.precocerto

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import android.os.Handler
import android.widget.ImageButton
import android.widget.TextView

class InicioActivity : AppCompatActivity() {

    private lateinit var popupView: View
    private lateinit var dialog: Dialog
    private lateinit var edtEmail: EditText
    private lateinit var btnBuscarDados: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var edtSearch: EditText
    private lateinit var btnSearch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        dbHelper = DatabaseHelper(this)

        // Recupera o nome do usuário do SharedPreferences
        val nomeDoUsuario = intent.getStringExtra("usuario_email")

        // Referência ao TextView de saudação
        val welcomeTextView: TextView = findViewById(R.id.welcomeText)
        welcomeTextView.text = "Bem-vindo, $nomeDoUsuario !"

        // Referências aos elementos da tela
        edtSearch = findViewById(R.id.editTextSearch)
        btnSearch = findViewById(R.id.buttonSearch)

        // Ação para o ícone de busca
        btnSearch.setOnClickListener {
            val query = edtSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                /// Chama a função de busca no banco de dados
                buscarNoBanco(query)
            } else {
                Toast.makeText(this, "Por favor, insira algo para buscar.", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar ações nos ícones do menu inferior
        findViewById<ImageView>(R.id.iconPublicar).setOnClickListener {
            showCustomDialog() // Exibir o pop-up de escolha
        }

        findViewById<ImageView>(R.id.iconLista).setOnClickListener {
            // Placeholder para a tela de lista
            Toast.makeText(this, "Tela de Lista ainda não implementada", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.iconFavoritos).setOnClickListener {
            // Placeholder para a tela de favoritos
            Toast.makeText(this, "Tela de Favoritos ainda não implementada", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.iconPerfil).setOnClickListener {
            // Passando o e-mail para a tela de perfil
            val intent = Intent(this, PerfilActivity::class.java)
            intent.putExtra("usuario_email", nomeDoUsuario) // Substitua usuarioEmail pelo e-mail real
            startActivity(intent)
        }

        // Configuração do botão de logoff
        findViewById<ImageButton>(R.id.btnSair).setOnClickListener {
            realizarLogoff() // Chama a função de logoff
        }

        // Configurar o carrossel de imagens
        val viewPager = findViewById<ViewPager2>(R.id.viewPagerCarousel)

        // Lista de imagens para o carrossel
        val images = listOf(
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3
        )

        // Configurar o adapter para o carrossel
        val adapter = CarouselAdapter(images)
        viewPager.adapter = adapter

        // Ativar rolagem infinita (opcional)
        viewPager.offscreenPageLimit = 1

        // Criar um Handler para a navegação automática
        val handler = Handler(Looper.getMainLooper())

        val update = Runnable {
            val currentItem = viewPager.currentItem
            val nextItem = if (currentItem + 1 < images.size) currentItem + 1 else 0
            viewPager.setCurrentItem(nextItem, true)
        }

        // Definir um intervalo para a rotação das imagens (exemplo: 3 segundos)
        handler.postDelayed(object : Runnable {
            override fun run() {
                update.run()
                handler.postDelayed(this, 5000) // Repetir a rotação a cada 3 segundos
            }
        }, 5000)
    }

    // Método para fazer o logoff
    private fun logoff() {
        // Alterar o status de login para false
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("is_logged_in", false)  // Usuário deslogado
        editor.apply()

        // Redirecionar para a tela de login
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()  // Fecha a activity atual para que o usuário não volte à tela anterior
    }

    // Método para abrir a nova janela de dialogo
    private fun showCustomDialog() {
        // Inflar o layout do dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog, null)

        // Criar o Dialog com o layout inflado
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)

        // Referências para os botões dentro do layout
        val option1Button: Button = dialogView.findViewById(R.id.option1)
        val option2Button: Button = dialogView.findViewById(R.id.option2)

        // Configurar os cliques dos botões
        option1Button.setOnClickListener {
            // Ação para o botão "Novo Mercado"
            openNewMarketDialog() // Você pode chamar a função de sua escolha
            dialog.dismiss() // Fecha o diálogo após a ação
        }

        option2Button.setOnClickListener {
            // Ação para o botão "Novo Produto"
            openNewProductDialog() // Outra função personalizada
            dialog.dismiss() // Fecha o diálogo após a ação
        }

        // Exibir o diálogo
        dialog.show()
    }

    // Método para abrir a tela de "Novo Mercado"
    private fun openNewMarketDialog() {
        val intent = Intent(this, NovoMercadoActivity::class.java)
        startActivity(intent)
    }

    // Método para abrir a tela de "Novo Produto"
    private fun openNewProductDialog() {
        val intent = Intent(this, NovoProdutoActivity::class.java)
        startActivity(intent)
    }

    // Método para abrir o pop-up do perfil
    private fun abrirPopupPerfil() {
        // Inflando o layout do pop-up
        popupView = layoutInflater.inflate(R.layout.popup_perfil, null)
        dialog = Dialog(this)
        dialog.setContentView(popupView)

        // Inicializando os campos do pop-up
        edtEmail = popupView.findViewById(R.id.edtEmail)
        btnBuscarDados = popupView.findViewById(R.id.btnBuscarDados)

        // Exibindo o pop-up
        dialog.show()

        // Configurando o evento do botão "Buscar meus dados"
        btnBuscarDados.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            if (email.isNotEmpty() && isValidEmail(email)) {
                // Chama o método para buscar os dados do usuário com o e-mail
                buscarUsuarioPorEmail(email)
            } else {
                Toast.makeText(this, "Por favor, insira um e-mail válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Validação de e-mail
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Método para buscar os dados do usuário no banco
    private fun buscarUsuarioPorEmail(email: String) {
        val usuario = dbHelper.buscarUsuarioPorEmail(email)

        if (usuario != null) {
            // Se o usuário foi encontrado, fecha o pop-up e abre a tela de perfil
            dialog.dismiss()
            abrirTelaDePerfil(usuario)
        } else {
            // Caso o e-mail não seja encontrado
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para abrir a tela de perfil e passar os dados do usuário
    private fun abrirTelaDePerfil(usuario: Usuario) {
        // Crie uma Intent para abrir a tela de perfil e passe os dados do usuário
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("usuario_nome", usuario.nome)
        intent.putExtra("usuario_email", usuario.email)
        intent.putExtra("usuario_data_nascimento", usuario.dataNascimento)
        intent.putExtra("usuario_endereco", usuario.endereco)
        startActivity(intent)
    }

    // Função para realizar a busca no banco de dados
    private fun buscarNoBanco(query: String) {
        // Chama o método do DatabaseHelper para buscar no banco
        val resultados = dbHelper.buscarPorTermo(query)

        if (resultados.isNotEmpty()) {
            // Exibe os resultados encontrados
            exibirResultados(resultados)
        } else {
            Toast.makeText(this, "Nenhum resultado encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para exibir os resultados da busca em uma nova Activity
    private fun exibirResultados(resultados: List<String>) {
        val resultadosString = resultados.joinToString("\n")

        // Criar um Intent para abrir a ActivityResultadoPesquisa
        val intent = Intent(this, ResultadosPesquisaActivity::class.java)

        // Passar os resultados para a nova Activity
        intent.putExtra("resultados", resultadosString)

        // Iniciar a Activity
        startActivity(intent)
    }

    // Método de logoff
    private fun realizarLogoff() {
        // Limpa as preferências do usuário, se necessário (opcional)
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Direciona para a tela de login novamente
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Finaliza a atividade atual para que o usuário não possa voltar
        finish()
    }
}
