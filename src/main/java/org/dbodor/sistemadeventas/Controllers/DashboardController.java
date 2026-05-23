package org.dbodor.sistemadeventas.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dbodor.sistemadeventas.DAO.TurnoDAO;
import org.dbodor.sistemadeventas.DAO.VentaDAO;
import org.dbodor.sistemadeventas.HelloApplication;
import org.dbodor.sistemadeventas.Model.Turno;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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

    private Stage stage = new Stage();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TurnoDAO turnoDAO = new TurnoDAO();
        Turno turnoActual = turnoDAO.getTurnoAbierto();

        if (turnoActual == null) {
            mostrarModalAperturaCaja();
        } else {
            LocalDate fechaAperturaTurno = turnoActual.getFechaApertura().toLocalDate();
            LocalDate fechaHoy = LocalDate.now(ZoneId.of("America/Bogota"));

            if (fechaAperturaTurno.isBefore(fechaHoy)) {
                double montoInicial = turnoActual.getMontoInicial();
                double totalVendido = turnoDAO.calcularTotalVentasDelTurno(turnoActual.getId());
                double montoFinalCalculado = montoInicial + totalVendido;

                DateTimeFormatter formatoVisual = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                Alert alertCierre = new Alert(Alert.AlertType.WARNING);
                alertCierre.setTitle("Cierre de Caja");
                alertCierre.setHeaderText("Turno abierto detectado de la fecha: " + fechaAperturaTurno.format(formatoVisual));
                alertCierre.setContentText(
                        "--- ARQUEO AUTOMÁTICO DE SEGURIDAD ---\n\n" +
                                "• Base Inicial: $" + String.format("%,.2f", montoInicial) + "\n" +
                                "• Ventas del Turno: $" + String.format("%,.2f", totalVendido) + "\n" +
                                "• Efectivo Estimado en Caja: $" + String.format("%,.2f", montoFinalCalculado) + "\n\n" +
                                "El sistema cerrará esta caja vieja y le pedirá la base para el día de hoy."
                );
                Stage stageAlerta = (Stage) alertCierre.getDialogPane().getScene().getWindow();

                java.net.URL url = getClass().getResource("/images/logo_cuadrado_toonout.png");
                if (url != null) {
                    stageAlerta.getIcons().add(new javafx.scene.image.Image(url.toExternalForm()));
                }

                DialogPane dialogPane = alertCierre.getDialogPane();
                dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
                dialogPane.getStyleClass().addAll("alert", "alert-danger");
                alertCierre.showAndWait();

                turnoDAO.cerrarTurno(montoFinalCalculado);

                mostrarModalAperturaCaja();
            } else {
                btnVenta.fire();
            }
        }

        javafx.application.Platform.runLater(() -> {
            stage = (Stage) btnCerrar.getScene().getWindow();

            stage.setOnCloseRequest(event -> {
                event.consume();

                TurnoDAO turnDAO = new TurnoDAO();
                if (turnDAO.hayTurnoAbierto()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Cierre de Aplicación");
                    alert.setHeaderText("¿Qué desea hacer?");
                    alert.setContentText("Aún hay un turno abierto. Puede cerrar el turno actual o salir sin cerrarlo.");

                    Stage stageAlerta = (Stage) alert.getDialogPane().getScene().getWindow();

                    java.net.URL url = getClass().getResource("/images/logo_cuadrado_toonout.png");
                    if (url != null) {
                        stageAlerta.getIcons().add(new javafx.scene.image.Image(url.toExternalForm()));
                    }

                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
                    dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
                    dialogPane.getStyleClass().addAll("alert", "alert-warning");

                    ButtonType btnCerrarTurno = new ButtonType("Cerrar Turno y Salir");
                    ButtonType btnSalirSinCerrar = new ButtonType("Salir sin cerrar");
                    ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

                    alert.getButtonTypes().setAll(btnCerrarTurno, btnSalirSinCerrar, btnCancelar);

                    alert.showAndWait().ifPresent(tipo -> {
                        if (tipo == btnCerrarTurno) {
                            cerrarCaja(null);
                        } else if (tipo == btnSalirSinCerrar) {
                            System.exit(0);
                        }
                    });
                } else {
                    System.exit(0);
                }
            });
        });
    }

    private void mostrarModalAperturaCaja() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/apertura_caja.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            stage.setScene(scene);

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.initStyle(StageStyle.UNDECORATED);

            stage.setOnCloseRequest(event -> event.consume());

            HelloApplication.aplicarIcono(stage);

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

        Stage stageAlerta = (Stage) alert.getDialogPane().getScene().getWindow();

        java.net.URL url = getClass().getResource("/images/logo_cuadrado_toonout.png");
        if (url != null) {
            stageAlerta.getIcons().add(new javafx.scene.image.Image(url.toExternalForm()));
        }

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        dialogPane.getStyleClass().addAll("alert", "alert-success");
        alert.showAndWait();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Stage stageAlerta = (Stage) alert.getDialogPane().getScene().getWindow();

        java.net.URL url = getClass().getResource("/images/logo_cuadrado_toonout.png");
        if (url != null) {
            stageAlerta.getIcons().add(new javafx.scene.image.Image(url.toExternalForm()));
        }

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        dialogPane.getStyleClass().addAll("alert", "alert-danger");
        alert.showAndWait();
    }

}
