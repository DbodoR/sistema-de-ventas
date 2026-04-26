package org.dbodor.sistemadeventas.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

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

}
