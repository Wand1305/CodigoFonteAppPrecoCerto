package com.example.precocerto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ResultadosAdapter(context: Context, private val listaResultados: List<String>) : ArrayAdapter<String>(context, 0, listaResultados) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_resultado, parent, false)

        // Recupera o item da lista
        val resultado = listaResultados[position]

        // Divide os dados de cada resultado para exibir
        val dadosProduto = resultado.split(" - ")  // Exemplo de separação por hífen, modifique conforme necessário

        // Conecta as TextViews do item com o layout de item_resultado.xml
        val textViewProduto = view.findViewById<TextView>(R.id.textViewProduto)
        val textViewMarca = view.findViewById<TextView>(R.id.textViewMarca)
        val textViewTamanho = view.findViewById<TextView>(R.id.textViewTamanho)
        val textViewPreco = view.findViewById<TextView>(R.id.textViewPreco)
        val textViewMercado = view.findViewById<TextView>(R.id.textViewMercado)
        val textViewEndereco = view.findViewById<TextView>(R.id.textViewEndereco)

        // Preenche as TextViews com os dados do produto
        if (dadosProduto.size >= 6) {
            textViewProduto.text = dadosProduto[0]
            textViewMarca.text = dadosProduto[1]
            textViewTamanho.text = dadosProduto[2]
            textViewPreco.text = dadosProduto[3]
            textViewMercado.text = dadosProduto[4]
            textViewEndereco.text = dadosProduto[5]
        }

        return view
    }

    fun getItemCount(): Int {
        return listaResultados.size
    }
}
