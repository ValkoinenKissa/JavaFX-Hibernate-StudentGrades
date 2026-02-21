package dao.impl;

import dao.interfaces.StudentDAO;
import jakarta.persistence.TypedQuery;
import models.Module;
import models.Student;
import models.StudentModule;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class StudentDAOImp extends GenericDAOImp<Student, Integer> implements StudentDAO {
    public StudentDAOImp() {
        super(Student.class);
    }

    @Override
    public Student findByUserId(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Student> query = session.createQuery(
                    "FROM Student WHERE user.id = :userId",
                    Student.class
            );
            query.setParameter("userId", userId);
            List<Student> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar estudiante por userId: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Student> findByUserIdOptional(Integer userId) {
        return Optional.ofNullable(findByUserId(userId));
    }

    @Override
    public List<Student> findByCourse(String course) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Student> query = session.createQuery(
                    "FROM Student WHERE course = :course",
                    Student.class
            );
            query.setParameter("course", course);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar estudiantes por curso: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> findByGradeGroup(String gradeGroup) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Student> query = session.createQuery(
                    "FROM Student WHERE gradeGroup = :gradeGroup",
                    Student.class
            );
            query.setParameter("gradeGroup", gradeGroup);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar estudiantes por grupo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> findByCourseAndGradeGroup(String course, String gradeGroup) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Student> query = session.createQuery(
                    "FROM Student WHERE course = :course AND gradeGroup = :gradeGroup",
                    Student.class
            );
            query.setParameter("course", course);
            query.setParameter("gradeGroup", gradeGroup);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar estudiantes por curso y grupo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Module> getModulesByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "SELECT sm.module FROM StudentModule sm WHERE sm.student.id = :studentId",
                    Module.class
            );
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener módulos del estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public List<StudentModule> getEnrollmentsByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<StudentModule> query = session.createQuery(
                    "FROM StudentModule WHERE student.id = :studentId",
                    StudentModule.class
            );
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener matrículas del estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> findByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Student> query = session.createQuery(
                    "SELECT sm.student FROM StudentModule sm WHERE sm.module.id = :moduleId",
                    Student.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar estudiantes por módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isEnrolledInModule(Integer studentId, Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(sm) FROM StudentModule sm WHERE sm.student.id = :studentId AND sm.module.id = :moduleId",
                    Long.class
            );
            query.setParameter("studentId", studentId);
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar la matrícula: " + e.getMessage(), e);
        }
    }

    @Override
    public long countModulesByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(sm) FROM StudentModule sm WHERE sm.student.id = :studentId",
                    Long.class
            );
            query.setParameter("studentId", studentId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar módulos del estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> findAllCourses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<String> query = session.createQuery(
                    "SELECT DISTINCT s.course FROM Student s ORDER BY s.course",
                    String.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener cursos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> findAllGradeGroups() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<String> query = session.createQuery(
                    "SELECT DISTINCT s.gradeGroup FROM Student s ORDER BY s.gradeGroup",
                    String.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener grupos: " + e.getMessage(), e);
        }
    }
}