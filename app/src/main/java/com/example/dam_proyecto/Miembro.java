package com.example.dam_proyecto;

// Clase para representar a un integrante del grupo
public class Miembro {
    private int id;
    private String nombre;
    private String urlImagen;  // Nueva
    private String estadoPago; // Nueva: 'SI' o 'NO'

    public Miembro(int id, String nombre, String urlImagen, String estadoPago) {
        this.id = id;
        this.nombre = nombre;
        this.urlImagen = urlImagen;
        this.estadoPago = estadoPago;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUrlImagen() { return urlImagen; }
    public String getEstadoPago() { return estadoPago; }
}