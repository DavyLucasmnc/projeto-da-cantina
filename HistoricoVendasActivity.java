package com.example.telas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;
import java.util.Locale;

import banco_de_dados.GerenciadorVenda;
import banco_de_dados.RegistroVenda;

public class HistoricoVendasActivity extends AppCompatActivity {

    private ListView listViewHistorico;
    private GerenciadorVenda gerenciadorVenda;
    private ArrayAdapter<RegistroVenda> adapter;
    private List<RegistroVenda> vendas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_vendas);

        // 1. Encontra e configura a Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_historico);
        setSupportActionBar(toolbar);

        // 2. Encontra os outros componentes
        listViewHistorico = findViewById(R.id.listViewHistorico);
        gerenciadorVenda = new GerenciadorVenda(this);

        // 3. Configura os cliques na lista
        // Clique curto: Mostra detalhes da venda
        listViewHistorico.setOnItemClickListener((parent, view, position, id) -> {
            RegistroVenda vendaSelecionada = vendas.get(position);
            mostrarDialogoDetalhes(vendaSelecionada);
        });

        // Clique longo: Oferece a opção de excluir a venda selecionada
        listViewHistorico.setOnItemLongClickListener((parent, view, position, id) -> {
            RegistroVenda vendaParaExcluir = vendas.get(position);
            confirmarExclusaoUnica(vendaParaExcluir);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarHistorico();
    }

    private void carregarHistorico() {
        vendas = gerenciadorVenda.listarVendas();
        if (vendas == null || vendas.isEmpty()) {
            Toast.makeText(this, "Nenhuma venda encontrada.", Toast.LENGTH_SHORT).show();
            listViewHistorico.setAdapter(null);
        } else {
            // Usa o novo Adapter customizado para exibir os itens
            adapter = new ArrayAdapter<RegistroVenda>(this, 0, vendas) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.historico_item_custom, parent, false);
                    }

                    TextView tvPrincipal = convertView.findViewById(R.id.textViewHistoricoPrincipal);
                    TextView tvSecundario = convertView.findViewById(R.id.textViewHistoricoSecundario);

                    RegistroVenda venda = getItem(position);

                    if (venda != null) {
                        String principal = String.format(Locale.getDefault(),
                                "ID: %d | Cliente: %s | Total: R$ %.2f",
                                venda.id, venda.nomeCliente, venda.valorTotal);
                        tvPrincipal.setText(principal);

                        String dataFormatada = (venda.dataVenda != null && venda.dataVenda.length() >= 16) ? venda.dataVenda.substring(0, 16) : "Data indisponível";
                        String secundario = String.format(Locale.getDefault(),
                                "Pagamento: %s | Data: %s",
                                (venda.metodoPagamento != null ? venda.metodoPagamento : "-"),
                                dataFormatada);
                        tvSecundario.setText(secundario);
                    }
                    return convertView;
                }
            };
            listViewHistorico.setAdapter(adapter);
        }
    }

    private void mostrarDialogoDetalhes(RegistroVenda venda) {
        List<String> itensDaVenda = gerenciadorVenda.buscarItensDetalhesPorVendaId(venda.id);
        StringBuilder detalhes = new StringBuilder();

        if (itensDaVenda.isEmpty()) {
            detalhes.append("Nenhum item detalhado encontrado para esta venda.");
        } else {
            for (int i = 0; i < itensDaVenda.size(); i++) {
                detalhes.append(itensDaVenda.get(i));
                if (i < itensDaVenda.size() - 1) {
                    detalhes.append("\n--------------------\n");
                }
            }
        }
        new AlertDialog.Builder(this)
                .setTitle("Detalhes da Venda #" + venda.id)
                .setMessage(detalhes.toString())
                .setPositiveButton("Fechar", null)
                .show();
    }

    private void confirmarExclusaoUnica(final RegistroVenda venda) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Venda")
                .setMessage("Tem certeza que deseja apagar o registro da venda #" + venda.id + "?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    gerenciadorVenda.deletarVendaPorId(venda.id);
                    Toast.makeText(this, "Venda #" + venda.id + " excluída.", Toast.LENGTH_SHORT).show();
                    carregarHistorico();
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.historico_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_limpar_historico) {
            confirmarLimparHistorico();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void confirmarLimparHistorico() {
        new AlertDialog.Builder(this)
                .setTitle("Limpar Todo o Histórico")
                .setMessage("Tem certeza que deseja apagar TODOS os registros de vendas?")
                .setPositiveButton("Limpar Tudo", (dialog, which) -> {
                    gerenciadorVenda.limparHistoricoVendas();
                    Toast.makeText(this, "Histórico de vendas apagado.", Toast.LENGTH_SHORT).show();
                    carregarHistorico();
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}