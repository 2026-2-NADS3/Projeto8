package com.example.etapp_02;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PerfilFragment extends Fragment {

    public PerfilFragment() {
        super(R.layout.fragment_perfil);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        SharedPreferences sp = Util.sp(requireContext());

        ((TextView) v.findViewById(R.id.textNome)).setText(sp.getString("nome", ""));
        ((TextView) v.findViewById(R.id.textEmail)).setText("E-mail · " + sp.getString("email", ""));

        Util.link(v.findViewById(R.id.btnCard), "construcao");
        Util.link(v.findViewById(R.id.btnCert1), "construcao");
        Util.link(v.findViewById(R.id.btnCert2), "construcao");
        Util.link(v.findViewById(R.id.btnConfig), "construcao");

        v.findViewById(R.id.btnSair).setOnClickListener(x -> {
            sp.edit().putBoolean("manter", false).apply();
            Intent i = new Intent(requireContext(), LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}
