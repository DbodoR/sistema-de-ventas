package org.dbodor.sistemadeventas.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.dbodor.sistemadeventas.Model.DetalleVenta;
import org.dbodor.sistemadeventas.Model.Producto;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dbodor.sistemadeventas.Model.Venta;

import java.net.URL;
import java.util.ResourceBundle;

public class VentaController implements Initializable {
    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnCobrar;

    @FXML
    private Button btnProducto;

    @FXML
    private TableColumn<Producto, String> colAcciones;

    @FXML
    private TableColumn<DetalleVenta, Integer> colCantidad;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, String> colProducto;

    @FXML
    private TableColumn<Venta, Double> colSubtotal;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<Producto> tablaVenta;

    @FXML
    private TextField txtBuscar;

    private double totalVenta = 0.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablaVenta.editableProperty().setValue(false);
        
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        if (tablaVenta.getItems().isEmpty()) {
            btnCobrar.setDisable(true);
        }
    }
}
