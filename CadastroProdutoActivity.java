package com.example.telas;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import banco_de_dados.GerenciadorVenda;
import banco_de_dados.Produto;

public class CadastroProdutoActivity extends AppCompatActivity {

    private EditText edtNome, edtPreco, edtEstoque, edtDescricao;
    private Button btnCadastrar;
    private GerenciadorVenda gerenciadorVenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // A CORREÇÃO PRINCIPAL ESTÁ AQUI:
        // Garantindo que estamos carregando o layout correto de cadastro de PRODUTO.
        setContentView(R.layout.activity_cadastro_produto);

        // Instanciando o GerenciadorVenda
        gerenciadorVenda = new GerenciadorVenda(this);

        // Associando as variáveis aos componentes corretos do layout
        edtNome = findViewById(R.id.tiet_nome_produto);
        edtPreco = findViewById(R.id.tiet_preco_produto);
        edtEstoque = findViewById(R.id.tiet_quantidade);
        edtDescricao = findViewById(R.id.tiet_descricao_produto);
        btnCadastrar = findViewById(R.id.button_cadastrar_produto);

        btnCadastrar.setOnClickListener(v -> cadastrarProduto());
    }

    private void cadastrarProduto() {
        String nome = edtNome.getText().toString().trim();
        String precoStr = edtPreco.getText().toString().trim();
        String estoqueStr = edtEstoque.getText().toString().trim();
        String descricao = edtDescricao.getText().toString().trim();

        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(precoStr) || TextUtils.isEmpty(estoqueStr)) {
            mostrarAlerta("Erro de Validação", "Nome, Preço e Estoque são campos obrigatórios.");
            return;
        }

        double preco;
        int estoque;

        try {
            preco = Double.parseDouble(precoStr);
            estoque = Integer.parseInt(estoqueStr);
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "Por favor, insira um número válido para Preço e Estoque.");
            return;
        }

        Produto novoProduto = new Produto(0, nome, preco, estoque, descricao);
        long resultado = gerenciadorVenda.inserirProduto(novoProduto);

        if (resultado != -1) {
            new AlertDialog.Builder(this)
                    .setTitle("Sucesso")
                    .setMessage("Produto '" + nome + "' cadastrado com sucesso!")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> finish()) // Apenas fecha a tela
                    .show();
        } else {
            mostrarAlerta("Erro", "Falha ao cadastrar o produto. Verifique se o nome já existe.");
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("OK", null)
                .show();
    }
}