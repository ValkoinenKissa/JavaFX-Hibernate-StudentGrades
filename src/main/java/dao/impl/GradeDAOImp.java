package dao.impl;

import dao.interfaces.GradeDAO;
import models.Grade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GradeDAOImp extends GenericDAOImp<Grade, Integer> implements GradeDAO {
    public GradeDAOImp() {
        super(Grade.class);
    }

    @Override
    public List<Grade> findByStudentModule(Integer studentModuleId) {
        return List.of();
    }

    @Override
    public List<Grade> findByStudent(Integer studentId) {
        return List.of();
    }

    @Override
    public List<Grade> findByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public BigDecimal calculateAverageGrade(Integer studentModuleId) {
        return null;
    }

    @Override
    public Grade findLatestGrade(Integer studentModuleId) {
        return null;
    }

    @Override
    public Grade findHighestGrade(Integer studentModuleId) {
        return null;
    }

    @Override
    public Grade findLowestGrade(Integer studentModuleId) {
        return null;
    }

    @Override
    public List<Grade> findByGradeRange(BigDecimal minGrade, BigDecimal maxGrade) {
        return List.of();
    }

    @Override
    public List<Grade> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public long countPassedByModule(Integer moduleId) {
        return 0;
    }

    @Override
    public long countFailedByModule(Integer moduleId) {
        return 0;
    }

    @Override
    public boolean hasPassed(Integer studentModuleId, BigDecimal passingGrade) {
        return false;
    }

    @Override
    public List<Grade> findPassedGradesByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public List<Grade> findFailedGradesByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public BigDecimal calculateOverallAverageByStudent(Integer studentId) {
        return null;
    }

    @Override
    public BigDecimal calculateAverageGradeByModule(Integer moduleId) {
        return null;
    }

    @Override
    public long countByStudentModule(Integer studentModuleId) {
        return 0;
    }

    @Override
    public void deleteByStudentModule(Integer studentModuleId) {

    }
}
