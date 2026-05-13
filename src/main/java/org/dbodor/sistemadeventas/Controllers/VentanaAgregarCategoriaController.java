package org.dbodor.sistemadeventas.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.DAO.CategoriaDAO;
import org.kordamp.bootstrapfx.BootstrapFX;

public class VentanaAgregarCategoriaController {

    @FXML
    private TextField fieldNombreCategoria;

    @FXML
    private Button btnAgregarCategoria;

    @FXML
    void continuarCategoria(ActionEvent event) {
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        boolean exito = categoriaDAO.nuevaCategoria(fieldNombreCategoria.getText());
        if (exito) {
            mostrarInformacion("Éxito", "La categoría se guardó correctamente.");
            Stage stage = (Stage) btnAgregarCategoria.getScene().getWindow();
            stage.close();
        }else mostrarAlerta("Error", "No se pudo guardar la categoría.");
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
}
