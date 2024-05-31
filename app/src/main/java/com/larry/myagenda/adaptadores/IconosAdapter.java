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

    private List<Iconos> iconosList; // Lista de íconos
    private OnIconoClickListener listener; // Listener para los clics en los íconos
    private Iconos iconoSeleccionado;

    // Constructor del adaptador
    public IconosAdapter(List<Iconos> iconosList) {
        this.iconosList = iconosList; // Inicializamos la lista de íconos
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
        // Obtenemos el objeto Iconos correspondiente a la posición dada
        Iconos icono = iconosList.get(position);
        // Establecemos la imagen del ícono en el ImageView del ViewHolder
        holder.iconoImageView.setImageResource(icono.getImagenId());
        // Establecemos el nombre del calendario en el TextView correspondiente
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
                // Actualizamos el ícono seleccionado y notificamos los cambios
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

    // Clase interna que representa un ViewHolder para los íconos
    public class IconoViewHolder extends RecyclerView.ViewHolder {
        ImageView iconoImageView; // ImageView para mostrar el ícono
        TextView calendarionombreT; // TextView para mostrar el nombre del calendario

        // Constructor del ViewHolder
        public IconoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializamos las vistas
            iconoImageView = itemView.findViewById(R.id.iconImageView);
            calendarionombreT = itemView.findViewById(R.id.calendarionombreT);
        }
    }

    // Interfaz que define el método para manejar los clics en los íconos
    public interface OnIconoClickListener {
        void onIconoClick(Iconos icono);
    }

    // Método para establecer un listener para manejar los clics en los íconos
    public void setOnIconoClickListener(OnIconoClickListener listener) {
        this.listener = listener;
    }
}

