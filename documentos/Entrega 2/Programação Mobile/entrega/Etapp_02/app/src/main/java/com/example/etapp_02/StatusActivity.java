package com.example.etapp_02;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_status);

        String tipo = getIntent().getStringExtra("tipo");
        String extra = getIntent().getStringExtra("texto");
        if (tipo == null) tipo = "construcao";

        String t = "Em construção";
        String s = "Essa página ainda está sendo preparada.";
        String bt = "Voltar";
        String simb = null;
        int fundo = 0xFFF5B301;
        int pe = 0xFF111111;

        switch (tipo) {
            case "internet":
                fundo = 0xFFF5F5F5;
                pe = 0;
                simb = "📶";
                t = "Sem internet";
                s = "O que já está salvo continua aqui. Sincronizo quando a conexão voltar.";
                bt = "Tentar de novo";
                break;
            case "erro":
                fundo = 0xFFFFFFFF;
                t = "Algo deu errado";
                s = "Não consegui carregar agora. Tente de novo em instantes.";
                bt = "Tentar de novo";
                break;
            case "vazio":
                pe = 0xFFFFFFFF;
                t = "Sem novidades por enquanto";
                s = "Quando surgir bolsa, prova ou evento da sua área, eu aviso.";
                bt = "Ver oportunidades";
                break;
            case "confirmada":
                pe = 0;
                simb = "✓";
                t = "Presença confirmada!";
                s = extra != null ? extra : "Palestra · 11/11 · 12h30";
                bt = "Ver minhas presenças";
                break;
        }

        findViewById(R.id.raiz).setBackgroundColor(fundo);

        ImageView icone = findViewById(R.id.icone);
        if (pe == 0) {
            icone.setVisibility(View.GONE);
        } else {
            icone.setColorFilter(pe);
        }

        TextView simbolo = findViewById(R.id.simbolo);
        if (simb != null) {
            simbolo.setVisibility(View.VISIBLE);
            simbolo.setText(simb);
            if (tipo.equals("confirmada")) {
                simbolo.setBackgroundResource(R.drawable.bg_circulo);
                simbolo.setBackgroundTintList(ColorStateList.valueOf(0xFFFFFFFF));
            }
        }

        ((TextView) findViewById(R.id.titulo)).setText(t);
        ((TextView) findViewById(R.id.texto)).setText(s);

        TextView botao = findViewById(R.id.botao);
        botao.setText(bt);
        botao.setOnClickListener(v -> finish());
    }
}
