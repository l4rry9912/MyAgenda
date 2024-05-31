package com.larry.myagenda.objetos;

public class Iconos {
    private int imagenId;
    private String nombre;
    private String ruta;
    public Iconos(int imagenId, String nombre, String ruta) {
        this.imagenId = imagenId;
        this.nombre = nombre;
        this.ruta = ruta;
    }

    public int getImagenId() {
        return imagenId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public void setImagenId(int imagenId) {
        this.imagenId = imagenId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRuta(String ruta) { // Nuevo método para establecer la ruta del icono
        this.ruta = ruta;
    }
}
