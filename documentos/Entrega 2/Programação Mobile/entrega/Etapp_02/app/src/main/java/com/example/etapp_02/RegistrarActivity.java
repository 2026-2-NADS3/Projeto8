package com.example.etapp_02;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class RegistrarActivity extends AppCompatActivity {

    private final ActivityResultLauncher<ScanOptions> leitor =
            registerForActivityResult(new ScanContract(), r -> {
                String c = r.getContents();
                if (c == null) return;

                if (c.startsWith("http://") || c.startsWith("https://")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(c)));
                } else {
                    Util.sp(this).edit().putBoolean("confirmada", true).apply();
                    Intent i = new Intent(this, StatusActivity.class);
                    i.putExtra("tipo", "confirmada");
                    i.putExtra("texto", c);
                    startActivity(i);
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_registrar);

        findViewById(R.id.voltar).setOnClickListener(v -> finish());
        Util.link(findViewById(R.id.ajuda), "construcao");

        findViewById(R.id.botaoLer).setOnClickListener(v -> {
            ScanOptions o = new ScanOptions();
            o.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            o.setPrompt("Aponte para o QR Code");
            o.setBeepEnabled(false);
            o.setOrientationLocked(false);
            leitor.launch(o);
        });
    }
}
