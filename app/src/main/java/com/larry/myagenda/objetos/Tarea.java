package com.larry.myagenda.objetos;

import java.util.Date;

import java.util.Date;

public class Tarea {
    private String id;
    private String calendarioId;
    private String titulo;
    private String contenido;
    private String fecha;

    // Constructor
    public Tarea(String id, String calendarioId, String titulo, String contenido, String fecha) {
        this.id = id;
        this.calendarioId = calendarioId;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCalendarioId() {
        return calendarioId;
    }

    public void setCalendarioId(String calendarioId) {
        this.calendarioId = calendarioId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

