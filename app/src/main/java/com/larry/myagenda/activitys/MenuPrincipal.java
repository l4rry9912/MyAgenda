package com.larry.myagenda.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.larry.myagenda.R;
import com.larry.myagenda.fragmentos.AjustesFragment;
import com.larry.myagenda.fragmentos.HomeFragment;
import com.larry.myagenda.fragmentos.NotasFragment;

public class MenuPrincipal extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        //INICIALIZAR VARIABLES
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.notas_menu) {
                    LoadFragment(new NotasFragment(), false);

                } else if (itemId == R.id.home_menu) {
                    LoadFragment(new HomeFragment(), false);

                } else if (itemId == R.id.ajustes_menu) {
                    LoadFragment(new AjustesFragment(), false);

                }

                return true;
            }
        });

        LoadFragment(new NotasFragment(), true);
    }

    private void LoadFragment(Fragment fragment, boolean isAppInitialized) {
        // Obtener el administrador de fragmentos
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Obtener el fragmento actualmente visible en el contenedor
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragment_container);
        // Verificar si el fragmento que se va a cargar es el mismo que el actual
        boolean isSameFragment = currentFragment != null && currentFragment.getClass().getName().equals(fragment.getClass().getName());

        // Crear una transacción de fragmentos
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Si la aplicación se ha inicializado y el fragmento no es el mismo que el actual
        if (isAppInitialized && !isSameFragment) {
            // Agregar el nuevo fragmento al contenedor
            fragmentTransaction.add(R.id.fragment_container, fragment);
        } else {
            // Reemplazar el fragmento actual con el nuevo fragmento en el contenedor
            fragmentTransaction.replace(R.id.fragment_container, fragment);
        }
        // Confirmar la transacción
        fragmentTransaction.commit();
    }
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment instanceof NotasFragment || fragment instanceof HomeFragment || fragment instanceof AjustesFragment) {
            // Mueve la tarea de la aplicación a un segundo plano
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }
}