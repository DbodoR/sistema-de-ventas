package org.dbodor.sistemadeventas.Model;

import java.time.LocalDateTime;

public class Turno {
    private int id;
    private LocalDateTime fechaApertura;
    private double montoInicial;
    private double montoFinal;
    private LocalDateTime fechaCierre;
    private String estado;

    public Turno() {}

    public Turno(int id, LocalDateTime fechaApertura, double montoInicial, double montoFinal, LocalDateTime fechaCierre, String estado) {
        this.id = id;
        this.fechaApertura = fechaApertura;
        this.montoInicial = montoInicial;
        this.montoFinal = montoFinal;
        this.fechaCierre = fechaCierre;
        this.estado = estado;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(double montoInicial) { this.montoInicial = montoInicial; }

    public double getMontoFinal() { return montoFinal; }
    public void setMontoFinal(double montoFinal) { this.montoFinal = montoFinal; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}