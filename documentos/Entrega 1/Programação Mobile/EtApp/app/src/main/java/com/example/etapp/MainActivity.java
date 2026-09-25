package com.example.etapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    // Variáveis
    Button btn_01;
    EditText input_01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Pegando o Id
        btn_01 = findViewById(R.id.btn_01);
        input_01 = findViewById(R.id.input_01);

        // Define o que acontece quando o botão "ENTRAR" é clicado
        btn_01.setOnClickListener(v -> {
            // Pega o texto digitado no campo de nome
            String nome = input_01.getText().toString().trim();

            // Cria um "Intent": um pedido para abrir outra tela (SecondActivity)
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);

            // Anexa o nome digitado ao Intent, com a chave "nome",
            // para a próxima tela conseguir ler esse valor
            intent.putExtra("nome", nome);

            startActivity(intent);
        });
    }
}