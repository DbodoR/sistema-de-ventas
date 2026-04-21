package org.dbodor.sistemadeventas.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private StackPane areaContenido;

    public void cargarVenta() {
        try {
            // Cargamos el FXML hijo
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/venta.fxml"));
            Parent vistaHija = loader.load();

            areaContenido.getChildren().clear();
            areaContenido.getChildren().add(vistaHija);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
