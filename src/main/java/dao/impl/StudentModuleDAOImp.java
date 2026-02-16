package dao.impl;

import dao.interfaces.StudentModuleDAO;
import models.Grade;
import models.Student;
import models.StudentModule;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class StudentModuleDAOImp extends GenericDAOImp <StudentModule, Integer> implements StudentModuleDAO {

    public StudentModuleDAOImp() {
        super(StudentModule.class);
    }

    @Override
    public StudentModule findByStudentAndModule(Integer studentId, Integer moduleId) {
        return null;
    }

    @Override
    public Optional<StudentModule> findByStudentAndModuleOptional(Integer studentId, Integer moduleId) {
        return Optional.empty();
    }

    @Override
    public List<StudentModule> findByStudent(Integer studentId) {
        return List.of();
    }

    @Override
    public List<StudentModule> findByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public List<Grade> getGradesByEnrollment(Integer studentModuleId) {
        return List.of();
    }

    @Override
    public boolean existsEnrollment(Integer studentId, Integer moduleId) {
        return false;
    }

    @Override
    public StudentModule enrollStudent(Integer studentId, Integer moduleId) {
        return null;
    }

    @Override
    public void unenrollStudent(Integer studentId, Integer moduleId) {

    }

    @Override
    public List<StudentModule> findByStudentWithGrades(Integer studentId) {
        return List.of();
    }

    @Override
    public BigDecimal calculateAverageGrade(Integer studentModuleId) {
        return null;
    }

    @Override
    public StudentModule findByIdWithGrades(Integer studentModuleId) {
        return null;
    }

    @Override
    public long countByStudent(Integer studentId) {
        return 0;
    }

    @Override
    public long countByModule(Integer moduleId) {
        return 0;
    }
}
