package com.produvisa.emergencias.models;

// POJO para mapear el JSON que se envía por POST
public class IncidenciaDto {
    private int TrabajadorId;
    // Usamos Integer para permitir valores null para FamiliarId
    private Integer FamiliarId;
    private boolean Pregunta1;
    private boolean Pregunta2;
    private boolean Pregunta3;
    private boolean Pregunta4;
    private boolean Pregunta5;

    // Constructor
    public IncidenciaDto(int trabajadorId, Integer familiarId, boolean p1, boolean p2, boolean p3, boolean p4, boolean p5) {
        this.TrabajadorId = trabajadorId;
        this.FamiliarId = familiarId;
        this.Pregunta1 = p1;
        this.Pregunta2 = p2;
        this.Pregunta3 = p3;
        this.Pregunta4 = p4;
        this.Pregunta5 = p5;
    }

    // Getters y Setters
    public int getTrabajadorId() {
        return TrabajadorId;
    }

    public Integer getFamiliarId() {
        return FamiliarId;
    }

    public boolean isPregunta1() {
        return Pregunta1;
    }

    public boolean isPregunta2() {
        return Pregunta2;
    }

    public boolean isPregunta3() {
        return Pregunta3;
    }

    public boolean isPregunta4() {
        return Pregunta4;
    }

    public boolean isPregunta5() {
        return Pregunta5;
    }
}