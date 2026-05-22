package org.dbodor.sistemadeventas.Controllers;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.DAO.ProductoDAO;
import org.dbodor.sistemadeventas.DAO.TurnoDAO;
import org.dbodor.sistemadeventas.Model.DetalleVenta;
import org.dbodor.sistemadeventas.Model.Producto;
import javafx.scene.control.cell.PropertyValueFactory;
import org.dbodor.sistemadeventas.Model.Turno;
import org.dbodor.sistemadeventas.Model.Venta;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class VentaController implements Initializable {

    @FXML
    private Button btnCobrar;

    @FXML
    private TableColumn<Producto, String> colAcciones;

    @FXML
    private TableColumn<Producto, Integer> colCantidad;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, String> colProducto;

    @FXML
    private TableColumn<Producto, Double> colSubtotal;

    @FXML
    private Label lblTotal;

    @FXML
    private TableView<Producto> tablaVenta;

    @FXML
    private TextField txtBuscar;

    private ObservableList<Producto> carritoCompras = FXCollections.observableArrayList();
    private double totalVenta = 0.0;

    private Producto producto = null;

    private TurnoDAO turnoDAO = new TurnoDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        colSubtotal.setCellValueFactory(cellData -> {
            Producto p = cellData.getValue();
            double subtotal = p.getPrecio() * p.getCantidad();
            return new SimpleDoubleProperty(subtotal).asObject();
        });

        colAcciones.setCellFactory(param -> new TableCell<Producto, String>() {
            // Componentes visuales de la celda
            private final Button btnMas = new Button("+");
            private final Button btnMenos = new Button("-");
            private final HBox contenedorBotonera = new HBox(5, btnMas, btnMenos); // 5px de separación espacial

            {
                // 1. Estilizar los botones con BootstrapFX y tu style.css
                btnMas.getStyleClass().addAll("btn-success", "btn-sm");
                btnMas.setStyle("-fx-font-size: 15px");
                btnMas.setStyle("-fx-font-weight: bold");
                btnMenos.getStyleClass().addAll("btn-danger", "btn-sm");
                btnMenos.setStyle("-fx-font-size: 15px");
                btnMenos.setStyle("-fx-font-weight: bold");
                contenedorBotonera.setAlignment(javafx.geometry.Pos.CENTER);

                // --- ACCIÓN DEL BOTÓN AUMENTAR (+) ---
                btnMas.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    if (producto != null) {
                        if (producto.getCantidad() < producto.getStock()) {
                            producto.setCantidad(producto.getCantidad() + 1);

                            getTableView().refresh();
                            calcularTotal();
                        } else {
                            mostrarAlerta("Límite de Stock", "No hay más unidades disponibles de: " + producto.getNombre());
                        }
                    }
                });

                // --- ACCIÓN DEL BOTÓN DISMINUIR (-) ---
                btnMenos.setOnAction(event -> {
                    Producto producto = getTableView().getItems().get(getIndex());
                    if (producto != null) {
                        if (producto.getCantidad() > 1) {
                            // Si hay más de una unidad, restamos una
                            producto.setCantidad(producto.getCantidad() - 1);
                        } else {
                            // Si la cantidad llega a 1 y presionan -, se remueve por completo del carrito
                            getTableView().getItems().remove(producto);
                        }

                        // Refrescar la tabla y recalcular el total de la venta actual
                        getTableView().refresh();
                        calcularTotal();
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // Mostramos el HBox con ambos botones en la fila correspondiente
                    setGraphic(contenedorBotonera);
                }
            }
        });
        expandirYBloquearColumnas(tablaVenta);

        if (tablaVenta.getItems().isEmpty()) {
            btnCobrar.setDisable(true);
        }
        tablaVenta.setStyle("-fx-font-size: 15px;");

        tablaVenta.setPlaceholder(new Label("No hay articulos en el carrito."));
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
            tabla.getColumns().get(1).setPrefWidth(anchoEfectivo * 0.35);
            tabla.getColumns().get(2).setPrefWidth(anchoEfectivo * 0.12);
            tabla.getColumns().get(3).setPrefWidth(anchoEfectivo * 0.10);
            tabla.getColumns().get(4).setPrefWidth(anchoEfectivo * 0.13);
            tabla.getColumns().get(5).setPrefWidth(anchoEfectivo * 0.15);
        });
    }

    @FXML
    void buscarProducto(ActionEvent event) {
        ProductoDAO proDAO = new ProductoDAO();
        String criterio = txtBuscar.getText();
        if (criterio.isEmpty()) {
            return;
        }
        producto = proDAO.buscar(criterio);

        if (producto != null) {
            agregarProductoAlCarrito(producto);
            txtBuscar.clear();
            txtBuscar.requestFocus();
        }
    }

    @FXML
    private void manejarBusquedaProducto(ActionEvent event) {
        String entrada = txtBuscar.getText().trim();
        if (entrada.isEmpty()) return;

        ProductoDAO productoDAO = new ProductoDAO();
        Producto producto = productoDAO.buscar(entrada);

        if (producto != null) {
            agregarProductoAlCarrito(producto);
            txtBuscar.clear();
        } else {
            List<Producto> coincidencias = productoDAO.buscarPorNombreAproximado(entrada);

            if (coincidencias.isEmpty()) {
                mostrarAlerta("Sin resultados", "No se encontró ningún producto con ese código o nombre.");
                txtBuscar.selectAll();
            } else if (coincidencias.size() == 1) {
                agregarProductoAlCarrito(coincidencias.get(0));
                txtBuscar.clear();
            } else {
                abrirModalSeleccionProducto(coincidencias);
            }
        }
    }

    private void abrirModalSeleccionProducto(List<Producto> productos) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/buscar_producto.fxml"));
            Parent root = loader.load();

            BuscarProductoController controller = loader.getController();
            controller.cargarProductos(productos);

            Stage modalStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
            modalStage.setTitle("Seleccionar Producto");
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modalStage.initOwner(txtBuscar.getScene().getWindow());
            modalStage.setScene(scene);
            modalStage.setResizable(false);
            // Aplicar el icono de la app si lo creaste en los pasos anteriores
            // HelloApplication.aplicarIcono(modalStage);

            modalStage.showAndWait();

            Producto elegido = controller.getProductoSeleccionado();
            if (elegido != null) {
                agregarProductoAlCarrito(elegido);
                txtBuscar.clear();
            } else {
                txtBuscar.selectAll();
            }

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la ventana de búsqueda.");
        }
    }

    private void agregarProductoAlCarrito(Producto producto) {
        for (Producto p : carritoCompras) {
            if (p.getId() == producto.getId()) {
                p.setCantidad(p.getCantidad() + 1);

                tablaVenta.refresh();
                calcularTotal();
                return;
            }
        }

        producto.setCantidad(1);
        carritoCompras.add(producto);
        calcularTotal();
        tablaVenta.setItems(carritoCompras);

        btnCobrar.setDisable(false);
    }

    private void calcularTotal() {
        totalVenta = 0.0;
        for (Producto p : carritoCompras) {
            totalVenta += p.getPrecio() * p.getCantidad();
        }
        lblTotal.setText(String.format("$ %.2f", totalVenta));
    }

    @FXML
    void cancelarVenta(ActionEvent event) {
        for (Producto p : carritoCompras) {
            p.setCantidad(0);
        }

        tablaVenta.getItems().clear();
        carritoCompras.clear();
        producto = null;
        calcularTotal();
        btnCobrar.setDisable(true);
        txtBuscar.clear();
        txtBuscar.requestFocus();
    }

    @FXML
    private void cobrarVenta(ActionEvent event) {

        if (carritoCompras.isEmpty()) return;
        if (!turnoDAO.hayTurnoAbierto()) return;

        ProductoDAO proDAO = new ProductoDAO();
        StringBuilder productosSinStock = new StringBuilder();

        for (Producto p : carritoCompras) {
            Producto productoBD = proDAO.buscar(p.getCodigoBarras());

            if (productoBD == null) {
                productosSinStock.append(String.format("• %s (No encontrado en el sistema)\n", p.getNombre()));
            } else if (productoBD.getStock() < p.getCantidad()) {
                productosSinStock.append(String.format("• %s\n  Llevas: %d  |  Disponible en inventario: %d\n\n",
                        p.getNombre(), p.getCantidad(), productoBD.getStock()));
            }
        }

        if (productosSinStock.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Stock Insuficiente");
            alert.setHeaderText("No se puede proceder con la venta:");
            alert.setContentText(productosSinStock.toString());

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            dialogPane.getStyleClass().addAll("alert", "alert-danger");

            dialogPane.setMinHeight(Region.USE_PREF_SIZE);
            dialogPane.setMinWidth(Region.USE_PREF_SIZE);

            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cobro.fxml"));
            Parent root = loader.load();

            CobroController cobroController = loader.getController();

            double totalActual = carritoCompras.stream()
                    .mapToDouble(p -> p.getPrecio() * p.getCantidad())
                    .sum();

            Turno turno = turnoDAO.getTurnoAbierto();

            if (turno == null) {
                mostrarAlerta("Error de Turno", "No se pudo recuperar la información del turno actual. Intente reabrir caja.");
                return;
            }

            cobroController.setData(totalActual, new ArrayList<>(carritoCompras), turno.getId());

            Stage modalStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            modalStage.setScene(scene);
            modalStage.setTitle("Caja de Cobro");
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modalStage.initOwner(btnCobrar.getScene().getWindow());
            modalStage.setResizable(false);

            modalStage.showAndWait();

            if (cobroController.isVentaRealizada()) {
                carritoCompras.clear();

                calcularTotal();
                txtBuscar.clear();
                txtBuscar.requestFocus();

                btnCobrar.setDisable(true);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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
