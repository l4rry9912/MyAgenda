package com.larry.myagenda.objetos;

import com.larry.myagenda.R;
import com.larry.myagenda.objetos.Iconos;

import java.util.ArrayList;
import java.util.List;

public class IconosUtils {
    private static final List<Iconos> iconosList = new ArrayList<>();

    static {
        iconosList.add(new Iconos(R.drawable.ic_cal_study, "Estudio", "ruta_del_icono1"));
        iconosList.add(new Iconos(R.drawable.ic_cal_medico, "Medico", "ruta_del_icono2"));
        iconosList.add(new Iconos(R.drawable.ic_cal_hbd, "Cumpleaños", "ruta_del_icono3"));
        iconosList.add(new Iconos(R.drawable.ic_cal_travel, "Viajes", "ruta_del_icono4"));
        iconosList.add(new Iconos(R.drawable.ic_cal_work, "Trabajo", "ruta_del_icono5"));
        iconosList.add(new Iconos(R.drawable.ic_cal_cal, "CALENDARIO", "ruta_del_icono6"));
        iconosList.add(new Iconos(R.drawable.ic_cal_cm, "Ciclo Menstrual", "ruta_del_icono7"));
        iconosList.add(new Iconos(R.drawable.ic_cal_gym, "GYM", "ruta_del_icono8"));
    }

    public static List<Iconos> getIconosList() {
        return iconosList;
    }
}

