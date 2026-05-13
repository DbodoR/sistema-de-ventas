package org.dbodor.sistemadeventas.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import org.dbodor.sistemadeventas.DAO.ProductoDAO;
import org.dbodor.sistemadeventas.Model.Producto;

public class InventarioController {
    @FXML
    private Button btnProducto;

    @FXML
    private TableColumn<?, ?> colAccion;

    @FXML
    private TableColumn<?, ?> colCategoria;

    @FXML
    private TableColumn<?, ?> colCodigo;

    @FXML
    private TableColumn<?, ?> colCosto;

    @FXML
    private TableColumn<?, ?> colPrecio;

    @FXML
    private TableColumn<?, ?> colProducto;

    @FXML
    private TableColumn<?, ?> colStock;

    @FXML
    private TextField txtBuscar;

    @FXML
    void buscarProducto(ActionEvent event) {
        ProductoDAO productoDAO = new ProductoDAO();
        String criterio = txtBuscar.getText();
        Producto producto = productoDAO.buscar(criterio);


    }
}
