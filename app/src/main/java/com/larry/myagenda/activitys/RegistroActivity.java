package com.larry.myagenda.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Usuarios;

public class RegistroActivity extends AppCompatActivity {

    EditText nombreET, correoET, contraseET, conf_contraseñaET;
    TextView tengo_cuenta;
    Button Btn_Registro;
    FirebaseAuth Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        nombreET = findViewById(R.id.nombreET);
        correoET = findViewById(R.id.correoET);
        contraseET = findViewById(R.id.contraseET);
        conf_contraseñaET = findViewById(R.id.conf_contraseñaET);
        tengo_cuenta = findViewById(R.id.tengo_cuenta);
        Btn_Registro = findViewById(R.id.Btn_Registro);
        Auth = FirebaseAuth.getInstance();

        Btn_Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
        tengo_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registrarUsuario() {
        final String nombre = nombreET.getText().toString().trim();
        final String correo = correoET.getText().toString().trim();
        final String contraseña = contraseET.getText().toString().trim();
        String confirmarContraseña = conf_contraseñaET.getText().toString().trim();

        // Validar que los campos no estén vacíos
        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(correo) || TextUtils.isEmpty(contraseña) || TextUtils.isEmpty(confirmarContraseña)) {
            Toast.makeText(getApplicationContext(), "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar el formato del correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(getApplicationContext(), "Ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar que las contraseñas coincidan
        if (!contraseña.equals(confirmarContraseña)) {
            Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creando su cuenta...");
        progressDialog.show();

        // Registrar usuario en Firebase Authentication
        Auth.createUserWithEmailAndPassword(correo, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Registro exitoso
                            FirebaseUser user = Auth.getCurrentUser();
                            if (user != null) {
                                // Guardar el nombre en el perfil de usuario
                                user.updateProfile(new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nombre)
                                        .build());
                                // Guardar datos del usuario en la base de datos Firebase
                                guardarDatosUsuarioFirebase(user.getUid(), nombre, correo, contraseña);
                                startActivity(new Intent(RegistroActivity.this, MenuPrincipal.class));
                                Toast.makeText(getApplicationContext(), "Bienvenido, " + nombre, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(getApplicationContext(), "Correo electrónico ya registrado, ¿quieres iniciar sesión?", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error al registrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void guardarDatosUsuarioFirebase(String uid, String nombre, String correo, String contraseña) {
        // Guardar datos del usuario en la base de datos Firebase
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        Usuarios usuario = new Usuarios(nombre, correo, contraseña);
        usuariosRef.child(uid).setValue(usuario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                            Toast.makeText(getApplicationContext(), "Error al guardar datos del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
