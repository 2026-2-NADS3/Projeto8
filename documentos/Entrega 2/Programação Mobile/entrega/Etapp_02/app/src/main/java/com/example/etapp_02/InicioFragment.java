package com.example.etapp_02;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InicioFragment extends Fragment {

    private SharedPreferences sp;
    private TextView aviso;
    private Button confirmar;

    public InicioFragment() {
        super(R.layout.fragment_inicio);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        sp = Util.sp(requireContext());
        aviso = v.findViewById(R.id.textAviso);
        confirmar = v.findViewById(R.id.btnConfirmar);

        ((TextView) v.findViewById(R.id.textOla)).setText("Olá, " + sp.getString("nome", ""));

        confirmar.setOnClickListener(x -> {
            sp.edit().putBoolean("confirmada", true).apply();
            atualizar();
            Util.abrir(requireContext(), "confirmada");
        });

        Util.link(v.findViewById(R.id.btnSino), "vazio");
        Util.link(v.findViewById(R.id.btnAjuda), "construcao");
        Util.link(v.findViewById(R.id.btnPasso), "construcao");
        atualizar();
    }

    @Override
    public void onResume() {
        super.onResume();
        atualizar();
    }

    private void atualizar() {
        boolean ok = sp.getBoolean("confirmada", false);
        aviso.setText(ok ? "Presença confirmada na palestra de quinta, 14h."
                : "Sua palestra é quinta, 14h. Falta confirmar presença.");
        confirmar.setText(ok ? "Presença confirmada ✓" : "Confirmar presença");
    }
}