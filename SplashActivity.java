package com.example.telas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Acessa as preferências para verificar se já existe um usuário logado
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean usuarioLogado = sharedPreferences.getBoolean("usuarioLogado", false);

        // Se 'usuarioLogado' for true, vai direto para a Home
        if (usuarioLogado) {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            // Se não, vai para a tela de Login
            Intent intent = new Intent(SplashActivity.this, formLogin.class);
            startActivity(intent);
        }

        // Finaliza esta activity "roteadora" para que o usuário não possa voltar para ela
        finish();
    }
}