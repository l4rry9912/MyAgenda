package com.larry.myagenda.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.larry.myagenda.R;
import com.larry.myagenda.activitys.MainActivity;


public class AjustesFragment extends Fragment {

    private Button CerraSesion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);

        CerraSesion = view.findViewById(R.id.btnAjustes); // Aquí está el cambio
        CerraSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        return view;
    }

    private void cerrarSesion() {
        // Cerrar la sesión de Firebase
        FirebaseAuth.getInstance().signOut();

        // Mostrar el Toast
        Toast.makeText(getActivity(), "Hasta pronto", Toast.LENGTH_SHORT).show();

        // Redirigir a la actividad de inicio de sesión
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Borrar el historial de actividades
        startActivity(intent);
        getActivity().finish();
    }
}

