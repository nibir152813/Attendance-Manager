package attendance.manager;

import javafx.beans.property.*;

public class Student {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty course;
    private final IntegerProperty attendance;

    public Student(int id, String name, String course, int attendance) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.course = new SimpleStringProperty(course);
        this.attendance = new SimpleIntegerProperty(attendance);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getCourse() {
        return course.get();
    }

    public StringProperty courseProperty() {
        return course;
    }

    public int getAttendance() {
        return attendance.get();
    }

    public IntegerProperty attendanceProperty() {
        return attendance;
    }
}



