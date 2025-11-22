package com.produvisa.emergencias.models;

// POJO para mapear los datos del familiar
public class Familiar {
    private int Id;
    private int TrabajadorId;
    private String Cedula;
    private String Nombres;
    private String Apellidos;

    // Getters y Setters (Necesarios para la deserialización de Gson/Retrofit)

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getCedula() {
        return Cedula;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public String getNombres() {
        return Nombres;
    }

    public void setNombres(String nombres) {
        Nombres = nombres;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    // Método para mostrar el nombre completo en el selector
    public String getNombreCompleto() {
        return Nombres + " " + Apellidos + " (" + Cedula + ")";
    }
}