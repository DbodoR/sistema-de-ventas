package org.dbodor.sistemadeventas.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.dbodor.sistemadeventas.DAO.CategoriaDAO;
import org.dbodor.sistemadeventas.DAO.ProductoDAO;
import org.dbodor.sistemadeventas.Model.Producto;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colCosto;

    @FXML
    private TableColumn<Producto, String> colPrecio;

    @FXML
    private TableColumn<Producto, String> colProducto;

    @FXML
    private TableColumn<Producto, String> colStock;

    @FXML
    private TableView<Producto> inventarioTabla;

    @FXML
    private TableColumn<Producto, String> colPrecioVariable;

    @FXML
    private TextField txtBuscar;

    private Producto producto = null;
    private String query = null;
    private ResultSet rs = null;

    private ObservableList<Producto> listaProducto = FXCollections.observableArrayList();
    private FilteredList<Producto> filtroBusqueda;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventarioTabla.setStyle("-fx-font-size: 15px;");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colCategoria.setCellValueFactory(cellData -> {
            int idCategoria = cellData.getValue().getCategoria_id();

            CategoriaDAO catDAO = new CategoriaDAO();
            String nombreCategoria = catDAO.obtenerNombrePorId(idCategoria);

            if (nombreCategoria == null || nombreCategoria.isEmpty()) {
                nombreCategoria = "Sin categoría";
            }

            return new SimpleStringProperty(nombreCategoria);
        });
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costo"));
        colPrecio.setCellValueFactory(new  PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colPrecioVariable.setCellValueFactory(cellData -> {
            boolean esVariable = cellData.getValue().isPrecioVariable();
            return new SimpleStringProperty(esVariable ? "Variable" : "Fijo");});

        inventarioTabla.setPlaceholder(new Label("No hay productos registrados."));

        colProducto.setCellFactory(column -> new TableCell<Producto, String>() {
            private final Tooltip tooltip = new Tooltip();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    tooltip.setText(item);
                    tooltip.setStyle("-fx-font-size: 14px;");

                    setTooltip(tooltip);
                }
            }
        });



        filtroBusqueda = new FilteredList<>(listaProducto, p -> true);
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filtroBusqueda.setPredicate(producto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String minusculas = newValue.toLowerCase();
                if (producto.getNombre().toLowerCase().contains(minusculas)) {
                    return true;
                } else if (producto.getCodigoBarras() != null && producto.getCodigoBarras().contains(minusculas)) {
                    return true;
                }

                return false;
            });
        });
        inventarioTabla.setItems(filtroBusqueda);
        expandirYBloquearColumnas(inventarioTabla);
        recargarLista();
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


            tabla.getColumns().get(0).setPrefWidth(anchoEfectivo * 0.15);
            tabla.getColumns().get(1).setPrefWidth(anchoEfectivo * 0.25);
            tabla.getColumns().get(2).setPrefWidth(anchoEfectivo * 0.15);
            tabla.getColumns().get(3).setPrefWidth(anchoEfectivo * 0.11);
            tabla.getColumns().get(4).setPrefWidth(anchoEfectivo * 0.12);
            tabla.getColumns().get(5).setPrefWidth(anchoEfectivo * 0.12);
            tabla.getColumns().get(6).setPrefWidth(anchoEfectivo * 0.10);
        });
    }

    private void recargarLista(){
        listaProducto.clear();
        query = "SELECT * FROM productos ORDER BY nombre ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                listaProducto.add(new Producto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getInt("stock"),
                        rs.getInt("categoria_id"),
                        rs.getString("codigo_barras"),
                        rs.getBoolean("precio_variable"),
                        rs.getDouble("costo")
                ));
            }
            inventarioTabla.setItems(filtroBusqueda);

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void editarProductos(ActionEvent event) {
        Producto seleccionado = inventarioTabla.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un producto de la tabla para modificar.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editar_producto.fxml"));
            Parent root = loader.load();

            EditarProductoController controller = loader.getController();
            controller.cargarDatosProducto(seleccionado);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            recargarLista();

        } catch (IOException e) {
            e.printStackTrace();
        }
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


}