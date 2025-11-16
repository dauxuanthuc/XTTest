package com.example.demo.service;

import com.example.demo.model.Class;
import com.example.demo.model.Enrollment;
import com.example.demo.model.User;
import com.example.demo.repository.ClassRepository;
import com.example.demo.repository.EnrollmentRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    public ClassService(ClassRepository classRepository, EnrollmentRepository enrollmentRepository, UserRepository userRepository) {
        this.classRepository = classRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
    }

    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    public Class getClassById(Long id) {
        return classRepository.findById(id).orElse(null);
    }

    public Class createClass(String className, String description) {
        Class classEntity = new Class(className, description);
        return classRepository.save(classEntity);
    }

    public Class updateClass(Long id, String className, String description) {
        Class classEntity = classRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found"));
        classEntity.setClassName(className);
        classEntity.setDescription(description);
        return classRepository.save(classEntity);
    }

    public void deleteClass(Long id) {
        classRepository.deleteById(id);
    }

    public Class assignTeacher(Long classId, Long teacherId) {
        Class classEntity = classRepository.findById(classId).orElseThrow(() -> new RuntimeException("Class not found"));
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new RuntimeException("Teacher not found"));
        classEntity.setTeacher(teacher);
        return classRepository.save(classEntity);
    }

    public Enrollment addStudent(Long classId, Long studentId) {
        Class classEntity = classRepository.findById(classId).orElseThrow(() -> new RuntimeException("Class not found"));
        User student = userRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        Enrollment enrollment = new Enrollment(student, classEntity);
        return enrollmentRepository.save(enrollment);
    }

    public void removeStudent(Long classId, Long studentId) {
        enrollmentRepository.deleteByStudentIdAndClassEntityId(studentId, classId);
    }

    public List<User> getStudentsByClass(Long classId) {
        List<Enrollment> enrollments = enrollmentRepository.findByClassEntityId(classId);
        return enrollments.stream().map(Enrollment::getStudent).collect(Collectors.toList());
    }
}
