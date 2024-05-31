package com.larry.myagenda.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.larry.myagenda.DB.BDCalendarios;
import com.larry.myagenda.R;

import java.util.Locale;

public class AgregarTareaActivity extends AppCompatActivity {

    private EditText etNombreTarea;
    private EditText etDescripcionTarea;
    private TimePicker timePickerHoraTarea;
    private Button btnGuardarTarea;
    private BDCalendarios.CalendarioDBHelper dbHelper;
    private String calendarioId;
    private int diaSeleccionado;
    private int mesSeleccionado;
    private int añoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_tarea);

        // Inicializar la base de datos
        dbHelper = new BDCalendarios.CalendarioDBHelper(this);

        // Obtener el ID del calendario y los datos de fecha de los argumentos
        if (getIntent().getExtras() != null) {
            calendarioId = getIntent().getStringExtra("calendarioId");
            diaSeleccionado = getIntent().getIntExtra("diaSeleccionado", 0);
            mesSeleccionado = getIntent().getIntExtra("mesSeleccionado", 0);
            añoSeleccionado = getIntent().getIntExtra("añoSeleccionado", 0);
        }

        // Inicializar vistas
        etNombreTarea = findViewById(R.id.editTextNombreTarea);
        etDescripcionTarea = findViewById(R.id.editTextDescripcionTarea);
        timePickerHoraTarea = findViewById(R.id.timePickerHoraTarea);
        btnGuardarTarea = findViewById(R.id.buttonGuardarTarea);

        // Configurar listener de clic para el botón para guardar la tarea
        btnGuardarTarea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTarea();
            }
        });
    }

    private void guardarTarea() {
        String nombre = etNombreTarea.getText().toString().trim();
        String descripcion = etDescripcionTarea.getText().toString().trim();

        // Crear una instancia de Calendar para obtener la fecha completa
        Calendar calendar = Calendar.getInstance();
        calendar.set(añoSeleccionado, mesSeleccionado, diaSeleccionado, timePickerHoraTarea.getHour(), timePickerHoraTarea.getMinute());

        // Obtener la fecha completa en milisegundos
        long fecha = calendar.getTimeInMillis();

        // Formatear la fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm",  Locale.getDefault());
        String fechaFormateada = sdf.format(calendar.getTime());

        // Verificar que todos los campos estén completos
        if (nombre.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insertar la tarea en la base de datos con el ID del calendario y la fecha completa
        long resultado = BDCalendarios.CalendarioDBHelper.insertarTarea(this, calendarioId, nombre, descripcion, fechaFormateada);

        if (resultado != -1) {
            // Tarea guardada correctamente
            Toast.makeText(this, "Tarea guardada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            // Error al guardar la tarea
            Toast.makeText(this, "Error al guardar la tarea", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}