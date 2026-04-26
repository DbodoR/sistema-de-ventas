package org.dbodor.sistemadeventas.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VentaController {
    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnCobrar;

    @FXML
    private Button btnProducto;

    @FXML
    private TableColumn<?, ?> colAcciones;

    @FXML
    private TableColumn<?, ?> colCantidad;

    @FXML
    private TableColumn<?, ?> colCodigo;

    @FXML
    private TableColumn<?, ?> colPrecio;

    @FXML
    private TableColumn<?, ?> colProducto;

    @FXML
    private TableColumn<?, ?> colSubtotal;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<?> tablaVenta;

    @FXML
    private TextField txtBuscar;
}
