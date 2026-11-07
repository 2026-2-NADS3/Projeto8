package com.example.etapp_02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private static final String URL_BASE = "http://10.0.2.2:3000/";

    private boolean cadastro = false;
    private TextView titulo, trocar, aviso;
    private EditText nome, email, senha, confirma;
    private CheckBox manter;
    private Button botao;

    @Override
    protected void onCreate(Bundle b) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(b);

        if (Util.sp(this).getBoolean("manter", false)) {
            irParaMain();
            return;
        }

        setContentView(R.layout.activity_login);
        titulo = findViewById(R.id.titulo);
        trocar = findViewById(R.id.trocar);
        aviso = findViewById(R.id.aviso);
        nome = findViewById(R.id.nome);
        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        confirma = findViewById(R.id.confirma);
        manter = findViewById(R.id.manter);
        botao = findViewById(R.id.botao);

        email.setText(Util.sp(this).getString("email", ""));
        botao.setOnClickListener(v -> enviar());
        trocar.setOnClickListener(v -> alternar());
    }

    private void alternar() {
        cadastro = !cadastro;
        nome.setVisibility(cadastro ? View.VISIBLE : View.GONE);
        confirma.setVisibility(cadastro ? View.VISIBLE : View.GONE);
        manter.setVisibility(cadastro ? View.GONE : View.VISIBLE);
        titulo.setText(cadastro ? "Criar conta" : "Entrar");
        botao.setText(cadastro ? "Cadastre-se" : "Entrar");
        trocar.setText(cadastro ? "Já tem login? Entrar" : "Ainda não tem conta? Cadastre-se");
        aviso.setText("");
    }

    private void enviar() {
        String n = nome.getText().toString().trim();
        String em = email.getText().toString().trim();
        String s = senha.getText().toString();

        if (em.isEmpty() || s.isEmpty()) {
            aviso.setText("Preencha e-mail e senha");
            return;
        }
        if (cadastro && (n.isEmpty() || !s.equals(confirma.getText().toString()))) {
            aviso.setText("Preencha o nome e repita a mesma senha");
            return;
        }
        aviso.setText("");

        new Thread(() -> {
            int codigo = -1;
            String corpo = "";
            try {
                JSONObject j = new JSONObject();
                j.put("nome", n);
                j.put("email", em);
                j.put("senha", s);

                HttpURLConnection c = (HttpURLConnection)
                        new URL(URL_BASE + (cadastro ? "registrar" : "login")).openConnection();
                c.setConnectTimeout(5000);
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type", "application/json");
                c.setDoOutput(true);

                OutputStream o = c.getOutputStream();
                o.write(j.toString().getBytes("UTF-8"));
                o.close();

                codigo = c.getResponseCode();
                if (codigo == 200 && !cadastro) {
                    corpo = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine();
                }
            } catch (Exception ex) {
                android.util.Log.e("API", "falhou", ex);
                codigo = -1;
            }
            int cod = codigo;
            String cp = corpo;
            runOnUiThread(() -> resposta(cod, cp, em));
        }).start();

    }

    private void resposta(int codigo, String corpo, String em) {
        if (codigo == -1) {
            Util.abrir(this, "internet");
            return;
        }
        if (codigo == 401) {
            aviso.setText("E-mail ou senha incorretos");
            return;
        }
        if (codigo == 409) {
            aviso.setText("Esse e-mail já tem conta");
            return;
        }
        if (codigo != 200) {
            Util.abrir(this, "erro");
            return;
        }
        if (cadastro) {
            alternar();
            aviso.setText("Conta criada! Agora é só entrar.");
            return;
        }
        try {
            JSONObject j = new JSONObject(corpo);
            Util.sp(this).edit()
                    .putString("nome", j.getString("nome"))
                    .putString("email", em)
                    .putBoolean("manter", manter.isChecked())
                    .apply();
            irParaMain();
        } catch (Exception ex) {
            Util.abrir(this, "erro");
        }
    }

    private void irParaMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}