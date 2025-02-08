package attendance.manager;

import javafx.beans.property.*;

public class AttendanceRecord {
    private final IntegerProperty id;
    private final StringProperty teacher;
    private final StringProperty course;

    public AttendanceRecord(int id, String teacher, String course) {
        this.id = new SimpleIntegerProperty(id);
        this.teacher = new SimpleStringProperty(teacher);
        this.course = new SimpleStringProperty(course);
    }

    public int getId() { return id.get(); }
    public String getTeacher() { return teacher.get(); }
    public String getCourse() { return course.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty teacherProperty() { return teacher; }
    public StringProperty courseProperty() { return course; }
}