package com.example.precocerto

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NovoMercadoActivity : AppCompatActivity() {

    private lateinit var etNomeMercado: EditText
    private lateinit var etEnderecoMercado: EditText
    private lateinit var btnSalvarMercado: Button
    private lateinit var dbHelper: DatabaseHelper // Classe que gerencia o SQLite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_mercado)

        // Inicializar os componentes da tela
        etNomeMercado = findViewById(R.id.etNomeMercado)
        etEnderecoMercado = findViewById(R.id.etEnderecoMercado)
        btnSalvarMercado = findViewById(R.id.btnSalvarMercado)
        dbHelper = DatabaseHelper(this)

        btnSalvarMercado.setOnClickListener {
            val nomeMercado = etNomeMercado.text.toString()
            val enderecoMercado = etEnderecoMercado.text.toString()

            // Verifica se os campos estão preenchidos
            if (nomeMercado.isNotEmpty() && enderecoMercado.isNotEmpty()) {
                Log.d("NovoMercadoActivity", "Nome Mercado: $nomeMercado, Endereço: $enderecoMercado")
                salvarMercado(nomeMercado, enderecoMercado)
            } else {
                Log.e("NovoMercadoActivity", "Campos estão vazios!")
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarMercado(nome: String, endereco: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("mercado_nome", nome)
            put("endereco", endereco)
        }

        // Log antes de tentar inserir
        Log.d("NovoMercadoActivity", "Tentando inserir mercado no banco de dados: $nome, $endereco")

        val newRowId = db.insert(DatabaseHelper.TABLE_MERCADOS, null, values)

        if (newRowId != -1L) {
            // Log para quando a inserção for bem-sucedida
            Log.d("NovoMercadoActivity", "Mercado inserido com sucesso, ID: $newRowId")
            Toast.makeText(this, "Mercado salvo com sucesso!", Toast.LENGTH_SHORT).show()
            finish() // Finaliza a atividade após salvar
        } else {
            // Log para quando a inserção falhar
            Log.e("NovoMercadoActivity", "Erro ao salvar mercado. ID retornado: $newRowId")
            Toast.makeText(this, "Erro ao salvar mercado", Toast.LENGTH_SHORT).show()
        }

        fun verificarSeTabelaContemDados() {
            val db = dbHelper.readableDatabase

            // Usando uma consulta simples para verificar se há dados na tabela "mercados"
            val cursor = db.rawQuery("SELECT COUNT(*) FROM mercados", null)

            if (cursor.moveToFirst()) {
                val count = cursor.getInt(0)  // Obtém a contagem de registros
                Log.d("NovoMercadoActivity", "Número de mercados na tabela: $count")

                if (count > 0) {
                    Log.d("NovoMercadoActivity", "A tabela 'mercados' contém dados.")
                } else {
                    Log.d("NovoMercadoActivity", "A tabela 'mercados' está vazia.")
                }
            }

            cursor.close()
        }
    }
}
