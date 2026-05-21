package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.DAO.VentaDAO;
import org.dbodor.sistemadeventas.Model.DetalleVenta;
import org.dbodor.sistemadeventas.Model.Producto;
import org.dbodor.sistemadeventas.Model.Venta;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CobroController implements Initializable {
    @FXML private Label lblTotal;
    @FXML private ComboBox<String> cmbMetodoPago;
    @FXML private TextField txtMontoRecibido;
    @FXML
    private Label lblCambio;
    @FXML private Button btnFinalizar;

    private double totalVenta;
    private List<Producto> carritoItems;
    private int idTurnoActual = 1;
    private boolean ventaRealizada = false;
    private final VentaDAO ventaDAO = new VentaDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbMetodoPago.setItems(FXCollections.observableArrayList("Efectivo", "Transferencia"));
        cmbMetodoPago.setValue("Efectivo");
        lblCambio.setStyle("-fx-font-weight: bold;");
        cmbMetodoPago.setStyle("-fx-font-size: 16px");

        cmbMetodoPago.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if ("Transferencia".toUpperCase().equals(nuevo)) {
                txtMontoRecibido.setText(String.valueOf(totalVenta));
                txtMontoRecibido.setEditable(false);
                lblCambio.setText("$0.00");
                btnFinalizar.setDisable(false);
            } else {
                txtMontoRecibido.setEditable(true);
                txtMontoRecibido.clear();
                lblCambio.setText("$0.00");
                btnFinalizar.setDisable(true);
            }
        });

        txtMontoRecibido.textProperty().addListener((obs, textoViejo, textoNuevo) -> {
            if (cmbMetodoPago.getValue().equals("Transferencia")) return;

            try {
                if (textoNuevo.trim().isEmpty()) {
                    lblCambio.setText("$0.00");
                    btnFinalizar.setDisable(true);
                    return;
                }

                double montoRecibido = Double.parseDouble(textoNuevo);
                double cambio = montoRecibido - totalVenta;

                if (cambio >= 0) {
                    lblCambio.setText(Double.toString(cambio));
                    btnFinalizar.setDisable(false);
                } else {
                    lblCambio.setText("Monto insuficiente");
                    btnFinalizar.setDisable(true);
                }
            } catch (NumberFormatException e) {
                lblCambio.setText("Número inválido");
                btnFinalizar.setDisable(true);
            }
        });
    }

    public void setData(double total, List<Producto> items, int idTurno) {
        this.totalVenta = total;
        this.carritoItems = items;
        this.idTurnoActual = idTurno;
        lblTotal.setText(String.format("$%.2f", total));
    }

    @FXML
    private void finalizarVenta(ActionEvent event) {
        Venta venta = new Venta();
        venta.setTurnoId(idTurnoActual);
        venta.setTotal(totalVenta);
        venta.setMetodoPago(cmbMetodoPago.getValue().toUpperCase());

        double recibido = Double.parseDouble(txtMontoRecibido.getText());
        venta.setMontoRecibido(recibido);
        venta.setCambio(recibido - totalVenta);

        List<DetalleVenta> detalles = new ArrayList<>();
        for (Producto p : carritoItems) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProductoId(p.getId());
            detalle.setCantidad(p.getCantidad());
            detalle.setPrecioUnitario(p.getPrecio());
            detalle.setCostoUnitario(p.getCosto());
            detalles.add(detalle);
        }

        boolean exito = ventaDAO.registrarVentaCompleta(venta, detalles);

        if (exito) {
            ventaRealizada = true;
            cerrarVentana();
        } else {
            System.out.println("Error al procesar la transacción.");
        }
    }

    @FXML
    private void cancelarVenta(ActionEvent event) {
        ventaRealizada = false;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnFinalizar.getScene().getWindow();
        stage.close();
    }

    public boolean isVentaRealizada() {
        return ventaRealizada;
    }


}
