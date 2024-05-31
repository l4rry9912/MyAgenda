package com.larry.myagenda.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.larry.myagenda.R;

public class LoginActivity extends AppCompatActivity {

    EditText CorreoLogin, ContraseñaLogin;
    TextView Notienecuenta;
    ProgressDialog progressDialog;
    Button Btn_ingresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CorreoLogin = findViewById(R.id.CorreoLogin);
        ContraseñaLogin = findViewById(R.id.ContraseñaLogin);
        Notienecuenta = findViewById(R.id.Notienecuenta);
        Btn_ingresar = findViewById(R.id.Btn_ingresar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Iniciando sesión...");

        Btn_ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtiene el correo electrónico y la contraseña ingresados por el usuario
                String email = CorreoLogin.getText().toString().trim();
                String password = ContraseñaLogin.getText().toString().trim();

                // Verifica si los campos están vacíos.
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Por favor, ingresa tu correo y contraseña.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Muestra el ProgressDialog.
                progressDialog.show();

                //Autentica al usuario con Firebase
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss(); // Oculta el ProgressDialog.

                                if (task.isSuccessful()){
                                    // Si la autenticación es exitosa, abre la actividad del menú principal.
                                    startActivity(new Intent(LoginActivity.this, MenuPrincipal.class));
                                    Toast.makeText(getApplicationContext(), "Bienvenid@", Toast.LENGTH_SHORT).show();
                                    finish(); // Cierra esta actividad para evitar que el usuario pueda volver atrás.
                                }else {
                                    // Si la autenticación falla, muestra un mensaje de error.
                                    Toast.makeText(LoginActivity.this, "Error al iniciar sesión. Verifica tus credenciales.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });
        Notienecuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
                finish();
            }
        });

    }
}