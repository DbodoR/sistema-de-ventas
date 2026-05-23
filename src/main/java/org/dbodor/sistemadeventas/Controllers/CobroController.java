package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.DAO.VentaDAO;
import org.dbodor.sistemadeventas.Model.DetalleVenta;
import org.dbodor.sistemadeventas.Model.Producto;
import org.dbodor.sistemadeventas.Model.Venta;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CobroController implements Initializable {

    @FXML
    private HBox HboxMonto;

    @FXML
    private VBox VboxMixto;

    @FXML
    private TextField txtMontoRecibidoEfectivo;

    @FXML
    private TextField txtMontoRecibidoTransferencia;

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
        cmbMetodoPago.setItems(FXCollections.observableArrayList("Efectivo", "Transferencia", "Mixto"));
        cmbMetodoPago.setValue("Efectivo");
        lblCambio.setStyle("-fx-font-weight: bold;");
        cmbMetodoPago.setStyle("-fx-font-size: 16px");

        VboxMixto.managedProperty().bind(VboxMixto.visibleProperty());

        HboxMonto.managedProperty().bind(HboxMonto.visibleProperty());

        VboxMixto.setVisible(false);
        HboxMonto.setVisible(true);

        cmbMetodoPago.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> {
            if (nuevo == null) return;

            lblCambio.setText("$0.00");
            txtMontoRecibido.clear();
            txtMontoRecibidoEfectivo.clear();
            txtMontoRecibidoTransferencia.clear();
            btnFinalizar.setDisable(true);

            if ("TRANSFERENCIA".equalsIgnoreCase(nuevo)) {
                VboxMixto.setVisible(false);
                HboxMonto.setVisible(true);

                txtMontoRecibido.setText(String.valueOf(totalVenta));
                txtMontoRecibido.setEditable(false);
                btnFinalizar.setDisable(false);

            } else if ("MIXTO".equalsIgnoreCase(nuevo)) {
                HboxMonto.setVisible(false);
                VboxMixto.setVisible(true);

                txtMontoRecibidoEfectivo.requestFocus();

            } else {
                VboxMixto.setVisible(false);
                HboxMonto.setVisible(true);

                txtMontoRecibido.setEditable(true);
            }

            if (cmbMetodoPago.getScene() != null && cmbMetodoPago.getScene().getWindow() != null) {
                cmbMetodoPago.getScene().getWindow().sizeToScene();
            }
        });

        txtMontoRecibido.textProperty().addListener((obs, textoViejo, textoNuevo) -> {
            if ("TRANSFERENCIA".equalsIgnoreCase(cmbMetodoPago.getValue())) return;
            if ("MIXTO".equalsIgnoreCase(cmbMetodoPago.getValue())) return;

            try {
                if (textoNuevo.trim().isEmpty()) {
                    lblCambio.setText("$0.00");
                    btnFinalizar.setDisable(true);
                    return;
                }

                double montoRecibido = Double.parseDouble(textoNuevo);
                double cambio = montoRecibido - totalVenta;

                if (cambio >= 0) {
                    lblCambio.setText(String.format("$%,.2f", cambio));
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

        javafx.beans.value.ChangeListener<String> listenerMixto = (obs, viejo, nuevo) -> {
            if (!"MIXTO".equalsIgnoreCase(cmbMetodoPago.getValue())) return;

            try {
                String efecTexto = txtMontoRecibidoEfectivo.getText().trim();
                String transTexto = txtMontoRecibidoTransferencia.getText().trim();

                double efectivo = efecTexto.isEmpty() ? 0 : Double.parseDouble(efecTexto);
                double transferencia = transTexto.isEmpty() ? 0 : Double.parseDouble(transTexto);

                double totalIngresado = efectivo + transferencia;
                double cambio = totalIngresado - totalVenta;

                if (totalIngresado >= totalVenta) {
                    if (cambio > efectivo) {
                        lblCambio.setText("Error: Cambio supera efectivo");
                        btnFinalizar.setDisable(true);
                    } else {
                        lblCambio.setText(String.format("$%,.2f", cambio));
                        btnFinalizar.setDisable(false);
                    }
                } else {
                    lblCambio.setText(String.format("Faltan: $%,.2f", (totalVenta - totalIngresado)));
                    btnFinalizar.setDisable(true);
                }
            } catch (NumberFormatException e) {
                lblCambio.setText("Número inválido");
                btnFinalizar.setDisable(true);
            }
        };
        txtMontoRecibidoEfectivo.textProperty().addListener(listenerMixto);
        txtMontoRecibidoTransferencia.textProperty().addListener(listenerMixto);
    }

    public void setData(double total, List<Producto> items, int idTurno) {
        this.totalVenta = total;
        this.carritoItems = items;
        this.idTurnoActual = idTurno;
        lblTotal.setText(String.format("$%.2f", total));
    }

    @FXML
    private void finalizarVenta(ActionEvent event) {
        String metodoPagoSeleccionado = cmbMetodoPago.getValue().toUpperCase();

        Venta venta = new Venta();
        venta.setTurnoId(idTurnoActual);
        venta.setTotal(totalVenta);
        venta.setMetodoPago(metodoPagoSeleccionado);

        if ("MIXTO".equals(metodoPagoSeleccionado)) {
            double efectivo = txtMontoRecibidoEfectivo.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtMontoRecibidoEfectivo.getText().trim());
            double transferencia = txtMontoRecibidoTransferencia.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtMontoRecibidoTransferencia.getText().trim());

            venta.setPagoEfectivo(efectivo);
            venta.setPagoTransferencia(transferencia);
            venta.setCambio((efectivo + transferencia) - totalVenta);
        } else if ("EFECTIVO".equals(metodoPagoSeleccionado)) {
            double recibido = Double.parseDouble(txtMontoRecibido.getText().trim());
            venta.setPagoEfectivo(recibido);
            venta.setPagoTransferencia(0.0);
            venta.setCambio(recibido - totalVenta);
        } else {
            venta.setPagoEfectivo(0.0);
            venta.setPagoTransferencia(totalVenta);
            venta.setCambio(0.0);
        }

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

            Alert alertExito = new Alert(Alert.AlertType.INFORMATION);
            alertExito.setTitle("Venta Exitosa");
            alertExito.setHeaderText(null);
            alertExito.setContentText("Transacción registrada con éxito.\nCambio: $" + String.format("%,.2f", venta.getCambio()));
            DialogPane dialogPane = alertExito.getDialogPane();
            dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            dialogPane.getStyleClass().addAll("alert", "alert-success");
            alertExito.showAndWait();

            cerrarVentana();
        } else {
            Alert alertError = new Alert(Alert.AlertType.ERROR);
            alertError.setTitle("Error de Transacción");
            alertError.setContentText("No se pudo registrar la venta en la base de datos.");
            DialogPane dialogPane = alertError.getDialogPane();
            dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            dialogPane.getStyleClass().addAll("alert", "alert-danger");
            alertError.showAndWait();
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
