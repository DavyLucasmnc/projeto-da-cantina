package com.example.telas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnNovaVenda;
    private Button btnHistoricoVendas;
    private Button btnCadastrarProduto;
    private Button btnGerenciarProdutos;
    private Button btnSair; // Botão novo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarComponentes();
        configurarListeners();
    }

    private void inicializarComponentes() {
        btnNovaVenda = findViewById(R.id.button_nova_venda);
        btnHistoricoVendas = findViewById(R.id.button_historico_vendas);
        btnCadastrarProduto = findViewById(R.id.button_cadastrar_produto);
        btnGerenciarProdutos = findViewById(R.id.button_gerenciar_produtos);
        btnSair = findViewById(R.id.button_sair); // Encontra o novo botão
    }

    private void configurarListeners() {
        // ... listeners dos outros botões ...
        btnNovaVenda.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, VendasActivity.class)));
        btnHistoricoVendas.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, HistoricoVendasActivity.class)));
        btnCadastrarProduto.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, CadastroProdutoActivity.class)));
        btnGerenciarProdutos.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, GerenciarProdutosActivity.class)));

        // Isoo aqui configura a ação de clique para o botão "Sair"
        btnSair.setOnClickListener(v -> {
            // Acessa as preferências
            SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Limpa todos os dados salvos (remove o "usuarioLogado")
            editor.clear();
            editor.apply();

            // Envia o usuário de volta para a tela de login
            Intent intent = new Intent(HomeActivity.this, formLogin.class);
            // Flags para limpar o histórico de telas e não permitir "voltar" para a Home
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}