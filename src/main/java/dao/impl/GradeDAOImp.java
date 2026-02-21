package dao.impl;

import dao.interfaces.GradeDAO;
import jakarta.persistence.TypedQuery;
import models.Grade;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GradeDAOImp extends GenericDAOImp<Grade, Integer> implements GradeDAO {
    public GradeDAOImp() {
        super(Grade.class);
    }

    @Override
    public List<Grade> findByStudentModule(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade WHERE studentModule.id = :smId ORDER BY id DESC",
                    Grade.class
            );
            query.setParameter("smId", studentModuleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar notas por matrícula: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Grade> findByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade g WHERE g.studentModule.student.id = :studentId ORDER BY g.id DESC",
                    Grade.class
            );
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar notas por estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Grade> findByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade g WHERE g.studentModule.module.id = :moduleId ORDER BY g.id DESC",
                    Grade.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar notas por módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculateAverageGrade(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<BigDecimal> query = session.createQuery(
                    "SELECT AVG(g.grade) FROM Grade g WHERE g.studentModule.id = :smId",
                    BigDecimal.class
            );
            query.setParameter("smId", studentModuleId);
            BigDecimal average = query.getSingleResult();
            return average != null ? average : BigDecimal.ZERO;
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular la nota media: " + e.getMessage(), e);
        }
    }

    @Override
    public Grade findLatestGrade(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade WHERE studentModule.id = :smId ORDER BY id DESC",
                    Grade.class
            );
            query.setParameter("smId", studentModuleId);
            query.setMaxResults(1);
            List<Grade> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar última nota: " + e.getMessage(), e);
        }
    }

    @Override
    public Grade findHighestGrade(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade WHERE studentModule.id = :smId ORDER BY grade DESC",
                    Grade.class
            );
            query.setParameter("smId", studentModuleId);
            query.setMaxResults(1);
            List<Grade> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar nota más alta: " + e.getMessage(), e);
        }
    }

    @Override
    public Grade findLowestGrade(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade WHERE studentModule.id = :smId ORDER BY grade ASC",
                    Grade.class
            );
            query.setParameter("smId", studentModuleId);
            query.setMaxResults(1);
            List<Grade> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar nota más baja: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Grade> findByGradeRange(BigDecimal minGrade, BigDecimal maxGrade) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade WHERE grade BETWEEN :min AND :max ORDER BY grade DESC",
                    Grade.class
            );
            query.setParameter("min", minGrade);
            query.setParameter("max", maxGrade);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar notas por rango: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Grade> findByDateRange(LocalDate startDate, LocalDate endDate) {
        // Nota: La tabla grades no tiene campo fecha
        return List.of();
    }

    @Override
    public long countPassedByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(DISTINCT g.studentModule) FROM Grade g WHERE g.studentModule.module.id = :moduleId AND g.grade >= 5.0",
                    Long.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar aprobados: " + e.getMessage(), e);
        }
    }

    @Override
    public long countFailedByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(DISTINCT g.studentModule) FROM Grade g WHERE g.studentModule.module.id = :moduleId AND g.grade < 5.0",
                    Long.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar suspensos: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean hasPassed(Integer studentModuleId, BigDecimal passingGrade) {
        BigDecimal average = calculateAverageGrade(studentModuleId);
        return average.compareTo(passingGrade) >= 0;
    }

    @Override
    public List<Grade> findPassedGradesByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade g WHERE g.studentModule.module.id = :moduleId AND g.grade >= 5.0 ORDER BY g.grade DESC",
                    Grade.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar notas aprobadas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Grade> findFailedGradesByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade g WHERE g.studentModule.module.id = :moduleId AND g.grade < 5.0 ORDER BY g.grade ASC",
                    Grade.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar notas suspensas: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculateOverallAverageByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<BigDecimal> query = session.createQuery(
                    "SELECT AVG(g.grade) FROM Grade g WHERE g.studentModule.student.id = :studentId",
                    BigDecimal.class
            );
            query.setParameter("studentId", studentId);
            BigDecimal average = query.getSingleResult();
            return average != null ? average : BigDecimal.ZERO;
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular media general: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculateAverageGradeByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<BigDecimal> query = session.createQuery(
                    "SELECT AVG(g.grade) FROM Grade g WHERE g.studentModule.module.id = :moduleId",
                    BigDecimal.class
            );
            query.setParameter("moduleId", moduleId);
            BigDecimal average = query.getSingleResult();
            return average != null ? average : BigDecimal.ZERO;
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular media del módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public long countByStudentModule(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(g) FROM Grade g WHERE g.studentModule.id = :smId",
                    Long.class
            );
            query.setParameter("smId", studentModuleId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar notas: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByStudentModule(Integer studentModuleId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.createNativeMutationQuery("DELETE FROM grades WHERE id_student_module = :smId")
                    .setParameter("smId", studentModuleId)
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar notas: " + e.getMessage(), e);
        }
    }
}