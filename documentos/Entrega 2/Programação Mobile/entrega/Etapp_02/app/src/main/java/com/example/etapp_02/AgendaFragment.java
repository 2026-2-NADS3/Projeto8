package com.example.etapp_02;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AgendaFragment extends Fragment {

    private SharedPreferences sp;
    private Button vou, naoVou;

    public AgendaFragment() {
        super(R.layout.fragment_agenda);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        sp = Util.sp(requireContext());
        vou = v.findViewById(R.id.btnVou);
        naoVou = v.findViewById(R.id.btnNaoVou);

        vou.setOnClickListener(x -> escolher("sim"));
        naoVou.setOnClickListener(x -> escolher("nao"));
        v.findViewById(R.id.btnRegistrar).setOnClickListener(x ->
                startActivity(new Intent(requireContext(), RegistrarActivity.class)));
        pintar();
    }

    private void escolher(String r) {
        sp.edit().putString("vou_dia", r).apply();
        pintar();
    }

    private void pintar() {
        String r = sp.getString("vou_dia", "");
        vou.setBackgroundTintList(ColorStateList.valueOf(r.equals("sim") ? 0xFFF5B301 : 0xFFFFFFFF));
        naoVou.setBackgroundTintList(ColorStateList.valueOf(r.equals("nao") ? 0xFFF5B301 : 0xFFFFFFFF));
    }
}