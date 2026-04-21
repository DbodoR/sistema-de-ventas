package org.dbodor.sistemadeventas.Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.dbodor.sistemadeventas.DAO.UsuariosDAO;
import org.dbodor.sistemadeventas.Util.DatabaseConnection;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class LoginController {

    @FXML
    private ProgressBar barraProgreso;

    @FXML
    private Button btnIniciar;

    @FXML
    private Label textoStatus;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usuarioField;

    @FXML
    void onClick(ActionEvent event) {
        btnIniciar.setDisable(true);
        usuarioField.setDisable(true);
        passwordField.setDisable(true);
        textoStatus.setText("Iniciando...");
        textoStatus.setStyle("-fx-text-fill: black;");

        barraProgreso.setVisible(true);
        barraProgreso.setProgress(0);

        Task<Boolean> loginTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {

                updateMessage("Conectando a la base de datos...");
                for (int i = 0; i <= 70; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(10);
                }
                DatabaseConnection.testConnection();

                // 2. Validación de usuario (70% -> 100%)
                updateMessage("Validando credenciales...");
                UsuariosDAO usuariosDAO = new UsuariosDAO();
                boolean esValido = usuariosDAO.validarUsuario(usuarioField.getText(), passwordField.getText());

                for (int i = 71; i <= 100; i++) {
                    updateProgress(i, 100);
                    Thread.sleep(10);
                }

                return esValido;
            }
        };

        // Vinculamos propiedades de la UI a la tarea
        barraProgreso.progressProperty().bind(loginTask.progressProperty());
        textoStatus.textProperty().bind(loginTask.messageProperty());

        loginTask.setOnSucceeded(e -> {
            boolean esValido = loginTask.getValue();
            textoStatus.textProperty().unbind(); // Desvinculamos para poner nuestro propio mensaje final
            if (esValido) {
                textoStatus.setText("¡Acceso concedido!");
                textoStatus.setStyle("-fx-text-fill: green;");

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) btnIniciar.getScene().getWindow();

                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

                    stage.setTitle("Jean Pier Agro's");
                    stage.setScene(scene);
                    stage.setResizable(true);
                    stage.setMaximized(true);
                } catch (IOException ex) {
                    System.out.println("Error al cargar el Dashboard: " + ex.getMessage());
                    ex.printStackTrace();
                }


            } else {
                textoStatus.setText("Usuario o contraseña incorrectos.");
                textoStatus.setStyle("-fx-text-fill: red;");
            }
            btnIniciar.setDisable(false);
            usuarioField.setDisable(false);
            passwordField.setDisable(false);
            barraProgreso.progressProperty().unbind();
            barraProgreso.setVisible(false);
        });

        loginTask.setOnFailed(e -> {
            textoStatus.textProperty().unbind(); // Desvinculamos para poner nuestro propio mensaje final
            textoStatus.setText("Error de conexión. Intente de nuevo.");
            textoStatus.setStyle("-fx-text-fill: red;");
            btnIniciar.setDisable(false);
            usuarioField.setDisable(false);
            passwordField.setDisable(false);
            barraProgreso.progressProperty().unbind();
            barraProgreso.setProgress(0);
        });

        new Thread(loginTask).start();
    }
}
