package dao.impl;

import dao.interfaces.UserDAO;
import models.User;
import models.UserType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import jakarta.persistence.TypedQuery;
import util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class UserDAOImp extends GenericDAOImp <User, Integer> implements UserDAO {

    public UserDAOImp() {
        super(User.class);
    }

    @Override
    public User findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<User> query = session.createQuery(
                    "FROM User WHERE username = :username",
                    User.class
            );
            query.setParameter("username", username);
            List<User> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuario por username: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByUsernameOptional(String username) {
        return Optional.ofNullable(findByUsername(username));
    }

    @Override
    public User validateLogin(String username, String passwordHash) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<User> query = session.createQuery(
                    "FROM User WHERE username = :username AND passwordHash = :passwordHash",
                    User.class
            );
            query.setParameter("username", username);
            query.setParameter("passwordHash", passwordHash);
            List<User> results = query.getResultList();
            return results.isEmpty() ? null : results.getFirst();
        } catch (Exception e) {
            throw new RuntimeException("Error al validar login: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> findByUserType(UserType userType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<User> query = session.createQuery(
                    "FROM User WHERE userType = :userType",
                    User.class
            );
            query.setParameter("userType", userType);
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuarios por tipo: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<Long> query = session.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.username = :username",
                    Long.class
            );
            query.setParameter("username", username);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar username: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> searchByName(String searchTerm) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            TypedQuery<User> query = session.createQuery(
                    "FROM User WHERE LOWER(firstName) LIKE LOWER(:term) OR LOWER(lastName) LIKE LOWER(:term)",
                    User.class
            );
            query.setParameter("term", "%" + searchTerm + "%");
            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuarios por nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public void changePassword(Integer userId, String newPasswordHash) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            User user = session.find(User.class, userId);
            if (user != null) {
                user.setPasswordHash(newPasswordHash);
                session.merge(user);
            } else {
                throw new RuntimeException("Usuario no encontrado con ID: " + userId);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al cambiar contraseña: " + e.getMessage(), e);
        }
    }
}
