package com.example.demo.controller;

import com.example.demo.model.Class;
import com.example.demo.model.Enrollment;
import com.example.demo.model.User;
import com.example.demo.payload.ClassCreateRequest;
import com.example.demo.payload.ClassDTO;
import com.example.demo.payload.EnrollmentRequest;
import com.example.demo.payload.MessageResponse;
import com.example.demo.payload.UserDTO;
import com.example.demo.service.ClassService;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/classes")
public class ClassController {

    private final ClassService classService;
    private final UserService userService;

    public ClassController(ClassService classService, UserService userService) {
        this.classService = classService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllClasses() {
        List<Class> classes = classService.getAllClasses();
        List<ClassDTO> classDTOs = classes.stream()
                .map(c -> new ClassDTO(
                        c.getId(),
                        c.getClassName(),
                        c.getDescription(),
                        c.getTeacher() != null ? c.getTeacher().getId() : null,
                        c.getTeacher() != null ? c.getTeacher().getUsername() : "Chưa có GV",
                        classService.getStudentsByClass(c.getId()).size()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(classDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        Class classEntity = classService.getClassById(id);
        if (classEntity == null) return ResponseEntity.notFound().build();
        ClassDTO classDTO = new ClassDTO(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getDescription(),
                classEntity.getTeacher() != null ? classEntity.getTeacher().getId() : null,
                classEntity.getTeacher() != null ? classEntity.getTeacher().getUsername() : "Chưa có GV",
                classService.getStudentsByClass(classEntity.getId()).size()
        );
        return ResponseEntity.ok(classDTO);
    }

    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody ClassCreateRequest req) {
        Class classEntity = classService.createClass(req.getClassName(), req.getDescription());
        ClassDTO classDTO = new ClassDTO(classEntity.getId(), classEntity.getClassName(), classEntity.getDescription(), null, "Chưa có GV", 0);
        return ResponseEntity.ok(classDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClass(@PathVariable Long id, @RequestBody ClassCreateRequest req) {
        Class classEntity = classService.updateClass(id, req.getClassName(), req.getDescription());
        ClassDTO classDTO = new ClassDTO(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getDescription(),
                classEntity.getTeacher() != null ? classEntity.getTeacher().getId() : null,
                classEntity.getTeacher() != null ? classEntity.getTeacher().getUsername() : "Chưa có GV",
                classService.getStudentsByClass(classEntity.getId()).size()
        );
        return ResponseEntity.ok(classDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(new MessageResponse("Lớp học đã được xóa"));
    }

    @PostMapping("/{id}/teacher/{teacherId}")
    public ResponseEntity<?> assignTeacher(@PathVariable Long id, @PathVariable Long teacherId) {
        Class classEntity = classService.assignTeacher(id, teacherId);
        ClassDTO classDTO = new ClassDTO(
                classEntity.getId(),
                classEntity.getClassName(),
                classEntity.getDescription(),
                classEntity.getTeacher() != null ? classEntity.getTeacher().getId() : null,
                classEntity.getTeacher() != null ? classEntity.getTeacher().getUsername() : "Chưa có GV",
                classService.getStudentsByClass(classEntity.getId()).size()
        );
        return ResponseEntity.ok(classDTO);
    }

    @PostMapping("/{id}/students")
    public ResponseEntity<?> addStudent(@PathVariable Long id, @RequestBody EnrollmentRequest req) {
        Enrollment enrollment = classService.addStudent(id, req.getStudentId());
        return ResponseEntity.ok(new MessageResponse("Sinh viên đã được thêm vào lớp"));
    }

    @DeleteMapping("/{id}/students/{studentId}")
    public ResponseEntity<?> removeStudent(@PathVariable Long id, @PathVariable Long studentId) {
        classService.removeStudent(id, studentId);
        return ResponseEntity.ok(new MessageResponse("Sinh viên đã được xóa khỏi lớp"));
    }

    @GetMapping("/{id}/students")
    public ResponseEntity<?> getStudentsByClass(@PathVariable Long id) {
        List<User> students = classService.getStudentsByClass(id);
        List<UserDTO> studentDTOs = students.stream()
                .map(s -> new UserDTO(s.getId(), s.getUsername(),
                        s.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }
}
