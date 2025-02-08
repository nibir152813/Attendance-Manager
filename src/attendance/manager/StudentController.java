package attendance.manager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentController {

    @FXML
    private TextField idField, nameField, courseField;

    @FXML
    private TableView<Student> studentsTable;

    @FXML
    private TableColumn<Student, Integer> idColumn;
    @FXML
    private TableColumn<Student, String> nameColumn, courseColumn;
    @FXML
    private TableColumn<Student, Integer> attendanceColumn;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();

    public void initialize() {
        idColumn.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        courseColumn.setCellValueFactory(data -> data.getValue().courseProperty());
        attendanceColumn.setCellValueFactory(data -> data.getValue().attendanceProperty().asObject());

        loadStudents();
    }

    public void loadStudents() {
        studentList.clear();
        try (Connection conn = DBUtils.getConnection()) {
            String query = "SELECT * FROM students";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                studentList.add(new Student(rs.getInt("id"), rs.getString("name"),
                        rs.getString("course"), rs.getInt("attendance")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        studentsTable.setItems(studentList);
    }

    @FXML
    public void addStudent() {
        String idText = idField.getText();
        String name = nameField.getText();
        String course = courseField.getText();

        if (idText.isEmpty() || name.isEmpty() || course.isEmpty()) {
            showAlert("Error", "Fields cannot be empty.");
            return;
        }

        try (Connection conn = DBUtils.getConnection()) {
            String query = "INSERT INTO students (id, name, course) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(idText));
            stmt.setString(2, name);
            stmt.setString(3, course);
            stmt.executeUpdate();
            loadStudents();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void updateStudent() {
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("Error", "No student selected.");
            return;
        }

        String name = nameField.getText();
        String course = courseField.getText();

        try (Connection conn = DBUtils.getConnection()) {
            String query = "UPDATE students SET name = ?, course = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setString(2, course);
            stmt.setInt(3, selectedStudent.getId());
            stmt.executeUpdate();
            loadStudents();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void addAttendance() {
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("Error", "No student selected.");
            return;
        }

        try (Connection conn = DBUtils.getConnection()) {
            String query = "UPDATE students SET attendance = attendance + 1 WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedStudent.getId());
            stmt.executeUpdate();
            loadStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void deleteStudent() {
        Student selectedStudent = studentsTable.getSelectionModel().getSelectedItem();
        if (selectedStudent == null) {
            showAlert("Error", "No student selected.");
            return;
        }

        try (Connection conn = DBUtils.getConnection()) {
            String query = "DELETE FROM students WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, selectedStudent.getId());
            stmt.executeUpdate();
            loadStudents();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        courseField.clear();
    }

    @FXML
    private void gototeachers(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Attendance.fxml")); // Ensure correct FXML file

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow(); // Get current stage
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
