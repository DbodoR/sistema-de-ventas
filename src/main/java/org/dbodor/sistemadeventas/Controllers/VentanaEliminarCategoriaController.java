package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.dbodor.sistemadeventas.DAO.CategoriaDAO;
import org.dbodor.sistemadeventas.Model.Categoria;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class VentanaEliminarCategoriaController implements Initializable {


    @FXML
    private Button btnEliminarCategoria;

    @FXML
    private ComboBox<Categoria> boxCategoria;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CategoriaDAO catDAO = new CategoriaDAO();
        List<Categoria> lista = catDAO.listarTodas();

        lista.removeIf(categoria -> categoria.getNombre() == "Sin categoria");

        boxCategoria.setItems(FXCollections.observableArrayList(lista));

        boxCategoria.setConverter(new StringConverter<Categoria>() {
            @Override
            public String toString(Categoria c) {
                return (c == null) ? "" : c.getNombre();
            }

            @Override
            public Categoria fromString(String string) {
                return null;
            }
        });
    }
    @FXML
    void eliminarCategoria(ActionEvent event) {
        int categoriaId = 0;
        if (boxCategoria.getSelectionModel().getSelectedItem() != null) {
            categoriaId = boxCategoria.getSelectionModel().getSelectedItem().getId();
        }

        CategoriaDAO catDAO = new CategoriaDAO();
        boolean exito = catDAO.eliminarCategoria(categoriaId);
        if (exito){
            mostrarInformacion("Éxito", "La categoría se eliminó correctamente.");
            Stage stage = (Stage) btnEliminarCategoria.getScene().getWindow();
            stage.close();
        }else mostrarAlerta("Error", "No se pudo eliminar la categoría.");

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
