package attendance.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import java.sql.*;

public class AttendanceController {
    @FXML private TableView<AttendanceRecord> tableView;
    @FXML private TableColumn<AttendanceRecord, Integer> colId;
    @FXML private TableColumn<AttendanceRecord, String> colTeacher;
    @FXML private TableColumn<AttendanceRecord, String> colCourse;
    @FXML private TextField txtId, txtTeacher, txtCourse;
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
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            showAlert("Error", "ID already exists or invalid data!");
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
            showAlert("Error", "Failed to update record!");
        }
    }

    @FXML
    private void deleteRecord() {
        AttendanceRecord selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "No record selected!");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM attendance WHERE id=?")) {
            stmt.setInt(1, selected.getId());
            stmt.executeUpdate();
            loadTable();
            clearFields();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete record!");
        }
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
}

