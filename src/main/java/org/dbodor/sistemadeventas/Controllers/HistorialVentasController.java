package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.DAO.VentaDAO;
import org.dbodor.sistemadeventas.HelloApplication;
import org.dbodor.sistemadeventas.Model.DetalleVenta;
import org.dbodor.sistemadeventas.Model.Producto;
import org.dbodor.sistemadeventas.Model.Venta;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class HistorialVentasController implements Initializable {

    @FXML
    private Button btnExpandir;
    @FXML
    private DatePicker dpFecha;
    @FXML private ComboBox<String> cmbMetodoPago;

    @FXML private TableView<Venta> tblVentas;
    @FXML private TableColumn<Venta, Integer> colId;
    @FXML private TableColumn<Venta, String> colMetodo;
    @FXML private TableColumn<Venta, Double> colEfectivo;
    @FXML private TableColumn<Venta, Double> colTransferencia;
    @FXML private TableColumn<Venta, Double> colTotal;
    @FXML private TableColumn<Venta, Double> colCambio;

    private final VentaDAO ventaDAO = new VentaDAO();
    private final ObservableList<Venta> ventasObservable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMetodo.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEfectivo.setCellValueFactory(new PropertyValueFactory<>("pagoEfectivo"));
        colTransferencia.setCellValueFactory(new PropertyValueFactory<>("pagoTransferencia"));
        colCambio.setCellValueFactory(new PropertyValueFactory<>("cambio"));
        expandirYBloquearColumnas(tblVentas);

        dpFecha.setValue(LocalDate.now());
        cmbMetodoPago.getItems().addAll("TODOS", "EFECTIVO", "TRANSFERENCIA", "MIXTO");
        cmbMetodoPago.setValue("TODOS");

        consultarVentas();

        dpFecha.valueProperty().addListener((obs, viejo, nuevo) -> consultarVentas());
        cmbMetodoPago.getSelectionModel().selectedItemProperty().addListener((obs, viejo, nuevo) -> consultarVentas());

        btnExpandir.setDisable(true);

        tblVentas.getSelectionModel().selectedItemProperty().addListener((obs, viejaSeleccion, nuevaSeleccion) -> {
            btnExpandir.setDisable(nuevaSeleccion == null);
        });
    }

    private void expandirYBloquearColumnas(TableView<Venta> tabla) {
        for (TableColumn<Venta, ?> columna : tabla.getColumns()) {
            columna.setReorderable(false);
            columna.setResizable(false);
            columna.setSortable(false);
        }

        tabla.widthProperty().addListener((observable, oldValue, newValue) -> {
            double anchoTotal = newValue.doubleValue();
            double anchoEfectivo = anchoTotal - 2;

            tabla.getColumns().get(0).setPrefWidth(anchoEfectivo * 0.166);
            tabla.getColumns().get(1).setPrefWidth(anchoEfectivo * 0.166);
            tabla.getColumns().get(2).setPrefWidth(anchoEfectivo * 0.166);
            tabla.getColumns().get(3).setPrefWidth(anchoEfectivo * 0.166);
            tabla.getColumns().get(4).setPrefWidth(anchoEfectivo * 0.166);
            tabla.getColumns().get(5).setPrefWidth(anchoEfectivo * 0.166);
        });
    }

    private void consultarVentas() {
        ventasObservable.clear();

        if (dpFecha.getValue() == null) return;

        String fechaStr = dpFecha.getValue().toString(); // Formato nativo "YYYY-MM-DD"
        String filtroMetodo = cmbMetodoPago.getValue();

        // Traer datos de SQLite
        List<Venta> resultados = ventaDAO.obtenerVentasPorFiltros(fechaStr, filtroMetodo);
        ventasObservable.addAll(resultados);
        tblVentas.setItems(ventasObservable);
    }

    @FXML
    void expandirDetalle(ActionEvent event) {
        Venta ventaSeleccionada = tblVentas.getSelectionModel().getSelectedItem();

        if (ventaSeleccionada == null) return;

        List<DetalleVenta> detalles = ventaDAO.obtenerDetallesPorVentaId(ventaSeleccionada.getId());

        if (detalles.isEmpty()) {
            mostrarAlerta("Sin registros", "No se encontraron productos asociados a esta venta.");
            return;
        }

        mostrarModalDetalleProductos(ventaSeleccionada.getId(), detalles);
    }

    private void mostrarModalDetalleProductos(int ventaId, List<DetalleVenta> detalles) {

        Stage detalleStage = new Stage();
        detalleStage.setTitle("Artículos de la Venta N° " + ventaId);
        detalleStage.initModality(Modality.APPLICATION_MODAL);
        detalleStage.initOwner(btnExpandir.getScene().getWindow());

        TableView<DetalleVenta> tblDetalle = new TableView<>();

        TableColumn<DetalleVenta, String> colProd = new TableColumn<>("Producto");
        colProd.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colProd.setPrefWidth(200);

        TableColumn<DetalleVenta, Integer> colCant = new TableColumn<>("Cantidad");
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCant.setPrefWidth(80);

        TableColumn<DetalleVenta, Double> colPrecio = new TableColumn<>("Precio Unitario");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecio.setPrefWidth(120);

        TableColumn<DetalleVenta, Double> colSub = new TableColumn<>("Subtotal");
        colSub.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        colSub.setPrefWidth(120);

        tblDetalle.getColumns().addAll(colProd, colCant, colPrecio, colSub);

        ObservableList<DetalleVenta> data = FXCollections.observableArrayList(detalles);

        for (TableColumn<DetalleVenta, ?> columna : tblDetalle.getColumns()) {
            columna.setReorderable(false);
            columna.setResizable(false);
            columna.setSortable(false);
        }

        tblDetalle.setItems(data);

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 15; -fx-alignment: center;");
        layout.getChildren().add(tblDetalle);

        Scene scene = new Scene(layout, 540, 300);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        detalleStage.setScene(scene);
        detalleStage.setResizable(false);
        HelloApplication.aplicarIcono(detalleStage);
        detalleStage.show();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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
        dialogPane.getStyleClass().addAll("alert", "alert-danger");
        alert.showAndWait();
    }
}
