package dao.interfaces;
import models.Module;
import models.Teacher;
import models.Student;
import java.util.List;

/**
 * DAO para la entidad Module
 */
public interface ModuleDAO extends GenericDAO<Module, Integer> {

    /**
     * Buscar módulos por nombre
     * @param moduleName Nombre del módulo (búsqueda parcial)
     * @return Lista de módulos que coinciden
     */
    List<Module> findByModuleName(String moduleName);

    /**
     * Buscar módulos por nombre exacto
     * @param moduleName Nombre exacto del módulo
     * @return Módulo encontrado o null
     */
    Module findByModuleNameExact(String moduleName);

    /**
     * Buscar módulos por curso
     * @param course Nombre del curso
     * @return Lista de módulos del curso
     */
    List<Module> findByCourse(String course);

    /**
     * Obtener profesores asignados a un módulo
     * @param moduleId ID del módulo
     * @return Lista de profesores
     */
    List<Teacher> getTeachersByModule(Integer moduleId);

    /**
     * Obtener alumnos matriculados en un módulo
     * @param moduleId ID del módulo
     * @return Lista de alumnos
     */
    List<Student> getStudentsByModule(Integer moduleId);

    /**
     * Contar alumnos matriculados en un módulo
     * @param moduleId ID del módulo
     * @return Número de alumnos
     */
    long countStudentsByModule(Integer moduleId);

    /**
     * Contar profesores de un módulo
     * @param moduleId ID del módulo
     * @return Número de profesores
     */
    long countTeachersByModule(Integer moduleId);

    /**
     * Buscar módulos que imparte un profesor específico
     * @param teacherId ID del profesor
     * @return Lista de módulos
     */
    List<Module> findByTeacher(Integer teacherId);

    /**
     * Buscar módulos en los que está matriculado un alumno
     * @param studentId ID del alumno
     * @return Lista de módulos
     */
    List<Module> findByStudent(Integer studentId);

    /**
     * Buscar módulos por rango de horas semanales
     * @param minHours Horas mínimas
     * @param maxHours Horas máximas
     * @return Lista de módulos
     */
    List<Module> findBySemanalHoursRange(Integer minHours, Integer maxHours);

    /**
     * Obtener todos los cursos únicos
     * @return Lista de nombres de cursos
     */
    List<String> findAllCourses();

    /**
     * Verificar si existe un módulo con ese nombre
     * @param moduleName Nombre del módulo
     * @return true si existe
     */
    boolean existsByModuleName(String moduleName);
}
