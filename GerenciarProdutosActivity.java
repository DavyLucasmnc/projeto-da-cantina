package com.example.telas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import banco_de_dados.GerenciadorVenda;
import banco_de_dados.Produto;

public class GerenciarProdutosActivity extends AppCompatActivity {

    private ListView listViewProdutos;
    private GerenciadorVenda gerenciadorVenda;
    private List<Produto> listaProdutos;
    private ArrayAdapter<Produto> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_produtos);

        listViewProdutos = findViewById(R.id.listViewProdutos);
        gerenciadorVenda = new GerenciadorVenda(this);

        // Ao clicar em um item, agora abre um menu de opções
        listViewProdutos.setOnItemClickListener((parent, view, position, id) -> {
            Produto produtoSelecionado = listaProdutos.get(position);
            mostrarDialogoOpcoes(produtoSelecionado);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarProdutos();
    }

    private void carregarProdutos() {
        listaProdutos = gerenciadorVenda.listarProdutos();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaProdutos);
        listViewProdutos.setAdapter(adapter);
    }

    private void mostrarDialogoOpcoes(final Produto produto) {
        final CharSequence[] options = {"Editar", "Excluir", "Cancelar"};

        new AlertDialog.Builder(this)
                .setTitle("O que você deseja fazer?")
                .setItems(options, (dialog, which) -> {
                    if (options[which].equals("Editar")) {
                        mostrarDialogoEditar(produto);
                    } else if (options[which].equals("Excluir")) {
                        mostrarDialogoConfirmacaoExcluir(produto);
                    } else if (options[which].equals("Cancelar")) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

// Dentro da classe GerenciarProdutosActivity.java

    private void mostrarDialogoEditar(final Produto produtoParaEditar) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_editar_produto, null);

        final EditText editTextNome = dialogView.findViewById(R.id.editTextDialogNomeProduto);
        final EditText editTextPreco = dialogView.findViewById(R.id.editTextDialogPrecoProduto);
        final EditText editTextEstoque = dialogView.findViewById(R.id.editTextDialogEstoqueProduto);
        final EditText editTextDescricao = dialogView.findViewById(R.id.editTextDialogDescricaoProduto);

        // Preenche todos os campos com os dados atuais do produto
        editTextNome.setText(produtoParaEditar.getNome());
        editTextPreco.setText(String.valueOf(produtoParaEditar.getPreco()));
        editTextEstoque.setText(String.valueOf(produtoParaEditar.getEstoque()));
        editTextDescricao.setText(produtoParaEditar.getDescricao());

        new AlertDialog.Builder(this)
                .setTitle("Editar Produto")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String novoNome = editTextNome.getText().toString().trim();
                    String novoPrecoStr = editTextPreco.getText().toString().trim();
                    String novoEstoqueStr = editTextEstoque.getText().toString().trim();
                    String novaDescricao = editTextDescricao.getText().toString().trim();

                    if (novoNome.isEmpty() || novoPrecoStr.isEmpty() || novoEstoqueStr.isEmpty()) {
                        Toast.makeText(this, "Nome, Preço and Estoque não podem ser vazios.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double novoPreco = Double.parseDouble(novoPrecoStr);
                        int novoEstoque = Integer.parseInt(novoEstoqueStr);

                        Produto produtoAtualizado = new Produto(produtoParaEditar.getId(), novoNome, novoPreco, novoEstoque, novaDescricao);
                        boolean sucesso = gerenciadorVenda.atualizarProduto(produtoAtualizado);

                        if (sucesso) {
                            Toast.makeText(this, "Produto atualizado!", Toast.LENGTH_SHORT).show();
                            carregarProdutos();
                        } else {
                            Toast.makeText(this, "Falha ao atualizar o produto.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Por favor, insira valores numéricos válidos para Preço e Estoque.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void mostrarDialogoConfirmacaoExcluir(final Produto produto) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja remover o produto '" + produto.getNome() + "' do catálogo?\n\n(Atenção: produtos que já foram vendidos não podem ser removidos).")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    boolean sucesso = gerenciadorVenda.deletarProduto(produto.getId());
                    if (sucesso) {
                        Toast.makeText(this, "Produto removido com sucesso!", Toast.LENGTH_SHORT).show();
                        carregarProdutos();
                    } else {
                        Toast.makeText(this, "Erro: Este produto não pode ser removido pois já faz parte de um histórico de venda.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}