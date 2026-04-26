package org.dbodor.sistemadeventas.Model;

public class Venta {
    private int id;
    private int turnoId;
    private String fecha;
    private String metodoPago;
    private double total;

    public Venta() {}

    public Venta(int id, int turnoId, String fecha, String metodoPago, double total) {
        this.id = id;
        this.turnoId = turnoId;
        this.fecha = fecha;
        this.metodoPago = metodoPago;
        this.total = total;
    }

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
}