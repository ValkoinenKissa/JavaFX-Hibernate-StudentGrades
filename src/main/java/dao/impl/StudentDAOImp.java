package dao.impl;

import dao.interfaces.StudentDAO;
import models.Module;
import models.Student;
import models.StudentModule;

import java.util.List;
import java.util.Optional;

public class StudentDAOImp extends GenericDAOImp <Student, Integer> implements StudentDAO {
    public StudentDAOImp() {
        super(Student.class);
    }


    @Override
    public Student findByUserId(Integer userId) {
        return null;
    }

    @Override
    public Optional<Student> findByUserIdOptional(Integer userId) {
        return Optional.empty();
    }

    @Override
    public List<Student> findByCourse(String course) {
        return List.of();
    }

    @Override
    public List<Student> findByGradeGroup(String gradeGroup) {
        return List.of();
    }

    @Override
    public List<Student> findByCourseAndGradeGroup(String course, String gradeGroup) {
        return List.of();
    }

    @Override
    public List<Module> getModulesByStudent(Integer studentId) {
        return List.of();
    }

    @Override
    public List<StudentModule> getEnrollmentsByStudent(Integer studentId) {
        return List.of();
    }

    @Override
    public List<Student> findByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public boolean isEnrolledInModule(Integer studentId, Integer moduleId) {
        return false;
    }

    @Override
    public long countModulesByStudent(Integer studentId) {
        return 0;
    }

    @Override
    public List<String> findAllCourses() {
        return List.of();
    }

    @Override
    public List<String> findAllGradeGroups() {
        return List.of();
    }
}
