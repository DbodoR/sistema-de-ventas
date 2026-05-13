package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.dbodor.sistemadeventas.DAO.CategoriaDAO;
import org.dbodor.sistemadeventas.DAO.ProductoDAO;
import org.dbodor.sistemadeventas.Model.Categoria;
import org.dbodor.sistemadeventas.Model.Producto;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class IngresarArticuloController implements Initializable {
    @FXML
    private ComboBox<Categoria> boxCategoria;

    @FXML
    private CheckBox checkPrecioVariable;

    @FXML
    private TextField fieldCodigo;

    @FXML
    private TextField fieldCosto;

    @FXML
    private TextField fieldNombre;

    @FXML
    private TextField fieldPrecio;

    @FXML
    private TextField fieldStock;

    @FXML
    private TextField fieldNombreCategoria;

    @FXML
    void continuarCategoria(ActionEvent event) {

    }

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
    @FXML
    void agregarProducto(ActionEvent event) {

        if (fieldNombre.getText().isEmpty() || fieldCodigo.getText().isEmpty() || fieldCosto.getText().isEmpty() || fieldPrecio.getText().isEmpty() || fieldStock.getText().isEmpty()) {
            mostrarAlerta("Campos incompletos", "Por favor, llene todos los campos de texto.");
            return;
        }

        String costoStr = fieldCosto.getText().trim();
        String precioStr = fieldPrecio.getText().trim();
        String stockStr = fieldStock.getText().trim();

        double costo = 0;
        double precio = 0;
        int stock = 0;

        try {
            costo = Double.parseDouble(costoStr);
            precio = Double.parseDouble(precioStr);
            stock = Integer.parseInt(stockStr);

        } catch (NumberFormatException e) {
            mostrarAlerta("Formato inválido", "El costo, precio y stock deben ser números válidos. Use punto (.) para decimales.");
            return;
        }

        if (costo < 0 || precio < 0) {
            mostrarAlerta("Error de valores", "El costo y el precio no pueden ser negativos.");
            return;
        }

        if (stock < 0) {
            mostrarAlerta("Error de valores", "El stock inicial no puede ser negativo.");
            return;
        }

        boolean precioVariable = false;
        if (checkPrecioVariable != null) {
            precioVariable = checkPrecioVariable.isSelected();
        }

        int categoriaId = 6;
        if (boxCategoria.getSelectionModel().getSelectedItem() != null) {
                 categoriaId = boxCategoria.getSelectionModel().getSelectedItem().getId();
            }

        Producto producto = new Producto();
        producto.setNombre(fieldNombre.getText());
        producto.setCodigoBarras(fieldCodigo.getText().trim());
        producto.setCosto(Double.parseDouble(fieldCosto.getText().trim()));
        producto.setPrecio(Double.parseDouble(fieldPrecio.getText().trim()));
        producto.setStock(Integer.parseInt(fieldStock.getText().trim()));
        producto.setPrecioVariable(precioVariable);
        producto.setCategoria_id(categoriaId);

        ProductoDAO dao = new ProductoDAO();
        boolean exito = dao.agregarProducto(producto);

        if (exito) {
            mostrarInformacion("Éxito", "El producto se guardó correctamente.");
            limpiarFormulario();
        } else {
            mostrarAlerta("Error al guardar", "Hubo un problema al guardar. Intentelo de nuevo");
        }

    }

    @FXML
    void irAgregarCategoria(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/agregar_categoria.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.initStyle(StageStyle.UNDECORATED);

            stage.setOnCloseRequest(evento -> evento.consume());

            stage.showAndWait();

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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    void irEliminarCategoria(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/eliminar_categoria.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.initStyle(StageStyle.UNDECORATED);

            stage.setOnCloseRequest(evento -> evento.consume());

            stage.showAndWait();

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        fieldNombre.clear();
        fieldCodigo.clear();
        fieldCosto.clear();
        fieldPrecio.clear();
        fieldStock.clear();
        checkPrecioVariable.setSelected(false);
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
