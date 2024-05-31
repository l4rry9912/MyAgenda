package com.larry.myagenda.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Tarea;
import java.util.List;

// TareasAdapter.java
public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.TareaViewHolder> {

    private Context context;
    private List<Tarea> listaTareas;
    private OnTareaDoubleClickListener tareaDoubleClickListener;

    public TareasAdapter(Context context, List<Tarea> listaTareas) {
        this.context = context;
        this.listaTareas = listaTareas;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tarea_item, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = listaTareas.get(position);
        holder.textViewNombreTarea.setText(tarea.getTitulo());
        holder.textViewDescripcionTarea.setText(tarea.getContenido());
        holder.textViewHoraTarea.setText(tarea.getFecha());

        // Manejar el doble clic en la tarea
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Intervalo de tiempo para considerar un doble clic en milisegundos
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                // Obtener el tiempo actual en milisegundos
                long clickTime = System.currentTimeMillis();

                // Verificar si es un doble clic
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    // Es un doble clic, llamar al método del listener
                    if (tareaDoubleClickListener != null) {
                        tareaDoubleClickListener.onTareaDoubleClick(tarea);
                    }
                }

                // Actualizar el tiempo del último clic
                lastClickTime = clickTime;
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombreTarea;
        TextView textViewDescripcionTarea;
        TextView textViewHoraTarea;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreTarea = itemView.findViewById(R.id.txtNombreTarea);
            textViewDescripcionTarea = itemView.findViewById(R.id.txtDescripcionTarea);
            textViewHoraTarea = itemView.findViewById(R.id.txtFechaHora);
        }
    }

    // Interfaz para manejar los eventos de doble clic en las tareas
    public interface OnTareaDoubleClickListener {
        void onTareaDoubleClick(Tarea tarea);
    }

    // Método para establecer el listener de doble clic en la tarea
    public void setOnTareaDoubleClickListener(OnTareaDoubleClickListener listener) {
        this.tareaDoubleClickListener = listener;
    }

    public void eliminarTarea(Tarea tarea) {
        int position = listaTareas.indexOf(tarea);
        if (position != -1) {
            listaTareas.remove(position);
            notifyItemRemoved(position);
        }
    }
}



