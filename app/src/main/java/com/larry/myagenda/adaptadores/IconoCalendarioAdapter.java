package com.larry.myagenda.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.larry.myagenda.objetos.IconosUtils;
import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Calendario;
import com.larry.myagenda.objetos.Iconos;

import java.util.List;

public class IconoCalendarioAdapter extends RecyclerView.Adapter<IconoCalendarioAdapter.IconoViewHolder> {

    private Context context;
    private List<Calendario> calendarios;
    private List<Iconos> iconosList;
    private OnCalendarioClickListener listener;
    private OnCalendarioDoubleClickListener doubleClickListener;
    private Calendario calendarioSeleccionado;
    private long lastClickTime = 0;
    private static final long DOUBLE_CLICK_TIME_DELTA = 300;

    public IconoCalendarioAdapter(List<Calendario> calendarios, Context context) {
        this.context = context;
        this.calendarios = calendarios;
        this.iconosList = getIconosList();
    }

    @NonNull
    @Override
    public IconoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.icono_calendario_item, parent, false);
        return new IconoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconoViewHolder holder, int position) {
        Calendario calendario = calendarios.get(position);

        holder.cardViewIcono.setCardBackgroundColor(calendario.getColor());
        String iconoRuta = calendario.getIconoRuta();
        Iconos icono = buscarIconoPorRuta(iconoRuta);
        if (icono != null) {
            holder.iconoImageView.setImageResource(icono.getImagenId());
        } else {

        }

        // Verificamos si este calendario es el calendario seleccionado y actualizamos su apariencia
        if (calendario.equals(calendarioSeleccionado)) {
            // Cambia la apariencia del calendario seleccionado
            holder.itemView.setScaleX(1.1f);
            holder.itemView.setScaleY(1.1f);
        } else {
            // Restauramos la apariencia predeterminada del calendario no seleccionado
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);
        }

        // Manejar el clic en el elemento del calendario
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Actualizar el calendario seleccionado y notificar los cambios
                calendarioSeleccionado = calendario;
                notifyDataSetChanged();

                // Manejar el doble clic
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    // Doble clic detectado
                    if (doubleClickListener != null) {
                        doubleClickListener.onCalendarioDoubleClick(calendario);
                    }
                }
                lastClickTime = clickTime;

                // Llamar al método onCalendarioClick() del listener para manejar el clic del calendario
                if (listener != null) {
                    listener.onCalendarioClick(calendario);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return calendarios.size();
    }

    public void setOnCalendarioClickListener(OnCalendarioClickListener listener) {
        this.listener = listener;
    }

    public void setOnCalendarioDoubleClickListener(OnCalendarioDoubleClickListener listener) {
        this.doubleClickListener = listener;
    }

    private List<Iconos> getIconosList() {
        return IconosUtils.getIconosList();
    }

    public static class IconoViewHolder extends RecyclerView.ViewHolder {
        ImageView iconoImageView;
        CardView cardViewIcono;

        public IconoViewHolder(@NonNull View itemView) {
            super(itemView);
            iconoImageView = itemView.findViewById(R.id.iconoImageView);
            cardViewIcono = itemView.findViewById(R.id.cardviewIcono);
        }
    }

    private Iconos buscarIconoPorRuta(String iconoRuta) {
        for (Iconos icono : iconosList) {
            if (icono.getRuta().equals(iconoRuta)) {
                return icono;
            }
        }
        return null;
    }

    public void seleccionarCalendario(Calendario calendario) {
        calendarioSeleccionado = calendario;
        notifyDataSetChanged();
        if (listener != null) {
            listener.onCalendarioClick(calendario);
        }
    }

    public interface OnCalendarioClickListener {
        void onCalendarioClick(Calendario calendario);
    }

    public interface OnCalendarioDoubleClickListener {
        void onCalendarioDoubleClick(Calendario calendario);
    }
}





