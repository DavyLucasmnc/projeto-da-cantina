package com.example.telas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import banco_de_dados.GerenciadorVenda;

public class formLogin extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private Button btnEntrar;
    // A CORREÇÃO IMPORTANTE ESTÁ AQUI:
    private CheckBox checkBoxManterConectado; // Estava com "Contextado"
    private TextView textTelaCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mantido como o seu original, que está correto
        setContentView(R.layout.activity_form_login);

        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        btnEntrar = findViewById(R.id.btn_entrar);
        checkBoxManterConectado = findViewById(R.id.checkbox_manter_conectado);
        textTelaCadastro = findViewById(R.id.text_tela_cadastro);

        btnEntrar.setOnClickListener(v -> realizarLogin());

        textTelaCadastro.setOnClickListener(v -> {
            Intent intent = new Intent(formLogin.this, formCadastro.class);
            startActivity(intent);
        });
    }

    // Dentro da classe formLogin.java

    private void realizarLogin() {
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()){
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Acessa o gerenciador para verificar o login no banco
        GerenciadorVenda gerenciador = new GerenciadorVenda(this);
        boolean loginValido = gerenciador.checarLogin(email, senha);

        if (loginValido) {
            // ... (salvar dados de login e ir para a HomeActivity) ...
            if (checkBoxManterConectado.isChecked()) {
                salvarPreferenciaLogin();
            }
            Intent intent = new Intent(formLogin.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Email ou senha inválidos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarPreferenciaLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("usuarioLogado", true);
        editor.apply();
    }
}