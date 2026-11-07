package com.example.etapp_02;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MuralFragment extends Fragment {

    private SharedPreferences sp;
    private LinearLayout filtros, lista;

    public MuralFragment() {
        super(R.layout.fragment_mural);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        sp = Util.sp(requireContext());
        filtros = v.findViewById(R.id.filtros);
        lista = v.findViewById(R.id.lista);

        for (int i = 0; i < filtros.getChildCount(); i++) {
            TextView t = (TextView) filtros.getChildAt(i);
            t.setOnClickListener(x -> {
                sp.edit().putString("filtro", t.getText().toString()).apply();
                aplicar();
            });
        }
        aplicar();

        Util.link(v.findViewById(R.id.btnCursos), "construcao");
        Util.link(v.findViewById(R.id.btnTestes), "construcao");
        Util.link(v.findViewById(R.id.btnOngs), "construcao");
        Util.link(v.findViewById(R.id.btnBolsa), "construcao");
        Util.link(v.findViewById(R.id.btnEstagio), "construcao");

        ligar(v.findViewById(R.id.btnAgendaDia), "agenda_dia");
        ligar(v.findViewById(R.id.btnAgendaPalestra), "agenda_palestra");
    }

    private void aplicar() {
        String f = sp.getString("filtro", "Todas");

        for (int i = 0; i < filtros.getChildCount(); i++) {
            TextView t = (TextView) filtros.getChildAt(i);
            boolean sel = t.getText().toString().equals(f);
            t.setBackgroundResource(sel ? R.drawable.bg_preto : R.drawable.bg_cinza);
            t.setTextColor(sel ? Color.WHITE : Color.BLACK);
        }

        for (int i = 0; i < lista.getChildCount(); i++) {
            View c = lista.getChildAt(i);
            c.setVisibility(f.equals("Todas") || f.equals(c.getTag()) ? View.VISIBLE : View.GONE);
        }
    }

    private void ligar(Button botao, String chave) {
        botao.setOnClickListener(x -> {
            sp.edit().putBoolean(chave, !sp.getBoolean(chave, false)).apply();
            pintar(botao, chave);
        });
        pintar(botao, chave);
    }

    private void pintar(Button botao, String chave) {
        boolean on = sp.getBoolean(chave, false);
        botao.setText(on ? " Na agenda" : "+ Agenda");
        botao.setBackgroundTintList(ColorStateList.valueOf(on ? 0xFFDDDDDD : 0xFFF5B301));
    }
}