package dao.interfaces;
import models.Student;
import models.Module;
import models.StudentModule;
import java.util.List;
import java.util.Optional;

/**
 * DAO para la entidad Student
 */
public interface StudentDAO extends GenericDAO<Student, Integer> {

    /**
     * Buscar alumno por ID de usuario
     * @param userId ID del usuario
     * @return Alumno encontrado o null
     */
    Student findByUserId(Integer userId);

    /**
     * Buscar alumno por ID de usuario (con Optional)
     * @param userId ID del usuario
     * @return Optional con el alumno si existe
     */
    Optional<Student> findByUserIdOptional(Integer userId);

    /**
     * Buscar alumnos por curso
     * @param course Nombre del curso (ej: "2º DAM")
     * @return Lista de alumnos del curso
     */
    List<Student> findByCourse(String course);

    /**
     * Buscar alumnos por grupo
     * @param gradeGroup Grupo (ej: "A", "B")
     * @return Lista de alumnos del grupo
     */
    List<Student> findByGradeGroup(String gradeGroup);

    /**
     * Buscar alumnos por curso y grupo
     * @param course Curso
     * @param gradeGroup Grupo
     * @return Lista de alumnos
     */
    List<Student> findByCourseAndGradeGroup(String course, String gradeGroup);

    /**
     * Obtener módulos en los que está matriculado un alumno
     * @param studentId ID del alumno
     * @return Lista de módulos
     */
    List<Module> getModulesByStudent(Integer studentId);

    /**
     * Obtener matrículas de un alumno
     * @param studentId ID del alumno
     * @return Lista de matrículas (StudentModule)
     */
    List<StudentModule> getEnrollmentsByStudent(Integer studentId);

    /**
     * Buscar alumnos matriculados en un módulo específico
     * @param moduleId ID del módulo
     * @return Lista de alumnos
     */
    List<Student> findByModule(Integer moduleId);

    /**
     * Verificar si un alumno está matriculado en un módulo
     * @param studentId ID del alumno
     * @param moduleId ID del módulo
     * @return true si está matriculado
     */
    boolean isEnrolledInModule(Integer studentId, Integer moduleId);

    /**
     * Contar módulos de un alumno
     * @param studentId ID del alumno
     * @return Número de módulos matriculados
     */
    long countModulesByStudent(Integer studentId);

    /**
     * Obtener todos los cursos únicos
     * @return Lista de nombres de cursos
     */
    List<String> findAllCourses();

    /**
     * Obtener todos los grupos únicos
     * @return Lista de grupos
     */
    List<String> findAllGradeGroups();
}