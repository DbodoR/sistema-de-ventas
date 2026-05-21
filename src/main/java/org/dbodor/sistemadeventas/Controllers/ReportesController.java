package org.dbodor.sistemadeventas.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.dbodor.sistemadeventas.DAO.ReporteDAO;
import org.dbodor.sistemadeventas.Model.Producto;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportesController implements Initializable {

    @FXML
    private HBox contenedorGraficos;

    @FXML private PieChart chartDiario;
    @FXML private StackedBarChart<String, Number> chartSemanal;
    @FXML private StackedBarChart<String, Number> chartMensual;

    @FXML private TableView<ResumenMes> tablaAnual;
    @FXML private TableColumn<ResumenMes, String> colMes;
    @FXML private TableColumn<ResumenMes, Integer> colTransacciones;
    @FXML private TableColumn<ResumenMes, Double> colIngresos;
    @FXML private TableColumn<ResumenMes, Double> colCostos;
    @FXML private TableColumn<ResumenMes, Double> colBeneficio;

    @FXML private Label lblVentasAnuales;
    @FXML private Label lblBeneficioAnual;
    @FXML private Label lblCatMasVendida;
    @FXML private Label lblProdMasVendido;

    @FXML
    private ComboBox<String> comboAnio;

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private final ReporteDAO reporteDAO = new ReporteDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        distribuirEspacioGraficos();
        configurarColumnasTabla();
        cargarReporteDiario();
        cargarReporteSemanal();
        cargarReporteMensual();

        List<String> aniosDisponibles = reporteDAO.obtenerAniosConVentas();
        comboAnio.setItems(FXCollections.observableArrayList(aniosDisponibles));
        comboAnio.getSelectionModel().selectFirst();

        String anioInicial = comboAnio.getSelectionModel().getSelectedItem();
        cargarDatosTablaYKPIs(anioInicial);

        comboAnio.setOnAction(event -> {
            String anioSeleccionado = comboAnio.getSelectionModel().getSelectedItem();
            if (anioSeleccionado != null) {
                cargarDatosTablaYKPIs(anioSeleccionado);
            }
        });
    }

    private void cargarDatosTablaYKPIs(String anio) {
        ObservableList<ResumenMes> listaTabla = FXCollections.observableArrayList();
        List<Map<String, Object>> resumenAnual = reporteDAO.obtenerResumenAnualPorMeses(anio);

        double acumuladoVentasAnual = 0.0;
        double acumuladoBeneficioAnual = 0.0;

        for (Map<String, Object> fila : resumenAnual) {
            double ingresos = (double) fila.get("ingresos");
            double beneficio = (double) fila.get("beneficio");

            acumuladoVentasAnual += ingresos;
            acumuladoBeneficioAnual += beneficio;

            listaTabla.add(new ResumenMes(
                    (String) fila.get("mes"),
                    (int) fila.get("transacciones"),
                    ingresos,
                    (double) fila.get("costos"),
                    beneficio
            ));
        }

        // Poblamos la tabla con los datos del año filtrado
        tablaAnual.setItems(listaTabla);

        // Actualizamos las tarjetas métricas correspondientes a ese año específico
        lblVentasAnuales.setText(String.format("$ %.2f", acumuladoVentasAnual));
        lblBeneficioAnual.setText(String.format("$ %.2f", acumuladoBeneficioAnual));
        lblProdMasVendido.setText(reporteDAO.obtenerProductoMasVendido(anio));
        lblCatMasVendida.setText(reporteDAO.obtenerCategoriaMasVendida(anio));
    }

    private void distribuirEspacioGraficos() {
        HBox.setHgrow(chartDiario, Priority.ALWAYS);
        HBox.setHgrow(chartSemanal, Priority.ALWAYS);
        HBox.setHgrow(chartMensual, Priority.ALWAYS);

        chartDiario.setMaxWidth(Double.MAX_VALUE);
        chartSemanal.setMaxWidth(Double.MAX_VALUE);
        chartMensual.setMaxWidth(Double.MAX_VALUE);

        chartDiario.setPrefWidth(100);
        chartSemanal.setPrefWidth(100);
        chartMensual.setPrefWidth(100);
    }

    private void configurarColumnasTabla() {
        colMes.setCellValueFactory(new PropertyValueFactory<>("mes"));
        colTransacciones.setCellValueFactory(new PropertyValueFactory<>("transacciones"));
        colIngresos.setCellValueFactory(new PropertyValueFactory<>("ingresos"));
        colCostos.setCellValueFactory(new PropertyValueFactory<>("costos"));
        colBeneficio.setCellValueFactory(new PropertyValueFactory<>("beneficio"));

        tablaAnual.setPlaceholder(new Label("No hay registros de ventas en el año actual."));
        tablaAnual.setStyle("-fx-font-size: 15px;");

        for (TableColumn<ResumenMes, ?> columna : tablaAnual.getColumns()) {
            columna.setReorderable(false);
            columna.setResizable(false);
            columna.setSortable(false);
        }

        tablaAnual.widthProperty().addListener((observable, oldValue, newValue) -> {
            double anchoTotal = newValue.doubleValue();

            double anchoEfectivo = anchoTotal - 2;

            tablaAnual.getColumns().get(0).setPrefWidth(anchoEfectivo * 0.2);
            tablaAnual.getColumns().get(1).setPrefWidth(anchoEfectivo * 0.2);
            tablaAnual.getColumns().get(2).setPrefWidth(anchoEfectivo * 0.2);
            tablaAnual.getColumns().get(3).setPrefWidth(anchoEfectivo * 0.2);
            tablaAnual.getColumns().get(4).setPrefWidth(anchoEfectivo * 0.2);
        });
    }

    private void cargarReporteDiario() {
        Map<String, Double> datosHoy = reporteDAO.obtenerDistribucionCajaHoy();
        double costo = datosHoy.get("costo");
        double beneficio = datosHoy.get("beneficio");

        if (costo == 0 && beneficio == 0) {
            chartDiario.setTitle("Hoy: Sin Ventas");
            chartDiario.setData(FXCollections.emptyObservableList());
            return;
        }

        chartDiario.setTitle("Distribución de Ingresos de Hoy");
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data(String.format("Inversión/Costo ($%.2f)", costo), costo),
                new PieChart.Data(String.format("Ganancia Neta ($%.2f)", beneficio), beneficio)
        );
        chartDiario.setData(pieChartData);
    }

    private void cargarReporteSemanal() {
        chartSemanal.setTitle("Desglose de Ventas (Últimos 7 Días)");

        // Creamos las dos capas de la barra apilada
        XYChart.Series<String, Number> serieCostos = new XYChart.Series<>();
        serieCostos.setName("Costo / Inversión");

        XYChart.Series<String, Number> serieBeneficios = new XYChart.Series<>();
        serieBeneficios.setName("Ganancia Neta");

        List<Map<String, Object>> listaDias = reporteDAO.obtenerVentasUltimos7Dias();
        for (Map<String, Object> dia : listaDias) {
            String fecha = (String) dia.get("fecha");
            serieCostos.getData().add(new XYChart.Data<>(fecha, (Number) dia.get("costos")));
            serieBeneficios.getData().add(new XYChart.Data<>(fecha, (Number) dia.get("beneficio")));
        }

        chartSemanal.getData().clear();
        chartSemanal.getData().addAll(serieCostos, serieBeneficios);

        agregarTooltipsABarras(serieCostos, "Costo");
        agregarTooltipsABarras(serieBeneficios, "Ganancia Neta");
    }

    private void cargarReporteMensual() {
        chartMensual.setTitle("Desglose del Mes Actual por Semanas");

        XYChart.Series<String, Number> serieCostos = new XYChart.Series<>();
        serieCostos.setName("Costo / Inversión");

        XYChart.Series<String, Number> serieBeneficios = new XYChart.Series<>();
        serieBeneficios.setName("Ganancia Neta");

        List<Map<String, Object>> listaSemanas = reporteDAO.obtenerVentasMensualesPorSemana();

        Map<String, Map<String, Object>> mapeoSemanas = new HashMap<>();
        for (Map<String, Object> sem : listaSemanas) {
            mapeoSemanas.put((String) sem.get("semana"), sem);
        }

        String[] ordenSemanas = {"Semana 1", "Semana 2", "Semana 3", "Semana 4"};
        for (String s : ordenSemanas) {
            double costo = 0.0;
            double beneficio = 0.0;
            if (mapeoSemanas.containsKey(s)) {
                costo = (double) mapeoSemanas.get(s).get("costos");
                beneficio = (double) mapeoSemanas.get(s).get("beneficio");
            }
            serieCostos.getData().add(new XYChart.Data<>(s, costo));
            serieBeneficios.getData().add(new XYChart.Data<>(s, beneficio));
        }

        chartMensual.getData().clear();
        chartMensual.getData().addAll(serieCostos, serieBeneficios);

        agregarTooltipsABarras(serieCostos, "Costo");
        agregarTooltipsABarras(serieBeneficios, "Ganancia Neta");
    }

    private void agregarTooltipsABarras(XYChart.Series<String, Number> serie, String tipoDato) {
        for (XYChart.Data<String, Number> data : serie.getData()) {
            javafx.scene.Node nodoBarra = data.getNode();

            if (nodoBarra != null) {
                double valor = data.getYValue().doubleValue();

                // Si el valor es cero, no es necesario interactuar con la barra
                if (valor == 0) continue;

                // Creamos el texto del Tooltip estilizado
                String mensaje = String.format("%s\nValor: $ %.2f", tipoDato, valor);
                Tooltip tooltip = new Tooltip(mensaje);

                // Tiempos de respuesta para que el hover se sienta instantáneo y fluido
                tooltip.setShowDelay(Duration.millis(50));
                tooltip.setHideDelay(Duration.millis(100));

                // Estilo visual del Tooltip (Alineado a fuentes limpias)
                tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                // Vinculamos de forma estática el Tooltip al nodo visual de la barra
                Tooltip.install(nodoBarra, tooltip);

                // Efecto visual extra: Cambiar la opacidad un poco al pasar el mouse para denotar selección
                nodoBarra.setOnMouseEntered(e -> nodoBarra.setOpacity(0.85));
                nodoBarra.setOnMouseExited(e -> nodoBarra.setOpacity(1.0));
            }
        }
    }

    public static class ResumenMes {
        private final String mes;
        private final int transacciones;
        private final double ingresos;
        private final double costos;
        private final double beneficio;

        public ResumenMes(String mes, int transacciones, double ingresos, double costos, double beneficio) {
            this.mes = mes;
            this.transacciones = transacciones;
            this.ingresos = ingresos;
            this.costos = costos;
            this.beneficio = beneficio;
        }

        public String getMes() { return mes; }
        public int getTransacciones() { return transacciones; }
        public double getIngresos() { return ingresos; }
        public double getCostos() { return costos; }
        public double getBeneficio() { return beneficio; }
    }
}
