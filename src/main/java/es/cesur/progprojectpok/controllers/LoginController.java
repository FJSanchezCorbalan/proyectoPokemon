package es.cesur.progprojectpok.controllers;

import es.cesur.progprojectpok.database.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField invalidCredential;

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Realizar la conexión con la base de datos
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM entrenador WHERE NOM_ENTRENADOR = ? AND PASS = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Si el usuario y la contraseña son válidos, cerrar la ventana de inicio de sesión
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();

                // Ahora cargar la nueva vista del menú principal
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/es/cesur/progprojectpok/view/menu-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 639, 645);
                Stage menuStage = new Stage();
                menuStage.setTitle("Menu");
                menuStage.setScene(scene);
                menuStage.show();
            } else {
                //Si las credenciales son incorrectas, mostrar un mensaje de error
                invalidCredential.appendText("Credenciales incorrectas. \n");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
