package com.larry.myagenda.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larry.myagenda.R;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //INICIALIZAR VARIABLES
        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                VerificarUsuario();
            }
        },1500);
    }

    private void VerificarUsuario() {
        //METODO PARA VERIFICAR SI EL USUARIO YA HA INICIADO SESION ANTES
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }else{
            startActivity(new Intent(SplashActivity.this, MenuPrincipal.class));
            finish();
        }

    }
}