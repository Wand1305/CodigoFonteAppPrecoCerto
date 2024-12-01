package com.example.precocerto

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.MessageDigest
import android.util.Base64

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "precocerto.db"
        const val DATABASE_VERSION = 5

        // Tabela de Produtos
        const val TABLE_PRODUTOS = "produtos"
        const val COLUMN_ID = "id"
        const val COLUMN_PRODUTO = "produto"
        const val COLUMN_MARCA = "marca"
        const val COLUMN_TAMANHO = "tamanho"
        const val COLUMN_PRECO = "preco"
        const val COLUMN_MERCADO = "mercado"

        // Tabela de Mercados
        const val TABLE_MERCADOS = "mercados"
        const val COLUMN_MERCADO_ID = "mercado_id"
        const val COLUMN_MERCADO_NOME = "mercado_nome"
        const val COLUMN_ENDERECO = "endereco"

        // Tabela de Usuários
        const val TABLE_USUARIOS = "usuarios"
        const val COLUMN_USUARIO_ID = "id"
        const val COLUMN_USUARIO_NOME = "nome"
        const val COLUMN_USUARIO_EMAIL = "email"
        const val COLUMN_USUARIO_DATA_NASCIMENTO = "data_nascimento"
        const val COLUMN_USUARIO_ENDERECO = "endereco"
        const val COLUMN_USUARIO_SENHA = "senha"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableProdutos = """
            CREATE TABLE $TABLE_PRODUTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PRODUTO TEXT NOT NULL,
                $COLUMN_MARCA TEXT,
                $COLUMN_TAMANHO TEXT,
                $COLUMN_PRECO REAL NOT NULL,
                $COLUMN_MERCADO TEXT NOT NULL
            );
        """.trimIndent()

        val createTableMercados = """
            CREATE TABLE $TABLE_MERCADOS (
                $COLUMN_MERCADO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MERCADO_NOME TEXT NOT NULL,
                $COLUMN_ENDERECO TEXT
            );
        """.trimIndent()

        val createTableUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COLUMN_USUARIO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USUARIO_NOME TEXT NOT NULL,
                $COLUMN_USUARIO_EMAIL TEXT NOT NULL,
                $COLUMN_USUARIO_DATA_NASCIMENTO TEXT,
                $COLUMN_USUARIO_ENDERECO TEXT,
                $COLUMN_USUARIO_SENHA TEXT NOT NULL
            );
        """.trimIndent()

        db.execSQL(createTableProdutos)
        db.execSQL(createTableMercados)
        db.execSQL(createTableUsuarios)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 5) {
            // Adiciona a coluna 'tamanho' à tabela 'produtos' se a versão for menor que 4
            db.execSQL("ALTER TABLE $TABLE_PRODUTOS ADD COLUMN $COLUMN_TAMANHO TEXT;")
        }
    }

    // Método para inserir um novo produto
    fun addProduto(nome: String, descricao: String, preco: Double, mercado: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        // Validar dados antes de inserir
        if (nome.isEmpty() || preco <= 0 || mercado.isEmpty()) {
            Log.e("DatabaseHelper", "Erro: Dados inválidos para inserção")
            db.close()
            return -1 // Retorna erro
        }

        values.apply {
            put(COLUMN_PRODUTO, nome)
            put(COLUMN_MARCA, descricao)
            put(COLUMN_PRECO, preco)
            put(COLUMN_MERCADO, mercado)
        }

        return try {
            db.insert(TABLE_PRODUTOS, null, values)
        } catch (e: SQLException) {
            Log.e("DatabaseHelper", "Erro ao inserir produto: ${e.message}")
            -1 // Retorna erro em caso de falha
        } finally {
            db.close()
        }
    }

    // Método para listar todos os produtos
    fun listarProdutos(): List<String> {
        val produtos = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_PRODUTOS,
            arrayOf(COLUMN_PRODUTO, COLUMN_MARCA, COLUMN_PRECO, COLUMN_MERCADO),
            null, null, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val nome = it.getString(it.getColumnIndexOrThrow(COLUMN_PRODUTO))
                val descricao = it.getString(it.getColumnIndexOrThrow(COLUMN_MARCA))
                val preco = it.getDouble(it.getColumnIndexOrThrow(COLUMN_PRECO))
                val mercado = it.getString(it.getColumnIndexOrThrow(COLUMN_MERCADO))
                produtos.add("$nome - $descricao - R$$preco - Mercado: $mercado")
            }
        }
        db.close()
        return produtos
    }

    // Método para inserir um novo mercado
    fun addMercado(nome: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        // Validar dados antes de inserir
        if (nome.isEmpty()) {
            Log.e("DatabaseHelper", "Erro: Nome do mercado não pode ser vazio")
            return -1 // Retorna erro
        }

        values.put(COLUMN_MERCADO_NOME, nome)

        return try {
            val id = db.insert(TABLE_MERCADOS, null, values)
            if (id == -1L) {
                Log.e("DatabaseHelper", "Erro ao inserir mercado: Retorno -1")
            } else {
                Log.d("DatabaseHelper", "Mercado inserido com sucesso. ID: $id")
            }
            id
        } catch (e: SQLException) {
            Log.e("DatabaseHelper", "Erro ao inserir mercado: ${e.message}")
            -1 // Retorna erro em caso de falha
        } finally {
            db.close()
        }
    }

    // Método para listar todos os mercados
    fun listarMercados(): List<String> {
        val mercados = mutableListOf<String>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.query(
            TABLE_MERCADOS,
            arrayOf(COLUMN_MERCADO_NOME, COLUMN_ENDERECO),
            null, null, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val nome = it.getString(it.getColumnIndexOrThrow(COLUMN_MERCADO_NOME))
                val endereco = it.getString(it.getColumnIndexOrThrow(COLUMN_ENDERECO))
                val mercadoComEndereco = "$nome - $endereco"
                mercados.add(mercadoComEndereco)
            }
        }
        db.close()
        return mercados
    }

    // Método para inserir um novo usuário
    fun inserirUsuario(nome: String, email: String, dataNascimento: String, endereco: String, senha: String): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            Log.e("DatabaseHelper", "Erro: Dados inválidos para inserção")
            db.close()
            return -1 // Retorna erro
        }

        values.apply {
            put(COLUMN_USUARIO_NOME, nome)
            put(COLUMN_USUARIO_EMAIL, email)
            put(COLUMN_USUARIO_DATA_NASCIMENTO, dataNascimento)
            put(COLUMN_USUARIO_ENDERECO, endereco)
            put(COLUMN_USUARIO_SENHA, senha)
        }

        return try {
            db.insert(TABLE_USUARIOS, null, values)
        } catch (e: SQLException) {
            Log.e("DatabaseHelper", "Erro ao inserir usuário: ${e.message}")
            -1 // Retorna erro em caso de falha
        }
    }

    // Método para buscar usuário por email, retornando um Cursor
    fun buscarUsuarioCursorPorEmail(email: String): Cursor? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIOS WHERE $COLUMN_USUARIO_EMAIL = ?"
        return db.rawQuery(query, arrayOf(email))
    }

    // Método para buscar usuário por email, retornando um Usuario
    fun buscarUsuarioPorEmail(email: String): Usuario? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USUARIOS,
            arrayOf(
                COLUMN_USUARIO_ID,
                COLUMN_USUARIO_NOME,
                COLUMN_USUARIO_EMAIL,
                COLUMN_USUARIO_SENHA,
                COLUMN_USUARIO_DATA_NASCIMENTO,
                COLUMN_USUARIO_ENDERECO
            ),
            "$COLUMN_USUARIO_EMAIL = ?",
            arrayOf(email),
            null, null, null
        )

        var usuario: Usuario? = null
        if (cursor != null && cursor.moveToFirst()) {
            // Verificar se o índice das colunas é válido
            val nomeIndex = cursor.getColumnIndex(COLUMN_USUARIO_NOME)
            val emailIndex = cursor.getColumnIndex(COLUMN_USUARIO_EMAIL)
            val dataNascimentoIndex = cursor.getColumnIndex(COLUMN_USUARIO_DATA_NASCIMENTO)
            val enderecoIndex = cursor.getColumnIndex(COLUMN_USUARIO_ENDERECO)

            if (nomeIndex >= 0 && emailIndex >= 0 && dataNascimentoIndex >= 0 && enderecoIndex >= 0) {
                val nome = cursor.getString(nomeIndex)
                val emailFetched = cursor.getString(emailIndex)
                val dataNascimento = cursor.getString(dataNascimentoIndex) // Pega a data de nascimento
                val endereco = cursor.getString(enderecoIndex) // Pega o endereço

                // Cria o usuário com os novos campos
                usuario = Usuario(nome, emailFetched, dataNascimento, endereco)
            }
        }

        cursor?.close()
        db.close()
        return usuario
    }

    // Método para atualizar um usuário
    fun atualizarUsuario(id: Int, nome: String, email: String, dataNascimento: String, endereco: String, senha: String): Int {
        val db = this.writableDatabase
        val values = ContentValues()

        values.apply {
            put(COLUMN_USUARIO_NOME, nome)
            put(COLUMN_USUARIO_EMAIL, email)
            put(COLUMN_USUARIO_DATA_NASCIMENTO, dataNascimento)
            put(COLUMN_USUARIO_ENDERECO, endereco)
            put(COLUMN_USUARIO_SENHA, senha)
        }

        val result = db.update(
            TABLE_USUARIOS,
            values,
            "$COLUMN_USUARIO_ID = ?",
            arrayOf(id.toString())
        )

        db.close()
        return result
    }

    // Método para excluir um usuário por email
    fun excluirContaPorEmail(email: String): Boolean {
        val db = this.writableDatabase
        return try {
            val rowsDeleted = db.delete(
                TABLE_USUARIOS,
                "$COLUMN_USUARIO_EMAIL = ?",
                arrayOf(email)
            )
            // Retorna true se a exclusão foi bem-sucedida (pelo menos uma linha excluída)
            rowsDeleted > 0
        } catch (e: SQLException) {
            Log.e("DatabaseHelper", "Erro ao excluir usuário: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    // Método para deletar todos os usuários
    fun deletarTodosUsuarios(): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_USUARIOS, null, null)
        db.close()
        return result
    }

    // Método para fechar o banco de dados
    fun fechar() {
        this.close()
    }

    // Método para verificar login
    fun verificarLogin(email: String, senha: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USUARIOS WHERE $COLUMN_USUARIO_EMAIL = ? AND $COLUMN_USUARIO_SENHA = ?",
            arrayOf(email, senha)
        )

        val loginValido = cursor.count > 0
        cursor.close()
        return loginValido
    }

    fun verificarSenhaAtualPorEmail(email: String, senhaAtual: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_USUARIO_SENHA FROM $TABLE_USUARIOS WHERE $COLUMN_USUARIO_EMAIL = ?",
            arrayOf(email)
        )

        if (cursor != null && cursor.moveToFirst()) {
            val senhaSalva = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUARIO_SENHA))
            cursor.close()
            return senhaSalva == senhaAtual
        }

        cursor?.close()
        return false
    }

    // Atualizar senha usando o e-mail
    fun atualizarSenhaPorEmail(email: String, novaSenha: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USUARIO_SENHA, novaSenha)
        }

        val rowsUpdated = db.update(
            TABLE_USUARIOS,
            values,
            "$COLUMN_USUARIO_EMAIL = ?",
            arrayOf(email)
        )

        return rowsUpdated > 0
    }

    //Hash da Senha
    private fun hashSenha(senha: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(senha.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    // Função de busca que busca tanto na tabela de produtos quanto na tabela de mercados
    fun buscarPorTermo(termo: String): List<String> {
        val resultados = mutableListOf<String>()
        val db = this.readableDatabase

        // Buscar na tabela de produtos
        val queryProdutos = "SELECT * FROM $TABLE_PRODUTOS WHERE $COLUMN_PRODUTO LIKE ? OR $COLUMN_MARCA LIKE ?"
        val cursorProdutos: Cursor? = db.rawQuery(queryProdutos, arrayOf("%$termo%", "%$termo%"))

        cursorProdutos?.use {
            while (it.moveToNext()) {
                // Recuperando todas as colunas da tabela de produtos
                val produtoId = it.getInt(it.getColumnIndexOrThrow(COLUMN_ID))
                val nomeProduto = it.getString(it.getColumnIndexOrThrow(COLUMN_PRODUTO))
                val marcaProduto = it.getString(it.getColumnIndexOrThrow(COLUMN_MARCA))
                val tamanhoProduto = it.getString(it.getColumnIndexOrThrow(COLUMN_TAMANHO))
                val precoProduto = it.getDouble(it.getColumnIndexOrThrow(COLUMN_PRECO))
                val mercadoProduto = it.getString(it.getColumnIndexOrThrow(COLUMN_MERCADO))

                // Adicionando todos os dados do produto na lista de resultados
                resultados.add("Produto ID: $produtoId - Nome: $nomeProduto - Marca: $marcaProduto - Tamanho: $tamanhoProduto - Preço: R$$precoProduto - Mercado: $mercadoProduto")
            }
        }

        db.close()
        return resultados
    }
}