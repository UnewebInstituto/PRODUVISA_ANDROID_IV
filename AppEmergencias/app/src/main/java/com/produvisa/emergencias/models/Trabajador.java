package com.produvisa.emergencias.models;

import java.util.List;

// POJO para mapear los datos del trabajador y su lista de familiares
public class Trabajador {
    private int Id;
    private String Cedula;
    private String Nombres;
    private String Apellidos;
    private String CorreoElectronico;
    private String Telefono;
    private List<Familiar> Familiares;

    // Getters y Setters

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

    public List<Familiar> getFamiliares() {
        return Familiares;
    }

    public void setFamiliares(List<Familiar> familiares) {
        Familiares = familiares;
    }

    public String getNombreCompleto() {
        return Nombres + " " + Apellidos + " (" + Cedula + ")";
    }
}
