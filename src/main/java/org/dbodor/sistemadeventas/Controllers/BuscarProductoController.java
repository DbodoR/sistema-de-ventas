package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.Model.Producto;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BuscarProductoController implements Initializable {

    @FXML
    private TableView<Producto> tablaBusqueda;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colNombre;
    @FXML private TableColumn<Producto, Double> colPrecio;

    @FXML
    private Button btnSeleccionarProducto;

    private Producto productoSeleccionado = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        expandirYBloquearColumnas(tablaBusqueda);

        // Atajo: Doble clic en la fila para seleccionar de inmediato
        tablaBusqueda.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tablaBusqueda.getSelectionModel().getSelectedItem() != null) {
                productoSeleccionado = tablaBusqueda.getSelectionModel().getSelectedItem();
                Stage stage = (Stage) tablaBusqueda.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void expandirYBloquearColumnas(TableView<Producto> tabla) {
        for (TableColumn<Producto, ?> columna : tabla.getColumns()) {
            columna.setReorderable(false);
            columna.setResizable(false);
            columna.setSortable(false);
        }

        tabla.widthProperty().addListener((observable, oldValue, newValue) -> {
            double anchoTotal = newValue.doubleValue();
            double anchoEfectivo = anchoTotal - 2;

            tabla.getColumns().get(0).setPrefWidth(anchoEfectivo * 0.33);
            tabla.getColumns().get(1).setPrefWidth(anchoEfectivo * 0.33);
            tabla.getColumns().get(2).setPrefWidth(anchoEfectivo * 0.33);
        });
    }

    public void cargarProductos(List<Producto> productos) {
        tablaBusqueda.setItems(FXCollections.observableArrayList(productos));
        if (!productos.isEmpty()) {
            tablaBusqueda.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void seleccionarProducto(ActionEvent event) {
        productoSeleccionado = tablaBusqueda.getSelectionModel().getSelectedItem();
        Stage stage = (Stage) btnSeleccionarProducto.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) tablaBusqueda.getScene().getWindow();
        stage.close();
    }

    public Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }
}
