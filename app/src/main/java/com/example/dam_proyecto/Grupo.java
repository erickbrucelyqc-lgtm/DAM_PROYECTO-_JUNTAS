package com.example.dam_proyecto;

public class Grupo {
    private int id;
    private String nombre;
    private String descripcion;
    private int creadorId;

    // 🔑 NUEVO CAMPO: Para almacenar la URL de la imagen del dashboard del grupo
    private String urlImagen;

    // Constructor ACTUALIZADO
    public Grupo(int id, String nombre, String descripcion, int creadorId, String urlImagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.creadorId = creadorId;
        this.urlImagen = urlImagen; // Inicialización del nuevo campo
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getCreadorId() {
        return creadorId;
    }

    // 🔑 NUEVO GETTER
    public String getUrlImagen() {
        return urlImagen;
    }

    // 🔑 NUEVO SETTER (útil si necesitas cambiar la URL después de la creación)
    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }
}