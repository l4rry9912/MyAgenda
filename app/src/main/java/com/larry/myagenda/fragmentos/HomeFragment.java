package com.larry.myagenda.fragmentos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larry.myagenda.DB.BDCalendarios;
import com.larry.myagenda.R;
import com.larry.myagenda.adaptadores.IconoCalendarioAdapter;
import com.larry.myagenda.activitys.AgregarCalendario;
import com.larry.myagenda.objetos.Calendario;
import com.larry.myagenda.objetos.Tarea;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String PREF_SELECTED_CALENDAR_ID = "selected_calendar_id";
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerViewCalendarios;
    private IconoCalendarioAdapter adapter;
    private List<Calendario> calendarioList;
    private List<Tarea> tareaList;
    private TextView nombreCalendarioTextView;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Inicializar Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Inicializar SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Inicializar RecyclerView
        recyclerViewCalendarios = rootView.findViewById(R.id.ViewMostrarCalendario);
        recyclerViewCalendarios.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Inicializar la lista de calendarios
        calendarioList = new ArrayList<>();

        // Inicializar el TextView
        nombreCalendarioTextView = rootView.findViewById(R.id.nombreCalendarioTextView);

        FloatingActionButton fab = rootView.findViewById(R.id.fabAddCalendario);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la nueva actividad de agregar calendario
                Intent intent = new Intent(getActivity(), AgregarCalendario.class);
                startActivity(intent);
            }
        });
        // Ocultar o mostrar el FAB dependiendo del scroll del RecyclerView
        recyclerViewCalendarios.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fab.isShown()) {
                    fab.hide();
                } else if (dy < 0 && !fab.isShown()) {
                    fab.show();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Actualizar la interfaz de usuario con los datos más recientes
        actualizarInterfazUsuario();

        // Seleccionar automáticamente el calendario almacenado o el primer calendario si no hay ninguno almacenado
        if (adapter != null && !calendarioList.isEmpty()) {
            String selectedCalendarId = sharedPreferences.getString(PREF_SELECTED_CALENDAR_ID, null);
            if (selectedCalendarId != null) {
                // Buscar el calendario seleccionado por ID
                for (Calendario calendario : calendarioList) {
                    if (calendario.getId().equals(selectedCalendarId)) {
                        adapter.seleccionarCalendario(calendario);
                        break;
                    }
                }
            }
        }
    }

    public void actualizarInterfazUsuario() {
        // Limpiar la lista actual
        calendarioList.clear();

        // Obtener el usuario actual
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Obtener los calendarios del usuario actual desde la base de datos SQLite
            String userId = user.getUid();
            BDCalendarios.CalendarioDBHelper dbHelper = new BDCalendarios.CalendarioDBHelper(getActivity());
            Cursor cursor = dbHelper.obtenerCalendariosDelUsuario(getActivity(), userId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.CalendarioEntrada._ID));
                    String nombre = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.CalendarioEntrada.COLUMN_NOMBRE));
                    String iconoRuta = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.CalendarioEntrada.COLUMN_ICONO_RUTA));
                    int color = cursor.getInt(cursor.getColumnIndexOrThrow(BDCalendarios.CalendarioEntrada.COLUMN_COLOR));

                    // Crear un objeto Calendario y agregarlo a la lista
                    Calendario calendario = new Calendario(id, nombre, iconoRuta, color);
                    calendarioList.add(calendario);
                } while (cursor.moveToNext());

                // Cerrar el cursor después de usarlo
                cursor.close();
            }
            if (adapter == null) {
                adapter = new IconoCalendarioAdapter(calendarioList, getContext());
                recyclerViewCalendarios.setAdapter(adapter);

                // Establecer el listener para el clic en el calendario
                adapter.setOnCalendarioClickListener(new IconoCalendarioAdapter.OnCalendarioClickListener() {
                    @Override
                    public void onCalendarioClick(Calendario calendario) {
                        // Obtener el FragmentManager
                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                        // Crear una instancia del CalendarioFragment y configurar cualquier dato necesario
                        CalendarioFragment calendarioFragment = new CalendarioFragment();

                        // Pasar cualquier dato necesario al CalendarioFragment, por ejemplo, el ID del calendario seleccionado
                        Bundle args = new Bundle();
                        args.putString("calendarioId", calendario.getId());
                        calendarioFragment.setArguments(args);

                        // Reemplazar el contenido del FrameLayout con el CalendarioFragment
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayoutCalendario, calendarioFragment)
                                .addToBackStack(null)
                                .commit();
                        nombreCalendarioTextView.setText(calendario.getNombre());

                        // Guardar el ID del calendario seleccionado en SharedPreferences
                        sharedPreferences.edit().putString(PREF_SELECTED_CALENDAR_ID, calendario.getId()).apply();

                        // Crear una instancia del TareasFragment y pasar el ID del calendario seleccionado como argumento
                        TareasFragment tareasFragment = new TareasFragment();
                        Bundle tareasArgs = new Bundle();
                        tareasArgs.putString("calendarioId", calendario.getId());
                        tareasArgs.putBoolean("mostrarTodasLasTareas", true);
                        tareasFragment.setArguments(tareasArgs);

                        // Utilizar el FragmentManager para reemplazar el contenido del FrameLayout en HomeFragment con el TareasFragment
                        fragmentManager.beginTransaction()
                                .replace(R.id.frameLayoutTareas, tareasFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                // En el método onCreateView o en algún lugar adecuado donde configures el adaptador
                adapter.setOnCalendarioDoubleClickListener(new IconoCalendarioAdapter.OnCalendarioDoubleClickListener() {
                    @Override
                    public void onCalendarioDoubleClick(Calendario calendario) {
                        // Mostrar un diálogo de confirmación para eliminar el calendario
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(("¿Seguro que quieres eliminar el calendario " + "(" + nombreCalendarioTextView.getText() + ")" + "?"))
                                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbHelper.eliminarCalendario(calendario.getId());
                                        Toast.makeText(getActivity(), "Calendario eliminado exitosamente", Toast.LENGTH_SHORT).show();
                                        actualizarInterfazUsuario();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // No hacer nada, simplemente cerrar el diálogo
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                });


            } else {
                // Notificar al adaptador sobre los cambios en los datos
                adapter.notifyDataSetChanged();
            }
        }
    }
}



