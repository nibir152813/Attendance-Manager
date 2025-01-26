package attendance.manager;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author User
 */
public class LoginController implements Initializable {

    @FXML
    private Button login;
    @FXML
    private TextField user;
    @FXML
    private PasswordField pass;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
    }

    private void onLogin(ActionEvent event) {
    }

    private void loadMainView(ActionEvent event) {
        try {
            // Load the new FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Main View");
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Load Error", "Failed to load the Main View. Please try again.", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String header, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void OnLogin(ActionEvent event) {
        
        String username = user.getText().trim();
        String password = pass.getText().trim();

        if (username.equals("nibir") && password.equals("1234")) {
            loadMainView(event);
        } else {
            showAlert("Login Error", "Invalid Credentials", "The username or password you entered is incorrect. Please try again.", Alert.AlertType.ERROR);
        }
    }
}
