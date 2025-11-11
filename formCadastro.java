package com.example.telas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import banco_de_dados.GerenciadorVenda;
import banco_de_dados.Usuario;

public class formCadastro extends AppCompatActivity {

    private EditText editEmail, editSenha, editConfirmarSenha;
    private Button btnCadastrar;
    private GerenciadorVenda gerenciador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Garante que o nome do layout está correto
        setContentView(R.layout.form_cadastro);

        gerenciador = new GerenciadorVenda(this);

        // Garante que os IDs correspondem aos do XML
        editEmail = findViewById(R.id.edit_email_cadastro);
        editSenha = findViewById(R.id.edit_senha_cadastro);
        editConfirmarSenha = findViewById(R.id.edit_confirmar_senha_cadastro);
        btnCadastrar = findViewById(R.id.btn_cadastrar);

        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());
    }

    private void cadastrarUsuario() {
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();
        String confirmarSenha = editConfirmarSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!senha.equals(confirmarSenha)) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario novoUsuario = new Usuario(email, senha);
        long resultado = gerenciador.inserirUsuario(novoUsuario);

        if (resultado != -1) {
            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            finish(); // Volta para a tela de login
        } else {
            Toast.makeText(this, "Erro ao cadastrar. O email já pode estar em uso.", Toast.LENGTH_LONG).show();
        }
    }
}