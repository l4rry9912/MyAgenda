package com.larry.myagenda.fragmentos;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.larry.myagenda.R;
import com.larry.myagenda.activitys.AgregarNota;
import com.larry.myagenda.objetos.Nota;
import com.larry.myagenda.adaptadores.NotaAdapter;
import java.util.ArrayList;
import java.util.List;

public class NotasFragment extends Fragment {

    private FloatingActionButton fab;
    private RecyclerView recyclerViewNotas;
    private NotaAdapter notaAdapter;
    private List<Nota> notaList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notas, container, false);

        // Inicializar RecyclerView
        recyclerViewNotas = root.findViewById(R.id.recyclerViewNotas);
        recyclerViewNotas.setLayoutManager(new GridLayoutManager(getContext(), 2));
        notaList = new ArrayList<>(); // Inicializa la lista aquí

        // Obtener el usuario actualmente autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            // Obtener referencia a las notas del usuario en la base de datos de Firebase
            DatabaseReference notasRef = FirebaseDatabase.getInstance().getReference("usuarios").child(user.getUid()).child("notas");

            // Escuchar cambios en las notas del usuario
            notasRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Limpiar la lista de notas antes de agregar nuevas notas
                    notaList.clear();
                    // Iterar sobre todas las notas del usuario y añadirlas a la lista
                    for (DataSnapshot notaSnapshot : dataSnapshot.getChildren()) {
                        Nota nota = notaSnapshot.getValue(Nota.class);
                        notaList.add(nota);
                    }
                    // Instanciar y establecer el adaptador con los datos actualizados
                    notaAdapter = new NotaAdapter(notaList, getContext());
                    recyclerViewNotas.setAdapter(notaAdapter);
                    // Agregar clics en las notas para abrir el FragmentNota
                    notaAdapter.setOnNotaClickListener(new NotaAdapter.OnNotaClickListener() {
                        @Override
                        public void onNotaClick(Nota nota) {
                            abrirFragmentNota(nota);
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores de lectura de la base de datos
                    databaseError.toException().printStackTrace();
                }
            });
        }

        // Botón flotante
        fab = root.findViewById(R.id.fabAddNota);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Abrir la nueva actividad de notas
                Intent intent = new Intent(getActivity(), AgregarNota.class);
                startActivity(intent);
            }
        });

        // Ocultar o mostrar el FAB dependiendo del scroll del RecyclerView
        recyclerViewNotas.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fab.isShown()) {
                    fab.hide();
                } else if (dy < 0 && !fab.isShown()) {
                    fab.show();
                }
            }
        });

        return root;
    }

    // Método para abrir el FragmentNota
    private void abrirFragmentNota(Nota nota) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentNota fragmentNota = new FragmentNota();
        Bundle args = new Bundle();
        args.putSerializable("nota", nota);
        fragmentNota.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentNota)
                .addToBackStack(null)
                .commit();
    }
}
