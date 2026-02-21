package dao.impl;

import dao.interfaces.TeacherDAO;
import jakarta.persistence.TypedQuery;
import models.Module;
import models.Teacher;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class TeacherDAOImp extends GenericDAOImp<Teacher, Integer> implements TeacherDAO {

    public TeacherDAOImp() {
        super(Teacher.class);
    }

    @Override
    public Teacher findByUserId(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){

            TypedQuery<Teacher> query = session.createQuery(
                    "FROM Teacher WHERE user.id = :userId",
                    Teacher.class
            );
            query.setParameter("userId", userId);
            List<Teacher> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e){
            throw new RuntimeException("Error al buscar profesor por userId: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Teacher> findByUserIdOptional(Integer userId) {
        return Optional.ofNullable(findByUserId(userId));
    }

    @Override
    public List<Teacher> findByDepartment(String department) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Teacher> query = session.createQuery(
                    "FROM Teacher WHERE department = :dept",
                    Teacher.class
            );
            query.setParameter("dept", department);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar profesores por departamento: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Teacher> findBySpecialty(String specialty) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Teacher> query = session.createQuery(
                    "FROM Teacher WHERE specialty = :spec",
                    Teacher.class
            );
            query.setParameter("spec", specialty);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar profesores por especialidad: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Module> getModulesByTeacher(Integer teacherId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Module> query = session.createQuery(
                    "SELECT m FROM Module m JOIN m.teachers t WHERE t.id = :teacherId",
                    Module.class
            );
            query.setParameter("teacherId", teacherId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener módulos del profesor: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Teacher> findByModule(Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Teacher> query = session.createQuery(
                    "SELECT t FROM Teacher t JOIN t.modules m WHERE m.id = :moduleId",
                    Teacher.class
            );
            query.setParameter("moduleId", moduleId);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar profesores por módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean teachesModule(Integer teacherId, Integer moduleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(t) FROM Teacher t JOIN t.modules m WHERE t.id = :teacherId AND m.id = :moduleId",
                    Long.class
            );
            query.setParameter("teacherId", teacherId);
            query.setParameter("moduleId", moduleId);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar si profesor imparte módulo: " + e.getMessage(), e);
        }
    }

    @Override
    public void assignModule(Integer teacherId, Integer moduleId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.createNativeMutationQuery(
                            "INSERT INTO teacher_module (teacher_id, module_id) VALUES (:teacherId, :moduleId)"
                    )
                    .setParameter("teacherId", teacherId)
                    .setParameter("moduleId", moduleId)
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al asignar módulo a profesor: " + e.getMessage(), e);
        }
    }

    @Override
    public void unassignModule(Integer teacherId, Integer moduleId) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.createNativeMutationQuery(
                            "DELETE FROM teacher_module WHERE teacher_id = :teacherId AND module_id = :moduleId"
                    )
                    .setParameter("teacherId", teacherId)
                    .setParameter("moduleId", moduleId)
                    .executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al desasignar módulo de profesor: " + e.getMessage(), e);
        }
    }

    @Override
    public long countModulesByTeacher(Integer teacherId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(m) FROM Module m JOIN m.teachers t WHERE t.id = :teacherId",
                    Long.class
            );
            query.setParameter("teacherId", teacherId);
            return query.getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar módulos del profesor: " + e.getMessage(), e);
        }
    }
}