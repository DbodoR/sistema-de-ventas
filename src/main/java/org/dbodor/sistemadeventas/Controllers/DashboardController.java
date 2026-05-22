package org.dbodor.sistemadeventas.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dbodor.sistemadeventas.DAO.TurnoDAO;
import org.dbodor.sistemadeventas.DAO.VentaDAO;
import org.dbodor.sistemadeventas.Model.Turno;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private StackPane areaContenido;

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnGraficas;

    @FXML
    private Button btnGuardarArticulo;

    @FXML
    private Button btnInventario;

    @FXML
    private Button btnVenta;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TurnoDAO turnoDAO = new TurnoDAO();

        if (!turnoDAO.hayTurnoAbierto()) {
            mostrarModalAperturaCaja();
        } else {
            btnVenta.fire();
        }
    }

    private void mostrarModalAperturaCaja() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/apertura_caja.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.initStyle(StageStyle.UNDECORATED);

            stage.setOnCloseRequest(event -> event.consume());

            stage.showAndWait();

            btnVenta.fire();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ventanaVenta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/venta.fxml"));
            Parent vistaHija = loader.load();

            areaContenido.getChildren().clear();
            areaContenido.getChildren().add(vistaHija);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ventanaIngresarProducto(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ingresar_articulo.fxml"));
            Parent vistaHija = loader.load();

            areaContenido.getChildren().clear();
            areaContenido.getChildren().add(vistaHija);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ventanaInventario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/inventario.fxml"));
            Parent vistaHija = loader.load();

            areaContenido.getChildren().clear();
            areaContenido.getChildren().add(vistaHija);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void ventanaGraficas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reportes.fxml"));
            Parent vistaHija = loader.load();

            areaContenido.getChildren().clear();
            areaContenido.getChildren().add(vistaHija);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void cerrarCaja(ActionEvent event) {
        TurnoDAO turnoDAO = new TurnoDAO();
        Turno turnoAbierto = turnoDAO.getTurnoAbierto();

        if (turnoAbierto != null) {
            VentaDAO ventaDAO = new VentaDAO();

            double totalEfectivo = ventaDAO.calcularTotalVentasPorTurno(turnoAbierto.getId());
            double totalTransferencias = ventaDAO.calcularTotalTransferenciasPorTurno(turnoAbierto.getId());

            double montoFinalFisico = turnoAbierto.getMontoInicial() + totalEfectivo;

            boolean exito = turnoDAO.cerrarTurno(montoFinalFisico);

            if (exito) {
                String mensaje = String.format(
                        "El turno se ha cerrado exitosamente.\n\n" +
                                "Dinero Base Inicial: $%.2f\n" +
                                "Ventas en Efectivo: $%.2f\n" +
                                "Ventas por Transferencia: $%.2f\n\n" +
                                "=======================\n" +
                                "TOTAL ESPERADO EN CAJA (Físico): $%.2f\n" +
                                "=======================\n\n",
                        turnoAbierto.getMontoInicial(), totalEfectivo, totalTransferencias, montoFinalFisico);

                mostrarInformacion("Caja Cerrada - Resumen de Turno", mensaje);
                System.exit(0);
            }
        } else {
            mostrarAlerta("Error", "No hay ningún turno abierto para cerrar.");
        }
    }

    private void mostrarInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
        dialogPane.getStyleClass().addAll("alert", "alert-success");
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        dialogPane.getStyleClass().addAll("alert", "alert-danger");
        alert.showAndWait();
    }

}
