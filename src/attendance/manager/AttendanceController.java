package attendance.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AttendanceController {
    @FXML private TableView<AttendanceRecord> tableView;
    @FXML private TableColumn<AttendanceRecord, Integer> colId;
    @FXML private TableColumn<AttendanceRecord, String> colTeacher;
    @FXML private TableColumn<AttendanceRecord, String> colCourse;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtTeacher;
    @FXML
    private TextField txtCourse;
    @FXML private Button btnAdd, btnUpdate, btnDelete;

    private Connection conn;
    private ObservableList<AttendanceRecord> data;

    public void initialize() {
        connectDB();
        setupTable();
        loadTable();
        tableView.setOnMouseClicked(this::onTableClick);
    }

    private void connectDB() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/attendance_db", "root", "");
            if (conn != null) {
                System.out.println("Connected to the database!");
            } else {
                showAlert("Error", "Failed to connect to the database!");
            }
        } catch (SQLException e) {
            showAlert("Error", "Database connection error: " + e.getMessage());
        }
    }

    private void setupTable() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colTeacher.setCellValueFactory(cellData -> cellData.getValue().teacherProperty());
        colCourse.setCellValueFactory(cellData -> cellData.getValue().courseProperty());
        data = FXCollections.observableArrayList();
        tableView.setItems(data);
    }

    private void loadTable() {
        data.clear();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM attendance")) {
            while (rs.next()) {
                data.add(new AttendanceRecord(rs.getInt("id"), rs.getString("teacher"), rs.getString("course_name")));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load data: " + e.getMessage());
        }
    }

    @FXML
    private void addRecord() {
        String idText = txtId.getText();
        String teacher = txtTeacher.getText();
        String course = txtCourse.getText();

        if (idText.isEmpty() || teacher.isEmpty() || course.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            showAlert("Error", "ID must be a number!");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO attendance (id, teacher, course_name) VALUES (?, ?, ?)")) {
            stmt.setInt(1, id);
            stmt.setString(2, teacher);
            stmt.setString(3, course);
            stmt.executeUpdate();
            loadTable();
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "ID already exists or invalid data: " + e.getMessage());
        }
    }

    @FXML
    private void updateRecord() {
        AttendanceRecord selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No record selected!");
            return;
        }

        String teacher = txtTeacher.getText();
        String course = txtCourse.getText();

        try (PreparedStatement stmt = conn.prepareStatement("UPDATE attendance SET teacher=?, course_name=? WHERE id=?")) {
            stmt.setString(1, teacher);
            stmt.setString(2, course);
            stmt.setInt(3, selected.getId());
            stmt.executeUpdate();
            loadTable();
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Failed to update record: " + e.getMessage());
        }
    }

    @FXML
    private void deleteRecord() {
        AttendanceRecord selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No record selected!");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to delete this record?");
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM attendance WHERE id=?")) {
                    stmt.setInt(1, selected.getId());
                    stmt.executeUpdate();
                    loadTable();
                    clearFields();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete record: " + e.getMessage());
                }
            }
        });
    }

    private void onTableClick(MouseEvent event) {
        AttendanceRecord selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            txtId.setText(String.valueOf(selected.getId()));
            txtTeacher.setText(selected.getTeacher());
            txtCourse.setText(selected.getCourse());
        }
    }

    private void clearFields() {
        txtId.clear();
        txtTeacher.clear();
        txtCourse.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to close the database connection: " + e.getMessage());
        }
    }

    @FXML
    private void onBackButton(ActionEvent event)  throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("MainView.fxml")); // Ensure correct FXML file

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow(); // Get current stage
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}