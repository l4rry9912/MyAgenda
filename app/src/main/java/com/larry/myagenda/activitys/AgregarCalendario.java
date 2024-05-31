package com.larry.myagenda.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.larry.myagenda.DB.BDCalendarios;
import com.larry.myagenda.objetos.IconosUtils;
import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Iconos;
import com.larry.myagenda.adaptadores.IconosAdapter;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AgregarCalendario extends AppCompatActivity {

    // Iconos
    private RecyclerView recyclerView;
    private IconosAdapter adapter;

    // Variables
    private Iconos iconoSeleccionado;
    private int colorSeleccionado = Color.WHITE;
    private int colorporDefecto = Color.WHITE;
    private Button Btn_seleccionarcolor;
    private EditText NombreCalendarioET;

    // Firebase
    private FirebaseAuth mAuth;

    // Base de datos
    private BDCalendarios.CalendarioDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_calendario);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar TextView
        NombreCalendarioET = findViewById(R.id.NombreCalendarioET);

        // Inicializar botones y EditText
        Button Btn_guardarcalendario = findViewById(R.id.Btn_guardarcalendario);
        Btn_seleccionarcolor = findViewById(R.id.Btn_seleccionarcolor);

        // Inicializar base de datos
        dbHelper = new BDCalendarios.CalendarioDBHelper(this);

        // Botón seleccionar color
        Btn_seleccionarcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionadorColor();
            }
        });

        // Botón guardar
        Btn_guardarcalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                    String nombreCalendario = NombreCalendarioET.getText().toString().trim();
                    if (!nombreCalendario.isEmpty() && iconoSeleccionado != null) {
                        guardarCalendario(userId, nombreCalendario, iconoSeleccionado.getRuta(), colorSeleccionado);
                        finish();
                    } else {
                        Toast.makeText(AgregarCalendario.this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // El usuario no está autenticado, manejar el caso según tus requerimientos
                    Toast.makeText(AgregarCalendario.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Iconos
        recyclerView = findViewById(R.id.recyclerViewCal);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new IconosAdapter(getIconosList());
        recyclerView.setAdapter(adapter);
        adapter.setOnIconoClickListener(new IconosAdapter.OnIconoClickListener() {
            @Override
            public void onIconoClick(Iconos icono) {
                iconoSeleccionado = icono;
            }
        });
    }

    private void seleccionadorColor() {
        ColorPickerDialogBuilder
                .with(this)
                .initialColor(colorporDefecto)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(15)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        Btn_seleccionarcolor.setBackgroundColor(selectedColor);
                    }
                })
                .setPositiveButton("Seleccionar", (dialog, selectedColor, allColors) -> {
                    colorSeleccionado = selectedColor;
                    String mensaje = "Color elegido correctamente: #" + Integer.toHexString(selectedColor);
                    Toast.makeText(AgregarCalendario.this, mensaje, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                })
                .build()
                .show();
    }

    private List<Iconos> getIconosList() {
        return IconosUtils.getIconosList();
    }


    private void guardarCalendario(String userId, String nombreCalendario, String iconoRuta, int color) {
        long resultado = dbHelper.insertarCalendario(userId, nombreCalendario, iconoRuta, color);
        if (resultado != -1) {
            Toast.makeText(this, "Calendario guardado exitosamente", Toast.LENGTH_SHORT).show();
            // Limpiar campos después de guardar
            NombreCalendarioET.setText("");
            iconoSeleccionado = null;
            colorSeleccionado = Color.WHITE;
            Btn_seleccionarcolor.setBackgroundColor(colorporDefecto);
        } else {
            Toast.makeText(this, "Error al guardar el calendario", Toast.LENGTH_SHORT).show();
        }

    }
}
