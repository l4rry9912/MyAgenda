package com.larry.myagenda.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Nota;

import java.util.Locale;

public class AgregarNota extends AppCompatActivity {

    DatabaseReference userNotesRef;
    FirebaseAuth auth;
    FirebaseUser user;
    private TextView editTextTitulo, editTextDescripcion, editTextContenido;
    private Button btnGuardar;
    private Nota notaActual;

    private static final int AGREGAR_NOTA_REQUEST_CODE = 1; // Código de solicitud único

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nota);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Obtener referencia a la base de datos para las notas del usuario actual
        if (user != null) {
            String userId = user.getUid();
            userNotesRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId).child("notas");
        } else {
            // Manejo de error: El usuario no está autenticado
            // Puedes mostrar un mensaje o redirigir al usuario a la pantalla de inicio de sesión
            Toast.makeText(AgregarNota.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Finaliza la actividad actual
            return; // Salir del método onCreate
        }

        // Inicializar vistas
        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        editTextContenido = findViewById(R.id.editTextContenido);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Verificar si se pasó una nota como extra en el Intent
        if (getIntent().hasExtra("nota")) {
            // Si se pasó una nota, cargar los detalles de la nota en los campos correspondientes
            notaActual = (Nota) getIntent().getSerializableExtra("nota");
            if (notaActual != null) {
                editTextTitulo.setText(notaActual.getTitulo());
                editTextDescripcion.setText(notaActual.getDescripcion());
                editTextContenido.setText(notaActual.getContenido());
            }
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarNota();
            }
        });
    }

    private void guardarNota() {
        String titulo = editTextTitulo.getText().toString().trim();
        String descripcion = editTextDescripcion.getText().toString().trim();
        String contenido = editTextContenido.getText().toString().trim();
        String fecha = obtenerFechaActual();

        if (!TextUtils.isEmpty(titulo)) {
            // Verificar si la nota actual es nula o si hay cambios en los campos
            if (notaActual == null || !titulo.equals(notaActual.getTitulo())
                    || !descripcion.equals(notaActual.getDescripcion())
                    || !contenido.equals(notaActual.getContenido())) {
                String notaId;

                // Si la nota actual es nula, significa que estamos agregando una nueva nota
                if (notaActual == null) {
                    notaId = userNotesRef.push().getKey();
                } else {
                    // Si la nota actual no es nula, significa que estamos editando una nota existente
                    notaId = notaActual.getId();
                }

                // Crear objeto Nota con los datos ingresados por el usuario
                Nota nota = new Nota(notaId, titulo, descripcion, contenido, fecha);

                // Guardar la nota en la base de datos
                userNotesRef.child(notaId).setValue(nota);
                Toast.makeText(AgregarNota.this, "Nota guardada exitosamente", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish();// Finalizar la actividad después de guardar la nota
            } else {
                // Mostrar un mensaje si no hay cambios en la nota
                Toast.makeText(AgregarNota.this, "No se realizaron cambios en la nota", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Mostrar un mensaje de error si el título está vacío
            Toast.makeText(AgregarNota.this, "Por favor, ingresa un título para la nota", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para obtener la fecha actual
    private String obtenerFechaActual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}


