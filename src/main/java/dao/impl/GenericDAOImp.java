package dao.impl;

import dao.interfaces.GenericDAO;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

/**
 * Implementación base de GenericDAO para Hibernate 6.x
 * Todos los DAOs específicos heredan de esta clase
 */
public abstract class GenericDAOImp<T, ID> implements GenericDAO<T, ID> {

    private final Class<T> entityClass;

    protected GenericDAOImp(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al guardar: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveOrUpdate(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(entity);  // ← merge hace save o update automáticamente
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al guardar/actualizar: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(ID id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T entity = session.find(entityClass, id);
            if (entity != null) {
                session.remove(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public T findById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(entityClass, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<T> findByIdOptional(ID id) {
        return Optional.ofNullable(findById(id));
    }

    @Override
    public List<T> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(entityClass);
            Root<T> root = cq.from(entityClass);
            cq.select(root);
            return session.createQuery(cq).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(entityClass);
            cq.select(cb.count(root));
            return session.createQuery(cq).getSingleResult();
        } catch (Exception e) {
            throw new RuntimeException("Error al contar: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(ID id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(entityClass);
            cq.select(cb.count(root));
            cq.where(cb.equal(root.get("id"), id));
            return session.createQuery(cq).getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar existencia: " + e.getMessage(), e);
        }
    }
}