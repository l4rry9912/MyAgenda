package com.larry.myagenda.adaptadores;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import com.larry.myagenda.DB.BDCalendarios;
import com.larry.myagenda.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class CalendarioAdapter extends BaseAdapter {

    private Context context;
    private List<Integer> daysOfMonth;
    private Spinner spinnerMes, spinnerAño;
    private String calendarioId;
    private int mesSeleccionado;
    private int añoSeleccionado;
    private int diaSeleccionado = -1;

    public CalendarioAdapter(Context context, Spinner spinnerMes, Spinner spinnerAño, String calendarioId) {
        this.context = context;
        this.daysOfMonth = new ArrayList<>();
        this.spinnerMes = spinnerMes;
        this.spinnerAño = spinnerAño;
        this.calendarioId = calendarioId;

        // Obtener el mes y el año actual
        Calendar calendar = Calendar.getInstance();
        mesSeleccionado = calendar.get(Calendar.MONTH);
        añoSeleccionado = calendar.get(Calendar.YEAR);
        diaSeleccionado = calendar.get(Calendar.DAY_OF_MONTH);
        actualizarMesYAño(mesSeleccionado, añoSeleccionado);

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mesSeleccionado = position;
                actualizarMesYAño(mesSeleccionado, añoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no se selecciona nada
            }
        });

        // Agregar listener para el cambio de selección en el spinner de año
        spinnerAño.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                añoSeleccionado = Integer.parseInt(parent.getItemAtPosition(position).toString());
                actualizarMesYAño(mesSeleccionado, añoSeleccionado);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada si no se selecciona nada
            }
        });
    }


    @Override
    public int getCount() {
        return daysOfMonth.size();
    }

    @Override
    public Object getItem(int position) {
        return daysOfMonth.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.dia_calendario_item, parent, false);
        }

        // Obtener el día actual
        int day = daysOfMonth.get(position);

        // Obtener una referencia al TextView y al indicador de tarea en el diseño del elemento del calendario
        TextView txtDay = convertView.findViewById(R.id.txtDay);
        CardView CVDia = convertView.findViewById(R.id.CVDia);
        ImageView imgTaskIndicator = convertView.findViewById(R.id.imgTaskIndicator);

        // Establecer el día en el TextView
        if (day != 0) {
            txtDay.setText(String.valueOf(day));
            CVDia.setCardBackgroundColor(ContextCompat.getColor(context, R.color.orange1));

            // Resaltar el día seleccionado
            if (day == diaSeleccionado) {
                txtDay.setBackgroundColor(context.getResources().getColor(R.color.yellow));
                txtDay.setTextColor(Color.BLACK);
            } else {
                txtDay.setBackgroundColor(Color.TRANSPARENT);
                txtDay.setTextColor(context.getResources().getColor(android.R.color.black));
            }

            // Verificar si hay tareas para el día actual
            boolean hayTareas = hayTareasParaDia(day, mesSeleccionado, añoSeleccionado, calendarioId);
            if (hayTareas) {
                imgTaskIndicator.setVisibility(View.VISIBLE);
            } else {
                imgTaskIndicator.setVisibility(View.INVISIBLE);
            }

        } else {
            txtDay.setText("");
            imgTaskIndicator.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }


    // Método para actualizar el mes y el año del adaptador
    public void actualizarMesYAño(int mes, int año) {
        spinnerMes.setSelection(mes);
        ArrayAdapter<String> adapterAño = (ArrayAdapter<String>) spinnerAño.getAdapter();
        spinnerAño.setSelection(adapterAño.getPosition(String.valueOf(año)));

        // Limpiar la lista de días del mes
        daysOfMonth.clear();

        // Actualizar la lista de días del mes
        int daysInMonth = obtenerDiasEnMes(mes, año);
        for (int i = 1; i <= daysInMonth; i++) {
            daysOfMonth.add(i);
        }

        notifyDataSetChanged();
    }

    // Método para obtener el número de días en un mes específico
    private int obtenerDiasEnMes(int mes, int año) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, año);
        calendar.set(Calendar.MONTH, mes);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // Método para establecer el día seleccionado
    public void setDiaSeleccionado(int dia) {
        diaSeleccionado = dia;
    }

    // Método para avanzar al siguiente mes
    public void avanzarMes() {
        mesSeleccionado++;
        if (mesSeleccionado > 11) {
            mesSeleccionado = 0;
            añoSeleccionado++;
        }
        actualizarMesYAño(mesSeleccionado, añoSeleccionado);
        // Verificar si hay tareas para el día seleccionado en el nuevo mes y año
        hayTareasParaDia(diaSeleccionado, mesSeleccionado, añoSeleccionado, calendarioId);
    }

    // Método para retroceder al mes anterior
    public void retrocederMes() {
        mesSeleccionado--;
        if (mesSeleccionado < 0) {
            mesSeleccionado = 11;
            añoSeleccionado--;
        }
        actualizarMesYAño(mesSeleccionado, añoSeleccionado);
        hayTareasParaDia(diaSeleccionado, mesSeleccionado, añoSeleccionado, calendarioId);
    }

    private boolean hayTareasParaDia(int dia, int mes, int año, String idCalendario) {
        // Formatear la fecha del día específico en el formato esperado (dd/MM/yyyy)
        String fechaDiaString = String.format(Locale.getDefault(), "%02d/%02d/%04d", dia, mes + 1, año);

        // Llamar al método estático de la clase BDCalendarios para obtener las tareas del calendario específico para esa fecha
        Cursor cursor = BDCalendarios.CalendarioDBHelper.obtenerTareasDeCalendario(context, idCalendario);

        // Verificar si se encontraron tareas para este día, mes y año
        boolean hayTareas = false;
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Obtener la fecha de la tarea de la base de datos
                String fechaTareaString = cursor.getString(cursor.getColumnIndexOrThrow(BDCalendarios.TareaEntrada.COLUMN_FECHA));

                // Comparar la fecha de la tarea con la fecha del día específico
                if (fechaTareaString.startsWith(fechaDiaString)) {
                    hayTareas = true;
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return hayTareas;
    }

}


