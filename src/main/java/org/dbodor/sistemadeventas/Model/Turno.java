package org.dbodor.sistemadeventas.Model;

public class Turno {
    private int id;
    private String fechaApertura;
    private double montoInicial;
    private double montoFinal;
    private String fechaCierre;
    private String estado;

    public Turno() {}

    public Turno(int id, String fechaApertura, double montoInicial, double montoFinal, String fechaCierre, String estado) {
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

    public String getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(String fechaApertura) { this.fechaApertura = fechaApertura; }

    public double getMontoInicial() { return montoInicial; }
    public void setMontoInicial(double montoInicial) { this.montoInicial = montoInicial; }

    public double getMontoFinal() { return montoFinal; }
    public void setMontoFinal(double montoFinal) { this.montoFinal = montoFinal; }

    public String getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(String fechaCierre) { this.fechaCierre = fechaCierre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}