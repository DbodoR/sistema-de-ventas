package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.dbodor.sistemadeventas.DAO.CategoriaDAO;
import org.dbodor.sistemadeventas.DAO.ProductoDAO;
import org.dbodor.sistemadeventas.Model.Categoria;
import org.dbodor.sistemadeventas.Model.Producto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EditarProductoController implements Initializable {

    @FXML
    private Button btnGuardar;

    @FXML
    private ComboBox<Categoria> boxCategoria;

    @FXML
    private CheckBox checkPrecioVariable;

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtCosto;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    private Producto productoAEditar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CategoriaDAO catDAO = new CategoriaDAO();
        List<Categoria> lista = catDAO.listarTodas();

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
    public void cargarDatosProducto(Producto producto) {
        this.productoAEditar = producto;

        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(String.valueOf(producto.getPrecio()));
        txtCosto.setText(String.valueOf(producto.getCosto()));
        txtStock.setText(String.valueOf(producto.getStock()));
        txtCodigo.setText(producto.getCodigoBarras());
        checkPrecioVariable.setSelected(producto.isPrecioVariable());

        for (Categoria cat : boxCategoria.getItems()) {
            if (cat.getId() == producto.getCategoria_id()) {
                boxCategoria.getSelectionModel().select(cat);
                break;
            }
        }
    }

    @FXML
    void guardarCambios(ActionEvent event) {
        productoAEditar.setNombre(txtNombre.getText().trim());
        productoAEditar.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        productoAEditar.setCosto(Double.parseDouble(txtCosto.getText().trim()));
        productoAEditar.setStock(Integer.parseInt(txtStock.getText().trim()));
        productoAEditar.setCodigoBarras(txtCodigo.getText().trim());
        productoAEditar.setPrecioVariable(checkPrecioVariable.isSelected());

        Categoria catSeleccionada = boxCategoria.getSelectionModel().getSelectedItem();
        productoAEditar.setCategoria_id(catSeleccionada != null ? catSeleccionada.getId() : 6);

        ProductoDAO dao = new ProductoDAO();
        if (dao.actualizarProducto(productoAEditar)) {
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } else {
        }
    }


}
