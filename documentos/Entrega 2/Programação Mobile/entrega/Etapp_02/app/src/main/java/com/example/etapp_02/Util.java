package com.example.etapp_02;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

public class Util {

    public static SharedPreferences sp(Context c) {
        return c.getSharedPreferences("passo", Context.MODE_PRIVATE);
    }

    public static void abrir(Context c, String tipo) {
        Intent i = new Intent(c, StatusActivity.class);
        i.putExtra("tipo", tipo);
        c.startActivity(i);
    }

    public static void link(View v, String tipo) {
        v.setOnClickListener(x -> abrir(v.getContext(), tipo));
    }
}
