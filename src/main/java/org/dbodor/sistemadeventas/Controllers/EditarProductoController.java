package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.dbodor.sistemadeventas.DAO.CategoriaDAO;
import org.dbodor.sistemadeventas.DAO.ProductoDAO;
import org.dbodor.sistemadeventas.Model.Categoria;
import org.dbodor.sistemadeventas.Model.Producto;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.net.URL;
import java.util.List;
import java.util.Objects;
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

        txtStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtStock.setText(newValue.replaceAll("[^\\d]", ""));
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

        if (!validarCamposProducto()) {
            return;
        }

        productoAEditar.setNombre(txtNombre.getText().trim());
        productoAEditar.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
        productoAEditar.setCosto(Double.parseDouble(txtCosto.getText().trim()));
        productoAEditar.setStock(Integer.parseInt(txtStock.getText().trim()));
        productoAEditar.setCodigoBarras(txtCodigo.getText().trim());
        productoAEditar.setPrecioVariable(checkPrecioVariable.isSelected());

        Categoria catSeleccionada = boxCategoria.getSelectionModel().getSelectedItem();
        productoAEditar.setCategoria_id(catSeleccionada != null ? catSeleccionada.getId() : 1);

        ProductoDAO dao = new ProductoDAO();
        if (dao.actualizarProducto(productoAEditar)) {
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        } else {
        }
    }

    private boolean validarCamposProducto() {
        if (txtNombre.getText().trim().isEmpty() ||
                txtCosto.getText().trim().isEmpty() ||
                txtPrecio.getText().trim().isEmpty() ||
                txtStock.getText().trim().isEmpty() ||
                txtCodigo.getText().trim().isEmpty()){

            mostrarAlerta("Campos Vacíos", "Por favor, completa todos los campos obligatorios del producto.");
            return false;
        }

        try {
            double costo = Double.parseDouble(txtCosto.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            if (costo < 0 || precio < 0) {
                mostrarAlerta("Valores Negativos", "El costo y el precio no pueden ser menores a cero.");
                return false;
            }

            if (stock < 0) {
                mostrarAlerta("Stock Inválido", "El stock inicial o disponible no puede ser negativo.");
                return false;
            }

            if (precio < costo) {
                mostrarAlerta("Error", "El precio de venta no puede ser menor al costo");
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Asegúrate de ingresar solo números válidos en Costo, Precio y Stock.");
            return false;
        }

        return true;
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Stage stageAlerta = (Stage) alert.getDialogPane().getScene().getWindow();

        java.net.URL url = getClass().getResource("/images/logo_cuadrado_toonout.png");
        if (url != null) {
            stageAlerta.getIcons().add(new javafx.scene.image.Image(url.toExternalForm()));
        }

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        dialogPane.getStyleClass().addAll("alert", "alert-warning");
        alert.showAndWait();
    }


}
