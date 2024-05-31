package com.larry.myagenda.fragmentos;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.larry.myagenda.R;
import com.larry.myagenda.activitys.AgregarNota;
import com.larry.myagenda.objetos.Nota;

public class FragmentNota extends Fragment {
    private TextView textViewTitulo, textViewDescripcion, textViewContenido, textViewFecha;
    private Button btnModificar, btnEliminar;
    private Nota nota;
    private static final int AGREGAR_NOTA_REQUEST_CODE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nota, container, false);

        // Inicializar vistas
        textViewTitulo = rootView.findViewById(R.id.TVTitulo);
        textViewDescripcion = rootView.findViewById(R.id.TVDescripcion);
        textViewContenido = rootView.findViewById(R.id.TVContenido);
        textViewFecha = rootView.findViewById(R.id.TVFecha);
        btnModificar = rootView.findViewById(R.id.Btn_modificar);
        btnEliminar = rootView.findViewById(R.id.Btn_eliminar);

        // Obtener la nota de los argumentos del fragmento
        if (getArguments() != null) {
            nota = (Nota) getArguments().getSerializable("nota");
            if (nota != null) {
                textViewTitulo.setText(nota.getTitulo());
                textViewDescripcion.setText(nota.getDescripcion());
                textViewContenido.setText(nota.getContenido());
                textViewFecha.setText(nota.getFecha());
            }
        }

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para iniciar la actividad AgregarNota
                Intent intent = new Intent(getActivity(), AgregarNota.class);

                // Pasar la nota como extra en el Intent para la edición
                intent.putExtra("nota", nota);

                // Iniciar la actividad con el intent para la edición de la nota
                startActivityForResult(intent, AGREGAR_NOTA_REQUEST_CODE);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Confirmar eliminación");
                builder.setMessage("¿Estás seguro de que quieres eliminar esta nota?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ejecutar la lógica para eliminar la nota
                        eliminarNota();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        return rootView;
    }
    //metodo para ocultar el navigationview
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ocultar el BottomNavigationView
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.INVISIBLE);
        Animation slideOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);
        bottomNavigationView.startAnimation(slideOutAnimation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Mostrar nuevamente el BottomNavigationView al salir del FragmentNota
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
        Animation slideInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_fade_in);
        bottomNavigationView.startAnimation(slideInAnimation);

    }
    // Método para configurar la animación de entrada y salida del fragment
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter) {
            // Animación de entrada personalizada
            return AnimationUtils.loadAnimation(getContext(), R.anim.slide_in);
        } else {
            // Animación de salida personalizada
            return AnimationUtils.loadAnimation(getContext(), R.anim.slide_out);
        }
    }
    // Método para eliminar la nota
    private void eliminarNota() {
        if (nota != null && isAdded()) {
            // Obtener referencia a la base de datos para la nota específica
            DatabaseReference notaRef = FirebaseDatabase.getInstance().getReference()
                    .child("usuarios").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("notas").child(nota.getId());

            // Eliminar la nota de la base de datos
            notaRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Nota eliminada correctamente", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Error al eliminar la nota", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AGREGAR_NOTA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            getParentFragmentManager().popBackStack();
        }
    }
}
