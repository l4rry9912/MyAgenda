package com.larry.myagenda.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Nota;

import java.util.List;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.NotaViewHolder> {

    private List<Nota> notasList;
    private Context context;
    private OnNotaClickListener onNotaClickListener; // Interfaz

    // Interfaz para manejar clics en las notas
    public interface OnNotaClickListener {
        void onNotaClick(Nota nota);
    }

    // Método para establecer el listener de clics en las notas
    public void setOnNotaClickListener(OnNotaClickListener listener) {
        this.onNotaClickListener = listener;
    }

    // Constructor que recibe la lista de notas y el contexto
    public NotaAdapter(List<Nota> notasList, Context context) {
        this.notasList = notasList;
        this.context = context;
    }

    // Método llamado cuando se necesita crear un nuevo ViewHolder
    @NonNull
    @Override
    public NotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout de la card view para cada elemento de la lista
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nota_item, parent, false);
        return new NotaViewHolder(itemView);
    }

    // Crear y retornar una nueva instancia de NotaViewHolder con la vista inflada
    @Override
    public void onBindViewHolder(@NonNull NotaViewHolder holder, int position) {
        // Obtener la nota en la posición dada
        Nota nota = notasList.get(position);

        // Asignar los datos de la nota a los elementos de la vista ViewHolder
        holder.textViewTitulo.setText(nota.getTitulo());
        holder.textViewDescripcion.setText(nota.getDescripcion());
        holder.textViewContenido.setText(nota.getContenido());
        holder.textViewFecha.setText(nota.getFecha());

        // Establecer un OnClickListener para cada elemento de la lista
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Si se ha establecido el listener de clics en las notas, llamar a su método onNotaClick
                if (onNotaClickListener != null) {
                    onNotaClickListener.onNotaClick(nota);
                }
            }
        });
    }

    // Método que devuelve la cantidad de elementos en la lista de notas
    @Override
    public int getItemCount() {
        return notasList.size();
    }

    // Clase interna que representa la vista de un elemento de la lista (ViewHolder)
    public static class NotaViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitulo, textViewDescripcion, textViewFecha, textViewContenido;

        // Constructor que recibe la vista inflada
        public NotaViewHolder(View view) {
            super(view);
            // Obtener referencias a los TextViews dentro de la vista
            textViewTitulo = view.findViewById(R.id.textViewTitulo);
            textViewDescripcion = view.findViewById(R.id.textViewDescripcion);
            textViewContenido = view.findViewById(R.id.textViewContenido);
            textViewFecha = view.findViewById(R.id.textViewFecha);
        }
    }
}



