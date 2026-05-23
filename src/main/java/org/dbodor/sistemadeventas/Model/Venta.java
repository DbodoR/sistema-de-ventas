package org.dbodor.sistemadeventas.Model;

public class Venta {
    private int id;
    private int turnoId;
    private String fecha;
    private String metodoPago;
    private double total;
    private double montoRecibido;
    private double cambio;
    private double pagoEfectivo;
    private double pagoTransferencia;

    public Venta() {}

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTurnoId() { return turnoId; }
    public void setTurnoId(int turnoId) { this.turnoId = turnoId; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public double getPagoEfectivo() {
        return pagoEfectivo;
    }

    public void setPagoEfectivo(double pagoEfectivo) {
        this.pagoEfectivo = pagoEfectivo;
    }

    public double getPagoTransferencia() {
        return pagoTransferencia;
    }

    public void setPagoTransferencia(double pagoTransferencia) {
        this.pagoTransferencia = pagoTransferencia;
    }
}