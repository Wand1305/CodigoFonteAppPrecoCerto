package com.example.precocerto

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ResultadosPesquisaActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var resultadosAdapter: ResultadosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_pesquisa)  // Este é o layout correto

        // Inicializa o ListView
        listView = findViewById(R.id.listViewResultados)

        // Pega os resultados passados pela InicioActivity
        val resultadosString = intent.getStringExtra("resultados") ?: ""

        // Se os resultados não estiverem vazios, exibe-os
        if (resultadosString.isNotEmpty()) {
            // Divide a string de resultados em uma lista
            val resultadosList = resultadosString.split("\n")

            // Cria o adaptador para o ListView e o define
            resultadosAdapter = ResultadosAdapter(this, resultadosList)
            listView.adapter = resultadosAdapter
        } else {
            // Exibe uma mensagem caso não haja resultados
            resultadosAdapter = ResultadosAdapter(this, listOf("Nenhum resultado encontrado"))
            listView.adapter = resultadosAdapter
        }
    }

    // Implementação do método de retorno
    fun voltarParaTelaAnterior(view: android.view.View) {
        super.onBackPressed()  // Vai realizar o comportamento padrão de voltar
    }
}
