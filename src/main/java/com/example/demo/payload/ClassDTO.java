package com.example.demo.payload;

public class ClassDTO {
    private Long id;
    private String className;
    private String description;
    private Long teacherId;
    private String teacherName;
    private int studentCount;

    public ClassDTO(Long id, String className, String description, Long teacherId, String teacherName, int studentCount) {
        this.id = id;
        this.className = className;
        this.description = description;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.studentCount = studentCount;
    }

    public Long getId() { return id; }
    public String getClassName() { return className; }
    public String getDescription() { return description; }
    public Long getTeacherId() { return teacherId; }
    public String getTeacherName() { return teacherName; }
    public int getStudentCount() { return studentCount; }
}
