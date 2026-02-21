package dao.impl;

import dao.interfaces.StudentModuleDAO;
import jakarta.persistence.TypedQuery;
import models.Grade;
import models.Student;
import models.Module;
import models.StudentModule;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class StudentModuleDAOImp extends GenericDAOImp<StudentModule, Integer> implements StudentModuleDAO {

    public StudentModuleDAOImp() {
        super(StudentModule.class);
    }

    @Override
    public StudentModule findByStudentAndModule(Integer studentId, Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<StudentModule> query = session.createQuery(
                    "FROM StudentModule WHERE student.id = :studentId AND module.id = :moduleId",
                    StudentModule.class
            );
            query.setParameter("studentId", studentId);
            query.setParameter("moduleId", moduleId);
            List<StudentModule> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar matrícula: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<StudentModule> findByStudentAndModuleOptional(Integer studentId, Integer moduleId) {
        return Optional.ofNullable(findByStudentAndModule(studentId, moduleId));
    }

    @Override
    public List<StudentModule> findByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<StudentModule> query = session.createQuery(
                    "FROM StudentModule WHERE student.id = :studentId",
                    StudentModule.class
            );
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar matrículas por estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentModule> findByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<StudentModule> query = session.createQuery(
                    "FROM StudentModule WHERE module.id = :moduleId",
                    StudentModule.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar matrículas por módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Grade> getGradesByEnrollment(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Grade> query = session.createQuery(
                    "FROM Grade WHERE studentModule.id = :smId ORDER BY id DESC",
                    Grade.class
            );
            query.setParameter("smId", studentModuleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener notas: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsEnrollment(Integer studentId, Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(sm) FROM StudentModule sm WHERE sm.student.id = :studentId AND sm.module.id = :moduleId",
                    Long.class
            );
            query.setParameter("studentId", studentId);
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar matrícula: " + e.getMessage(), e);
        }
    }

    @Override
    public StudentModule enrollStudent(Integer studentId, Integer moduleId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Student student = session.find(Student.class, studentId);
            Module module = session.find(Module.class, moduleId);

            if (student == null) {
                throw new RuntimeException("Estudiante no encontrado con ID: " + studentId);
            }
            if (module == null) {
                throw new RuntimeException("Módulo no encontrado con ID: " + moduleId);
            }

            StudentModule enrollment = new StudentModule(student, module);
            session.persist(enrollment);

            transaction.commit();
            return enrollment;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al matricular estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public void unenrollStudent(Integer studentId, Integer moduleId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            StudentModule enrollment = findByStudentAndModule(studentId, moduleId);
            if (enrollment != null) {
                session.remove(enrollment);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al desmatricular estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentModule> findByStudentWithGrades(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<StudentModule> query = session.createQuery(
                    "SELECT DISTINCT sm FROM StudentModule sm LEFT JOIN FETCH sm.grades WHERE sm.student.id = :studentId",
                    StudentModule.class
            );
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar matrículas con notas: " + e.getMessage(), e);
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
            throw new RuntimeException("Error al calcular nota media: " + e.getMessage(), e);
        }
    }

    @Override
    public StudentModule findByIdWithGrades(Integer studentModuleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<StudentModule> query = session.createQuery(
                    "SELECT DISTINCT sm FROM StudentModule sm LEFT JOIN FETCH sm.grades WHERE sm.id = :id",
                    StudentModule.class
            );
            query.setParameter("id", studentModuleId);
            List<StudentModule> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar matrícula con notas: " + e.getMessage(), e);
        }
    }

    @Override
    public long countByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(sm) FROM StudentModule sm WHERE sm.student.id = :studentId",
                    Long.class
            );
            query.setParameter("studentId", studentId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar matrículas: " + e.getMessage(), e);
        }
    }

    @Override
    public long countByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(sm) FROM StudentModule sm WHERE sm.module.id = :moduleId",
                    Long.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar matrículas: " + e.getMessage(), e);
        }
    }
}