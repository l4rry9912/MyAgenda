package com.larry.myagenda.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.larry.myagenda.R;
import com.larry.myagenda.activitys.AgregarCalendario;
import com.larry.myagenda.activitys.AgregarTareaActivity;
import com.larry.myagenda.adaptadores.CalendarioAdapter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class CalendarioFragment extends Fragment {

    private CalendarioAdapter adapter;
    private Spinner spinnerMes, spinnerAño;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        View rootView = inflater.inflate(R.layout.fragment_calendario, container, false);
        String calendarioId = getArguments().getString("calendarioId");

        // Obtener una referencia al GridView
        GridView gridViewCalendario = rootView.findViewById(R.id.gridViewCalendario);

        // Obtener una referencia a los Spinners para seleccionar el mes y el año
        spinnerMes = rootView.findViewById(R.id.spinnerMes);
        spinnerAño = rootView.findViewById(R.id.spinnerAño);

        // Configurar los adaptadores para los Spinners
        ArrayAdapter<CharSequence> adapterMes = ArrayAdapter.createFromResource(requireContext(),
                R.array.meses_array, android.R.layout.simple_spinner_item);
        adapterMes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(adapterMes);

        // Obtener los años desde el año actual hasta 10 años en el futuro
        int añoActual = Calendar.getInstance().get(Calendar.YEAR);
        ArrayList<String> años = new ArrayList<>();
        for (int i = añoActual; i <= añoActual + 10; i++) {
            años.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapterAño = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, años);
        adapterAño.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAño.setAdapter(adapterAño);

        // Crear una instancia del adaptador de calendario y pasar el contexto y los Spinners
        adapter = new CalendarioAdapter(requireContext(), spinnerMes, spinnerAño, calendarioId);

        // Configurar el adaptador en el GridView
        gridViewCalendario.setAdapter(adapter);
        gridViewCalendario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private long lastClickTime = 0;
            private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Valor de tiempo umbral para considerar un doble clic

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    // Se ha detectado un doble clic
                    int clickDia = (int) parent.getItemAtPosition(position);
                    abrirAgregarTareaActivity(clickDia);
                } else {
                    // Se ha detectado un clic sencillo
                    int clickDia = (int) parent.getItemAtPosition(position);
                    abrirTareasFragment(clickDia);
                }
                lastClickTime = clickTime;

                // Lógica existente para actualizar el día seleccionado y notificar al adaptador
                int clickDia = (int) parent.getItemAtPosition(position);
                adapter.setDiaSeleccionado(clickDia);
                adapter.notifyDataSetChanged();
            }

            private void abrirTareasFragment(int clickDia) {
                // Obtener el ID del calendario del argumento
                String calendarioId = getArguments().getString("calendarioId");

                // Obtener el mes y año seleccionados de los Spinners
                int mesSeleccionado = spinnerMes.getSelectedItemPosition(); // El índice del mes en el Spinner
                int añoSeleccionado = Integer.parseInt(spinnerAño.getSelectedItem().toString());

                // Crear un Bundle para pasar los argumentos al TareasFragment
                Bundle args = new Bundle();
                args.putInt("diaSeleccionado", clickDia);
                args.putInt("mesSeleccionado", mesSeleccionado);
                args.putInt("añoSeleccionado", añoSeleccionado);
                args.putString("calendarioId", calendarioId);

                // Crear una instancia de TareasFragment
                TareasFragment tareasFragment = new TareasFragment();
                tareasFragment.setArguments(args);

                // Utilizar el FragmentManager para reemplazar el contenido del FrameLayout en HomeFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayoutTareas, tareasFragment)
                        .addToBackStack(null)
                        .commit();
            }


            private void abrirAgregarTareaActivity(int clickDia) {
                // Obtener el ID del calendario del argumento
                String calendarioId = getArguments().getString("calendarioId");

                // Obtener el mes y año seleccionados de los Spinners
                int mesSeleccionado = obtenerMes(spinnerMes.getSelectedItem().toString());
                int añoSeleccionado = Integer.parseInt(spinnerAño.getSelectedItem().toString());
                Intent intent = new Intent(requireContext(), AgregarTareaActivity.class);

                // Pasa el día, mes, año y ID del calendario como extras al Intent
                intent.putExtra("diaSeleccionado", clickDia);
                intent.putExtra("mesSeleccionado", mesSeleccionado);
                intent.putExtra("añoSeleccionado", añoSeleccionado);
                intent.putExtra("calendarioId", calendarioId);
                startActivity(intent);
            }
        });



        ImageButton btnAnterior = rootView.findViewById(R.id.btnAnterior);
        ImageButton btnSiguiente = rootView.findViewById(R.id.btnSiguiente);

        btnAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anteriorMes();
            }
        });

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siguienteMes();
            }
        });

        return rootView;
    }

    // Método para avanzar al mes siguiente
    private void siguienteMes() {
        adapter.avanzarMes();
    }

    // Método para retroceder al mes anterior
    private void anteriorMes() {
        adapter.retrocederMes();
    }

    private int obtenerMes(String nombreMes) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] meses = dfs.getMonths();
        for (int i = 0; i < meses.length; i++) {
            if (meses[i].equalsIgnoreCase(nombreMes)) {
                return i;
            }
        }
        return -1;
    }
}


