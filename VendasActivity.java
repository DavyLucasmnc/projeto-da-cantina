package com.example.telas;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import banco_de_dados.GerenciadorVenda;
import banco_de_dados.ItemVenda;
import banco_de_dados.Produto;

public class VendasActivity extends AppCompatActivity {

    private EditText editTextNomeCliente;
    private Spinner spinnerProdutos;
    private Spinner spinnerPagamento;
    private EditText editTextQuantidade;
    private TextView textViewItensCompra;
    private TextView textViewTotal;
    private Button btnAdicionarProduto;
    private Button btnFinalizarCompra;

    private GerenciadorVenda gerenciadorVenda;
    private List<ItemVenda> carrinho = new ArrayList<>();
    private double totalVenda = 0.0;
    private List<Produto> listaDeProdutosDisponiveis = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendas);

        editTextNomeCliente = findViewById(R.id.editTextMatricula);
        spinnerProdutos = findViewById(R.id.spinnerProdutos);
        spinnerPagamento = findViewById(R.id.spinnerPagamento);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        textViewItensCompra = findViewById(R.id.textViewItensCompra);
        textViewTotal = findViewById(R.id.textViewTotal);
        btnAdicionarProduto = findViewById(R.id.btnAdicionarProduto);
        btnFinalizarCompra = findViewById(R.id.btnFinalizarCompra);

        gerenciadorVenda = new GerenciadorVenda(this);

        carregarProdutosDoBanco();
        configurarSpinnerPagamento();

        btnAdicionarProduto.setOnClickListener(v -> adicionarProdutoAoCarrinho());
        textViewItensCompra.setOnClickListener(v -> mostrarDialogoRemoverItem());
        btnFinalizarCompra.setOnClickListener(v -> finalizarVenda());
    }

    private void configurarSpinnerPagamento() {
        String[] metodos = {"Dinheiro", "PIX", "Cartão de Crédito", "Cartão de Débito"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_custom,
                metodos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPagamento.setAdapter(adapter);
    }

    private void carregarProdutosDoBanco() {
        listaDeProdutosDisponiveis = gerenciadorVenda.listarProdutos();
        if (listaDeProdutosDisponiveis.isEmpty()) {
            Toast.makeText(this, "Nenhum produto cadastrado!", Toast.LENGTH_LONG).show();
            btnAdicionarProduto.setEnabled(false);
            spinnerProdutos.setEnabled(false);
            return;
        }
        btnAdicionarProduto.setEnabled(true);
        spinnerProdutos.setEnabled(true);
        ArrayAdapter<Produto> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_custom,
                listaDeProdutosDisponiveis);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProdutos.setAdapter(adapter);
    }

    private void adicionarProdutoAoCarrinho() {
        Produto produtoSelecionado = (Produto) spinnerProdutos.getSelectedItem();
        if (produtoSelecionado == null) {
            Toast.makeText(this, "Nenhum produto selecionado.", Toast.LENGTH_SHORT).show();
            return;
        }
        int quantidade;
        try {
            quantidade = Integer.parseInt(editTextQuantidade.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantidade inválida.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (quantidade <= 0) {
            Toast.makeText(this, "A quantidade deve ser maior que zero.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (quantidade > produtoSelecionado.getEstoque()) {
            Toast.makeText(this, "Estoque insuficiente! Apenas " + produtoSelecionado.getEstoque() + " unidades disponíveis.", Toast.LENGTH_LONG).show();
            return;
        }
        ItemVenda novoItem = new ItemVenda(
                produtoSelecionado.getId(),
                quantidade,
                produtoSelecionado.getPreco()
        );
        carrinho.add(novoItem);
        totalVenda += (novoItem.precoNoMomentoDaVenda * novoItem.quantidade);
        editTextQuantidade.setText("1");
        Toast.makeText(this, produtoSelecionado.getNome() + " adicionado.", Toast.LENGTH_SHORT).show();
        atualizarExibicaoCarrinho();
    }

    private void mostrarDialogoRemoverItem() {
        if (carrinho.isEmpty()) {
            Toast.makeText(this, "O carrinho já está vazio.", Toast.LENGTH_SHORT).show();
            return;
        }
        ArrayList<String> itensNomes = new ArrayList<>();
        for (ItemVenda item : carrinho) {
            String nomeProduto = buscarNomeProdutoPorId(item.idProduto);
            itensNomes.add(String.format(Locale.getDefault(), "%d x %s", item.quantidade, nomeProduto));
        }
        new AlertDialog.Builder(this)
                .setTitle("Remover Item do Carrinho")
                .setItems(itensNomes.toArray(new String[0]), (dialog, which) -> {
                    ItemVenda itemParaRemover = carrinho.get(which);
                    totalVenda -= (itemParaRemover.precoNoMomentoDaVenda * itemParaRemover.quantidade);
                    carrinho.remove(which);
                    atualizarExibicaoCarrinho();
                    Toast.makeText(this, "Item removido.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void finalizarVenda() {
        if (carrinho.isEmpty()) {
            Toast.makeText(this, "O carrinho está vazio!", Toast.LENGTH_SHORT).show();
            return;
        }

        String nomeCliente = editTextNomeCliente.getText().toString().trim();
        if (nomeCliente.isEmpty()) {
            nomeCliente = "Anônimo";
        }
        String metodoPagamento = spinnerPagamento.getSelectedItem().toString();

        long idVenda = gerenciadorVenda.inserirVenda(nomeCliente, totalVenda, metodoPagamento);

        if (idVenda > 0) {
            for (ItemVenda item : carrinho) {
                gerenciadorVenda.inserirItemVenda(idVenda, item);
                gerenciadorVenda.decrementarEstoque(item.idProduto, item.quantidade);
            }

            Toast.makeText(this, "Venda #" + idVenda + " finalizada!", Toast.LENGTH_LONG).show();

            limparTelaParaNovaVenda();
        } else {
            Toast.makeText(this, "Erro ao registrar a venda.", Toast.LENGTH_LONG).show();
        }
    }

    private void limparTelaParaNovaVenda() {
        carrinho.clear();
        totalVenda = 0.0;
        editTextNomeCliente.setText("");
        spinnerPagamento.setSelection(0);
        atualizarExibicaoCarrinho();
        carregarProdutosDoBanco();
    }

    private void atualizarExibicaoCarrinho() {
        StringBuilder builder = new StringBuilder();
        if (carrinho.isEmpty()) {
            builder.append("--- Carrinho Vazio ---");
        } else {
            for (ItemVenda item : carrinho) {
                String nomeProduto = buscarNomeProdutoPorId(item.idProduto);
                double subtotal = item.precoNoMomentoDaVenda * item.quantidade;
                builder.append(String.format(Locale.getDefault(),
                        "%d x %s (Subtotal: R$ %.2f)\n",
                        item.quantidade,
                        nomeProduto,
                        subtotal
                ));
            }
        }
        textViewItensCompra.setText(builder.toString());
        textViewTotal.setText(String.format(Locale.getDefault(), "Total: R$ %.2f", totalVenda));
    }

    private String buscarNomeProdutoPorId(long idProduto) {
        for (Produto p : listaDeProdutosDisponiveis) {
            if (p.getId() == idProduto) {
                return p.getNome();
            }
        }
        return "Produto não encontrado";
    }
}