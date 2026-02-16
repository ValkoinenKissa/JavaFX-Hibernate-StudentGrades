package dao.interfaces;
import models.StudentModule;
import models.Grade;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

/**
 * DAO para la entidad StudentModule (matrícula)
 */
public interface StudentModuleDAO extends GenericDAO<StudentModule, Integer> {

    /**
     * Buscar matrícula por alumno y módulo
     * @param studentId ID del alumno
     * @param moduleId ID del módulo
     * @return Matrícula encontrada o null
     */
    StudentModule findByStudentAndModule(Integer studentId, Integer moduleId);

    /**
     * Buscar matrícula por alumno y módulo (con Optional)
     * @param studentId ID del alumno
     * @param moduleId ID del módulo
     * @return Optional con la matrícula si existe
     */
    Optional<StudentModule> findByStudentAndModuleOptional(Integer studentId, Integer moduleId);

    /**
     * Obtener todas las matrículas de un alumno
     * @param studentId ID del alumno
     * @return Lista de matrículas
     */
    List<StudentModule> findByStudent(Integer studentId);

    /**
     * Obtener todas las matrículas de un módulo
     * @param moduleId ID del módulo
     * @return Lista de matrículas
     */
    List<StudentModule> findByModule(Integer moduleId);

    /**
     * Obtener notas de una matrícula específica
     * @param studentModuleId ID de la matrícula
     * @return Lista de notas
     */
    List<Grade> getGradesByEnrollment(Integer studentModuleId);

    /**
     * Verificar si existe una matrícula
     * @param studentId ID del alumno
     * @param moduleId ID del módulo
     * @return true si existe la matrícula
     */
    boolean existsEnrollment(Integer studentId, Integer moduleId);

    /**
     * Matricular alumno en módulo
     * @param studentId ID del alumno
     * @param moduleId ID del módulo
     * @return Matrícula creada
     */
    StudentModule enrollStudent(Integer studentId, Integer moduleId);

    /**
     * Desmatricular alumno de módulo
     * @param studentId ID del alumno
     * @param moduleId ID del módulo
     */
    void unenrollStudent(Integer studentId, Integer moduleId);

    /**
     * Obtener matrículas con sus notas
     * @param studentId ID del alumno
     * @return Lista de matrículas con notas cargadas
     */
    List<StudentModule> findByStudentWithGrades(Integer studentId);

    /**
     * Calcular nota media de una matrícula
     * @param studentModuleId ID de la matrícula
     * @return Nota media o null si no hay notas
     */
    BigDecimal calculateAverageGrade(Integer studentModuleId);

    /**
     * Obtener matrícula con todas sus notas
     * @param studentModuleId ID de la matrícula
     * @return Matrícula con notas cargadas
     */
    StudentModule findByIdWithGrades(Integer studentModuleId);

    /**
     * Contar matrículas de un alumno
     * @param studentId ID del alumno
     * @return Número de matrículas
     */
    long countByStudent(Integer studentId);

    /**
     * Contar matrículas de un módulo
     * @param moduleId ID del módulo
     * @return Número de matrículas
     */
    long countByModule(Integer moduleId);
}