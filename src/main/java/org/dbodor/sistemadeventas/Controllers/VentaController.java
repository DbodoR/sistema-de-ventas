package org.dbodor.sistemadeventas.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;

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
    private TextField txtBuscar;
}
