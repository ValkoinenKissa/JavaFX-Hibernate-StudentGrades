package dao.impl;

import dao.interfaces.ModuleDAO;
import jakarta.persistence.TypedQuery;
import models.Module;
import models.Student;
import models.Teacher;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;

public class ModuleDAOImp extends GenericDAOImp<Module, Integer> implements ModuleDAO {
    public ModuleDAOImp() {
        super(Module.class);
    }

    @Override
    public List<Module> findByModuleName(String moduleName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "FROM Module WHERE LOWER(moduleName) LIKE LOWER(:name)",
                    Module.class
            );
            query.setParameter("name", "%" + moduleName + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar módulos por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public Module findByModuleNameExact(String moduleName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "FROM Module WHERE moduleName = :name",
                    Module.class
            );
            query.setParameter("name", moduleName);
            List<Module> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar módulo por nombre exacto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Module> findByCourse(String course) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "FROM Module WHERE course = :course",
                    Module.class
            );
            query.setParameter("course", course);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar módulos por curso: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Teacher> getTeachersByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Teacher> query = session.createQuery(
                    "SELECT t FROM Teacher t JOIN t.modules m WHERE m.id = :moduleId",
                    Teacher.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener profesores del módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Student> getStudentsByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Student> query = session.createQuery(
                    "SELECT sm.student FROM StudentModule sm WHERE sm.module.id = :moduleId",
                    Student.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estudiantes del módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public long countStudentsByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(sm) FROM StudentModule sm WHERE sm.module.id = :moduleId",
                    Long.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar estudiantes del módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public long countTeachersByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(t) FROM Teacher t JOIN t.modules m WHERE m.id = :moduleId",
                    Long.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar profesores del módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Module> findByTeacher(Integer teacherId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "SELECT m FROM Module m JOIN m.teachers t WHERE t.id = :teacherId",
                    Module.class
            );
            query.setParameter("teacherId", teacherId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar módulos por profesor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Module> findByStudent(Integer studentId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "SELECT sm.module FROM StudentModule sm WHERE sm.student.id = :studentId",
                    Module.class
            );
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar módulos por estudiante: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Module> findBySemanalHoursRange(Integer minHours, Integer maxHours) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "FROM Module WHERE semanalHours BETWEEN :min AND :max",
                    Module.class
            );
            query.setParameter("min", minHours);
            query.setParameter("max", maxHours);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar módulos por horas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> findAllCourses() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<String> query = session.createQuery(
                    "SELECT DISTINCT m.course FROM Module m ORDER BY m.course",
                    String.class
            );
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener cursos: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByModuleName(String moduleName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(m) FROM Module m WHERE m.moduleName = :name",
                    Long.class
            );
            query.setParameter("name", moduleName);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar módulo: " + e.getMessage(), e);
        }
    }
}