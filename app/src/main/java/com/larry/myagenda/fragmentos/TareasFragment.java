package com.larry.myagenda.fragmentos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.larry.myagenda.DB.BDCalendarios;
import com.larry.myagenda.R;
import com.larry.myagenda.adaptadores.TareasAdapter;
import com.larry.myagenda.objetos.Tarea;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TareasFragment extends Fragment {

    private TareasAdapter adapter;
    private BDCalendarios.CalendarioDBHelper dbHelper;

    private static final String TAG = "TareasFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tareas, container, false);

        // Recupera los argumentos
        int day = getArguments().getInt("diaSeleccionado", -1);
        int month = getArguments().getInt("mesSeleccionado", -1);
        int year = getArguments().getInt("añoSeleccionado", -1);
        String calendarioId = getArguments().getString("calendarioId");
        boolean mostrarTodasLasTareas = getArguments().getBoolean("mostrarTodasLasTareas", false);

        //inicializar bd
        dbHelper = new BDCalendarios.CalendarioDBHelper(getContext());


        // Si no se proporcionan argumentos válidos, no hacer nada
        if (calendarioId == null) {
            return rootView;
        }
        // Cargar tareas desde la base de datos
        List<Tarea> tareas;

        // cargar todas las tareas para el calendario
        if (mostrarTodasLasTareas || (day == -1 || month == -1 || year == -1)) {
            tareas = obtenerTodasLasTareasDeCalendario(getContext(), calendarioId);
        } else {
            // Si se proporciona una fecha específica, cargar las tareas para ese día
            tareas = obtenerTareasDeCalendarioParaDia(getContext(), calendarioId, day, month, year);
        }

        // Configurar el RecyclerView para mostrar las tareas
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerViewTareas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TareasAdapter(getContext(), tareas); // Asignar el adaptador a la variable de clase
        recyclerView.setAdapter(adapter);

        // Establecer el listener de doble clic en la tarea
        adapter.setOnTareaDoubleClickListener(new TareasAdapter.OnTareaDoubleClickListener() {
            @Override
            public void onTareaDoubleClick(Tarea tarea) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(("¿Seguro que quieres eliminar la tarea " + tarea.getTitulo() + "?"))
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper.eliminarTarea(tarea.getId());
                                adapter.eliminarTarea(tarea);
                                Toast.makeText(getActivity(), "Tarea eliminada exitosamente", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        return rootView;

    }
    public List<Tarea> obtenerTareasDeCalendarioParaDia(Context context, String calendarioId, int day, int month, int year) {
        List<Tarea> listaTareas = new ArrayList<>();

        // Formatear la fecha del día específico en el formato esperado (dd/MM/yyyy)
        String fechaDiaString = String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year);

        // Llamar al método estático de la clase BDCalendarios para obtener las tareas del calendario específico
        Cursor cursor = BDCalendarios.CalendarioDBHelper.obtenerTareasDeCalendario(context, calendarioId);

        // Verificar si se encontraron tareas para este calendario
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtener los datos de la tarea de la base de datos
                String id = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada._ID)));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada.COLUMN_TITULO));
                String contenido = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada.COLUMN_CONTENIDO));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada.COLUMN_FECHA));

                // Extraer solo la parte de la fecha (dd/MM/yyyy) para la comparación
                String fechaSolo = fecha.split(" - ")[0];

                // Filtrar las tareas para incluir solo las del día específico
                if (fechaSolo.equals(fechaDiaString)) {
                    // Crear un objeto Tarea con los datos obtenidos y añadirlo a la lista
                    Tarea tarea = new Tarea(id, calendarioId, titulo, contenido, fecha); // Cambia calendarioId a String
                    listaTareas.add(tarea);
                }
            } while (cursor.moveToNext());
            // Cerrar el cursor
            cursor.close();
        }

        return listaTareas;
    }
    public List<Tarea> obtenerTodasLasTareasDeCalendario(Context context, String calendarioId) {
        List<Tarea> listaTareas = new ArrayList<>();

        // Llamar al método estático de la clase BDCalendarios para obtener todas las tareas del calendario específico
        Cursor cursor = BDCalendarios.CalendarioDBHelper.obtenerTareasDeCalendario(context, calendarioId);

        // Verificar si se encontraron tareas para este calendario
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtener los datos de la tarea de la base de datos
                String id = String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada._ID)));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada.COLUMN_TITULO));
                String contenido = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada.COLUMN_CONTENIDO));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada.COLUMN_FECHA));

                // Crear un objeto Tarea con los datos obtenidos y añadirlo a la lista
                Tarea tarea = new Tarea(id, calendarioId, titulo, contenido, fecha); // Cambia calendarioId a String
                listaTareas.add(tarea);
            } while (cursor.moveToNext());
            // Cerrar el cursor
            cursor.close();
        }

        return listaTareas;
    }

}


