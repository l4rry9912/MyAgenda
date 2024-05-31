package com.larry.myagenda.objetos;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.larry.myagenda.DB.BDCalendarios;
import com.larry.myagenda.R;
import com.larry.myagenda.adaptadores.IconoCalendarioAdapter;

import java.util.ArrayList;
import java.util.List;

public class Calendario {
    private String id;
    private String nombre;
    private String iconoRuta;
    private int color;

    // Constructor para crear un objeto Calendario con todos los atributos
    public Calendario(String id, String nombre, String iconoRuta, int color) {
        this.id = id;
        this.nombre = nombre;
        this.iconoRuta = iconoRuta;
        this.color = color;
    }

    // Métodos para acceder a los datos del calendario
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIconoRuta() {
        return iconoRuta;
    }

    public int getColor() {
        return color;
    }
}












