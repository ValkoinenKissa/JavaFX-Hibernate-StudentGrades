package dao.interfaces;
import models.Teacher;
import models.Module;
import java.util.List;
import java.util.Optional;

/**
 * DAO para la entidad Teacher
 */
public interface TeacherDAO extends GenericDAO<Teacher, Integer> {

    /**
     * Buscar profesor por ID de usuario
     * @param userId ID del usuario
     * @return Profesor encontrado o null
     */
    Teacher findByUserId(Integer userId);

    /**
     * Buscar profesor por ID de usuario (con Optional)
     * @param userId ID del usuario
     * @return Optional con el profesor si existe
     */
    Optional<Teacher> findByUserIdOptional(Integer userId);

    /**
     * Buscar profesores por departamento
     * @param department Nombre del departamento
     * @return Lista de profesores del departamento
     */
    List<Teacher> findByDepartment(String department);

    /**
     * Buscar profesores por especialidad
     * @param specialty Especialidad
     * @return Lista de profesores con esa especialidad
     */
    List<Teacher> findBySpecialty(String specialty);

    /**
     * Obtener todos los módulos que imparte un profesor
     * @param teacherId ID del profesor
     * @return Lista de módulos
     */
    List<Module> getModulesByTeacher(Integer teacherId);

    /**
     * Buscar profesores que imparten un módulo específico
     * @param moduleId ID del módulo
     * @return Lista de profesores
     */
    List<Teacher> findByModule(Integer moduleId);

    /**
     * Verificar si un profesor imparte un módulo específico
     * @param teacherId ID del profesor
     * @param moduleId ID del módulo
     * @return true si el profesor imparte el módulo
     */
    boolean teachesModule(Integer teacherId, Integer moduleId);

    /**
     * Asignar un módulo a un profesor
     * @param teacherId ID del profesor
     * @param moduleId ID del módulo
     */
    void assignModule(Integer teacherId, Integer moduleId);

    /**
     * Desasignar un módulo de un profesor
     * @param teacherId ID del profesor
     * @param moduleId ID del módulo
     */
    void unassignModule(Integer teacherId, Integer moduleId);

    /**
     * Contar módulos de un profesor
     * @param teacherId ID del profesor
     * @return Número de módulos
     */
    long countModulesByTeacher(Integer teacherId);
}