package org.dbodor.sistemadeventas.Model;

public class Productos {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private int categoria_id;
    private String codigoBarras;
    private boolean precioVariable;

    public Productos() {
    }

    public Productos(int id, String nombre, String descripcion, double precio, int stock, int categoria_id, String codigoBarras, boolean precioVariable) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria_id = categoria_id;
        this.codigoBarras = codigoBarras;
        this.precioVariable = precioVariable;
    }

    public Productos(String nombre, String descripcion, double precio, int stock, int categoria_id, String codigoBarras, boolean precioVariable) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria_id = categoria_id;
        this.codigoBarras = codigoBarras;
        this.precioVariable = precioVariable;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCategoria_id() {
        return categoria_id;
    }

    public void setCategoria_id(int categoria_id) {
        this.categoria_id = categoria_id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public boolean isPrecioVariable() {
        return precioVariable;
    }

    public void setPrecioVariable(boolean precioVariable) {
        this.precioVariable = precioVariable;
    }

    @Override
    public String toString() {
        return "Productos{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", categoria_id=" + categoria_id +
                ", stock=" + stock +
                '}';
    }
}
