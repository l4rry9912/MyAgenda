package com.larry.myagenda.objetos;

import java.io.Serializable;

public class Nota implements Serializable {
    private String id;
    private String titulo;
    private String descripcion;
    private String contenido;
    private String fecha;

    public Nota() {
        // Constructor vacío
    }

    public Nota(String id, String titulo, String descripcion, String contenido, String fecha) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.contenido = contenido;
        this.fecha = fecha;
    }

    // Getters y setters
    public String getId() {return id;}

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {return titulo;}

    public void setTitulo(String titulo) {this.titulo = titulo;}

    public String getDescripcion() {return descripcion;}

    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}

    public String getContenido() {return contenido;}

    public void setContenido(String contenido) {this.contenido = contenido;}

    public String getFecha() {return fecha;}

    public void setFecha(String fecha) {this.fecha = fecha;}
}

