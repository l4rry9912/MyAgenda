package com.larry.myagenda.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Iconos;

import java.util.List;
public class IconosAdapter extends RecyclerView.Adapter<IconosAdapter.IconoViewHolder> {

    private List<Iconos> iconosList;
    private OnIconoClickListener listener;
    private Iconos iconoSeleccionado;

    // Constructor del adaptador
    public IconosAdapter(List<Iconos> iconosList) {
        this.iconosList = iconosList;
    }

    // Método llamado cuando se necesita crear un nuevo ViewHolder
    @NonNull
    @Override
    public IconoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el diseño del ítem y lo pasamos a un nuevo IconoViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_item, parent, false);
        return new IconoViewHolder(view);
    }

    // Método llamado para asociar datos a un ViewHolder existente
    @Override
    public void onBindViewHolder(@NonNull IconoViewHolder holder, int position) {
        Iconos icono = iconosList.get(position);
        holder.iconoImageView.setImageResource(icono.getImagenId());
        holder.calendarionombreT.setText(icono.getNombre());

        // Verificamos si este ícono es el ícono seleccionado y actualizamos su apariencia
        if (icono.equals(iconoSeleccionado)) {

            // Cambia la apariencia del ícono seleccionado
            holder.itemView.setScaleX(1.1f);
            holder.itemView.setScaleY(1.1f);

        } else {

            // Restauramos la apariencia predeterminada del ícono no seleccionado
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);
        }

        // Configuramos un OnClickListener en el itemView para manejar los clics en el ícono
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificamos que el listener no sea nulo y llamamos al método onIconoClick del listener
                if (listener != null) {
                    listener.onIconoClick(icono);
                }
                iconoSeleccionado = icono;
                notifyDataSetChanged();
            }
        });
    }

    // Método que devuelve el número total de ítems en el conjunto de datos
    @Override
    public int getItemCount() {
        return iconosList.size();
    }

    public class IconoViewHolder extends RecyclerView.ViewHolder {
        ImageView iconoImageView;
        TextView calendarionombreT;
        // Constructor del ViewHolder
        public IconoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializamos las vistas
            iconoImageView = itemView.findViewById(R.id.iconImageView);
            calendarionombreT = itemView.findViewById(R.id.calendarionombreT);
        }
    }
    public interface OnIconoClickListener {
        void onIconoClick(Iconos icono);
    }
    public void setOnIconoClickListener(OnIconoClickListener listener) {
        this.listener = listener;
    }
}

