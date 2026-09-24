package com.example.etapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SecondActivity extends AppCompatActivity {

    Button btn_config, btn_02, btn_03;
    TextView cardTitle, cardText1, cardText2, cardText3, title_01, title_03;
    ImageView img_perfil;

    // começa na página 2 (a do meio)
    int pagina = 2;

    String[] titulos = {
            "Perfil",
            "Eventos",
            "Notificações"
    };

    String[][] textos = {
            // página 1
            {"Nome: ", "Idade: 18", "Instituição: FECAP"},
            // página 2
            {"|------❯ \nPALESTRA: \n10/12 -Rua das Palmeiras, 120 -Sala 4 // 14h00-15h00", "|------❯ \nPALESTRA: \n11/11 -Avenida das Acácias, 850 // 12h30-13h30", "|------❯ \nDIA DE CARREIRA: \n8/12 -Travessa dos Ipês, 33 // 16h00-16h30"},
            // página 3
            {"- Novo exame de lógica de programação para cursos de Administração e Contabilidade disponivel no site oficial www.cursos/entrada/012349... LER MAIS.", "- Concurso público de bacos como Caixa Economica, Banco do Brasil e outros abrem vaga de nivel medio e superior nessa sexta-feira dia... LER MAIS.", "- SEM MAIS NOTIFICAÇÕES..."}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.segunda_tela);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.segunda), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_config = findViewById(R.id.btn_config);
        btn_02 = findViewById(R.id.btn_02);
        btn_03 = findViewById(R.id.btn_03);

        cardTitle = findViewById(R.id.cardTitle);
        cardText1 = findViewById(R.id.cardText1);
        cardText2 = findViewById(R.id.cardText2);
        cardText3 = findViewById(R.id.cardText3);
        title_01 = findViewById(R.id.title_01);
        title_03 = findViewById(R.id.title_03);

        img_perfil = findViewById(R.id.img_perfil);

        String nome = getIntent().getStringExtra("nome");
        if (nome == null || nome.isEmpty()) {
            nome = "usuário";
        }

        title_03.setText("Olá, " + nome);
        textos[0][0] = "Nome: " + nome;

        btn_config.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
            startActivity(intent);
        });

        // botão voltar (diminui)
        btn_02.setOnClickListener(v -> {
            if (pagina > 1) {
                pagina--;
                atualizarTela();
            }
        });

        // botão avançar (aumenta)
        btn_03.setOnClickListener(v -> {
            if (pagina < 3) {
                pagina++;
                atualizarTela();
            }
        });

        // mostra a página 2 logo ao abrir
        atualizarTela();
    }

    private void atualizarTela() {
        int i = pagina - 1; // arrays começam em 0

        cardTitle.setText(titulos[i]);
        cardText1.setText(textos[i][0]);
        cardText2.setText(textos[i][1]);
        cardText3.setText(textos[i][2]);

        // opcional: destaca a bolinha da página atual no "o o o"
        if (pagina == 1) title_01.setText("● ○ ○");
        else if (pagina == 2) title_01.setText("○ ● ○");
        else title_01.setText("○ ○ ●");

        if (pagina == 1) {
            img_perfil.setVisibility(View.VISIBLE);
        } else {
            img_perfil.setVisibility(View.GONE);
        }
    }

}
