package dao.interfaces;
import models.User;
import models.UserType;
import java.util.List;
import java.util.Optional;

/**
 * DAO para la entidad User
 */
public interface UserDAO extends GenericDAO<User, Integer> {

    /**
     * Buscar usuario por nombre de usuario
     * @param username Nombre de usuario
     * @return Usuario encontrado o null
     */
    User findByUsername(String username);

    /**
     * Buscar usuario por nombre de usuario (con Optional)
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsernameOptional(String username);

    /**
     * Validar login de usuario
     * @param username Nombre de usuario
     * @param passwordHash Hash de la contraseña
     * @return Usuario si las credenciales son correctas, null si no
     */
    User validateLogin(String username, String passwordHash);

    /**
     * Buscar usuarios por tipo
     * @param userType Tipo de usuario (PROFESOR o ALUMNO)
     * @return Lista de usuarios del tipo especificado
     */
    List<User> findByUserType(UserType userType);

    /**
     * Verificar si existe un nombre de usuario
     * @param username Nombre de usuario a verificar
     * @return true si existe, false si no
     */
    boolean existsUsername(String username);

    /**
     * Buscar usuarios por nombre o apellido
     * @param searchTerm Término de búsqueda
     * @return Lista de usuarios que coinciden
     */
    List<User> searchByName(String searchTerm);

    /**
     * Cambiar contraseña de un usuario
     * @param userId ID del usuario
     * @param newPasswordHash Nuevo hash de contraseña
     */
    void changePassword(Integer userId, String newPasswordHash);
}