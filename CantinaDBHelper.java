package banco_de_dados;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CantinaDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cantina.db";
    private static final int DATABASE_VERSION = 6;

    // Definições de todas as tabelas
    private static final String TABELA_USUARIOS = "USUARIOS";
    private static final String TABELA_PRODUTOS = "PRODUTOS";
    private static final String TABELA_VENDAS = "VENDAS";
    private static final String TABELA_ITEM_VENDA = "ITEM_VENDA";

    public CantinaDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // --- 1. SQL para criar a Tabela USUARIOS ---
        String sqlUsuarios = "CREATE TABLE " + TABELA_USUARIOS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "email TEXT NOT NULL UNIQUE, "
                + "senha TEXT NOT NULL);";
        db.execSQL(sqlUsuarios);

        // --- 2. SQL para criar a Tabela PRODUTOS ---
        String sqlProdutos = "CREATE TABLE " + TABELA_PRODUTOS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome TEXT NOT NULL UNIQUE, "
                + "preco REAL NOT NULL, "
                + "estoque INTEGER DEFAULT 0, "
                + "descricao TEXT);";
        db.execSQL(sqlProdutos);

        // --- 3. SQL para criar a Tabela VENDAS ---
        String sqlVenda = "CREATE TABLE " + TABELA_VENDAS + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "nome_cliente TEXT NOT NULL, "
                + "data_venda TEXT NOT NULL, "
                + "valor_total REAL NOT NULL, "
                + "metodo_pagamento TEXT);"; // Incluindo a coluna de método de pagamento
        db.execSQL(sqlVenda);

        // --- 4. SQL para criar a Tabela ITEM_VENDA ---
        String sqlItemVenda = "CREATE TABLE " + TABELA_ITEM_VENDA + "("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "id_venda INTEGER NOT NULL, "
                + "id_produto INTEGER NOT NULL, "
                + "quantidade INTEGER NOT NULL, "
                + "preco_no_momento_da_venda REAL NOT NULL, "
                + "FOREIGN KEY(id_venda) REFERENCES " + TABELA_VENDAS + "(_id), "
                + "FOREIGN KEY(id_produto) REFERENCES " + TABELA_PRODUTOS + "(_id));";
        db.execSQL(sqlItemVenda);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Apaga TODAS as tabelas antigas na ordem correta
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ITEM_VENDA);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_VENDAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_PRODUTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIOS);
        // Recria toda a estrutura do banco de dados
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}