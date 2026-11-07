package com.example.etapp_02;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private final String[] titulos = {"Início", "Agenda", "Mural", "Perfil"};

    @Override
    protected void onCreate(Bundle b) {
        EdgeToEdge.enable(this);
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        View raiz = findViewById(R.id.raiz);
        ViewPager2 pager = findViewById(R.id.viewPager);
        TabLayout abas = findViewById(R.id.tabLayout);

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets s = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, s.top, 0, 0);
            abas.setPadding(0, 0, 0, s.bottom);
            return insets;
        });

        pager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int p) {
                switch (p) {
                    case 1: return new AgendaFragment();
                    case 2: return new MuralFragment();
                    case 3: return new PerfilFragment();
                    default: return new InicioFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int p) {
                raiz.setBackgroundColor(p == 0 ? 0xFFF5B301 : 0xFFFFFFFF);
            }
        });

        new TabLayoutMediator(abas, pager, (tab, p) -> tab.setText(titulos[p])).attach();
    }
}