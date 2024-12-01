package com.example.precocerto

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class NovoProdutoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var spinnerMercado: Spinner
    private lateinit var edtProduto: EditText
    private lateinit var edtMarca: EditText
    private lateinit var edtTamanho: EditText
    private lateinit var edtPreco: EditText
    private lateinit var btnSalvar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_produto)

        // Inicializando o DBHelper
        dbHelper = DatabaseHelper(this)

        // Associando os elementos da UI
        edtProduto = findViewById(R.id.edtProduto)
        edtMarca = findViewById(R.id.edtMarca)
        edtTamanho = findViewById(R.id.edtTamanho)
        edtPreco = findViewById(R.id.edtPreco)
        spinnerMercado = findViewById(R.id.spinnerMercado)
        btnSalvar = findViewById(R.id.btnSalvar)

        // Carregar os mercados para o Spinner
        carregarMercados()

        // Ação do botão salvar
        btnSalvar.setOnClickListener {
            salvarProduto()
        }
    }

    private fun carregarMercados() {
        // Acessar o banco de dados
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT mercado_nome, endereco FROM mercados", null)

        // Criar uma lista para armazenar os nomes dos mercados
        val mercadosList = ArrayList<String>()

        // Adiciona o item "Selecione o mercado" como a primeira opção
        mercadosList.add("Selecione o mercado")  // Este será o primeiro item exibido

        // Preenche a lista com os mercados e endereços do banco de dados
        if (cursor.moveToFirst()) {
            do {
                val nomeColumnIndex = cursor.getColumnIndex("mercado_nome")
                val enderecoColumnIndex = cursor.getColumnIndex("endereco")

                if (nomeColumnIndex != -1 && enderecoColumnIndex != -1) {
                    val nomeMercado = cursor.getString(nomeColumnIndex)
                    val enderecoMercado = cursor.getString(enderecoColumnIndex)

                    // Adiciona o nome do mercado e o endereço concatenados
                    val mercadoEndereco = "$nomeMercado - $enderecoMercado"
                    mercadosList.add(mercadoEndereco)  // Adiciona o mercado e o endereço à lista
                } else {
                    Log.e("DatabaseHelper", "Colunas mercado_nome ou endereco não encontradas")
                }
            } while (cursor.moveToNext())
        }

        cursor.close()

        // Criar um adapter para vincular os dados do mercado ao Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mercadosList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Aqui você realmente configura o adapter ao Spinner
        spinnerMercado.adapter = adapter

        // Ajustar a altura do dropdown (lista suspensa) para mostrar até 3 ou 4 opções visíveis
        try {
            // Acessar o Popup do Spinner
            val popupField = Spinner::class.java.getDeclaredField("mPopup")
            popupField.isAccessible = true
            val popup = popupField.get(spinnerMercado)

            // Verificar se o Popup é uma ListPopupWindow
            if (popup is android.widget.ListPopupWindow) {
                // Calcular a altura em pixels para exibir 4 itens visíveis
                val itemHeight = (48 * resources.displayMetrics.density).toInt() // Tamanho de cada item
                val numberOfVisibleItems = 4 // Defina o número de itens visíveis
                val dropdownHeight = itemHeight * numberOfVisibleItems

                // Ajustar a altura do dropdown
                popup.height = dropdownHeight
            }
        } catch (e: Exception) {
            Log.e("Spinner", "Erro ao ajustar altura do dropdown: ${e.message}")
        }

        // Fechar o banco de dados
        db.close()  // Fechar o banco de dados após a operação
    }

    private fun salvarProduto() {
        // Verificar se todos os campos estão preenchidos
        val nome = edtProduto.text.toString().trim()
        val descricao = edtMarca.text.toString().trim()
        val tamanho = edtTamanho.text.toString().trim()
        val precoText = edtPreco.text.toString().trim()
        val mercado = spinnerMercado.selectedItem.toString()

        if (nome.isEmpty() || descricao.isEmpty() || tamanho.isEmpty() || precoText.isEmpty() || mercado == "Selecione o mercado") {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        val preco = precoText.toDoubleOrNull()

        if (preco == null) {
            Toast.makeText(this, "Preço inválido!", Toast.LENGTH_SHORT).show()
            return
        }

        // Inserir o produto no banco de dados
        val db = dbHelper.writableDatabase
        val values = ContentValues()
        values.put("produto", nome)
        values.put("marca", descricao)
        values.put("tamanho", tamanho)
        values.put("preco", preco)
        values.put("mercado", mercado)

        // Inserir na tabela de produtos
        val newRowId = db.insert("produtos", null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Produto salvo com sucesso!", Toast.LENGTH_SHORT).show()
            finish() // Finaliza a atividade após salvar
        } else {
            Toast.makeText(this, "Erro ao salvar produto", Toast.LENGTH_SHORT).show()
        }
    }
}
