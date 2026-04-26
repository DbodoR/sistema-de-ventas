package org.dbodor.sistemadeventas.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.DAO.TurnoDAO;
import org.kordamp.bootstrapfx.BootstrapFX;

public class AperturaCajaController {

    @FXML
    private Button btnAbrirTurno;

    @FXML
    private TextField txtMontoInicial;

    @FXML
    void abrirTurno(ActionEvent event) {
        String montoStr = txtMontoInicial.getText();

        if (montoStr == null || montoStr.trim().isEmpty()) {
            mostrarAlerta("Error", "Por favor, ingrese el dinero base para iniciar.");
            return;
        }

        try {
            double montoInicial = Double.parseDouble(montoStr);


            if (montoInicial < 0) {
                mostrarAlerta("Error", "El monto en caja no puede ser negativo.");
                return;
            }

            TurnoDAO turnoDAO = new TurnoDAO();

            boolean exito = turnoDAO.abrirTurno(montoInicial);

            if (exito) {
                Stage stage = (Stage) btnAbrirTurno.getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta("Error de Base de Datos", "No se pudo registrar el turno.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "Por favor ingrese solo números (Ej: 50000 o 50000.50).");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        dialogPane.getStyleClass().add("alert-danger");
        alert.showAndWait();
    }
}