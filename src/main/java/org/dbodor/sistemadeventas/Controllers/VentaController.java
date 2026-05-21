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
            private final Button btnEliminar = new Button("X");
            private final HBox contenedorCentrado = new HBox(btnEliminar);

            {
                contenedorCentrado.setAlignment(Pos.CENTER);

                btnEliminar.setStyle(
                        "-fx-font-weight: bold; " +
                                "-fx-font-size: 15px; " +
                                "-fx-pref-width: 32px; " +
                                "-fx-pref-height: 32px; " +
                                "-fx-padding: 0; " +
                                "-fx-background-radius: 5px;" +
                                "-fx-background-color: red;" +
                                "-fx-text-fill: white;"
                );
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    btnEliminar.setOnAction(event -> {
                        Producto productoFila = getTableView().getItems().get(getIndex());

                        if (productoFila.getCantidad() > 1) {
                            productoFila.setCantidad(productoFila.getCantidad() - 1);
                            getTableView().refresh();
                        } else carritoCompras.remove(productoFila);

                        calcularTotal();
                    });

                    setStyle("-fx-alignment: CENTER;");
                    setGraphic(contenedorCentrado);
                    setText(null);
                }
            }
        });
        expandirYBloquearColumnas(tablaVenta);

        if (tablaVenta.getItems().isEmpty()) {
            btnCobrar.setDisable(true);
        }
        tablaVenta.setStyle("-fx-font-size: 15px;");
    }

    private void expandirYBloquearColumnas(TableView<Producto> tabla) {
        for (TableColumn<Producto, ?> columna : tabla.getColumns()) {
            columna.setReorderable(false);
            columna.setResizable(false);
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
