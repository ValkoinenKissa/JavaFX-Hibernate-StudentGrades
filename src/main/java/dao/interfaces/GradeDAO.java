package dao.interfaces;
import models.Grade;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DAO para la entidad Grade (notas)
 */
public interface GradeDAO extends GenericDAO<Grade, Integer> {

    /**
     * Obtener todas las notas de una matrícula (alumno en módulo)
     * @param studentModuleId ID de la matrícula
     * @return Lista de notas ordenadas por fecha
     */
    List<Grade> findByStudentModule(Integer studentModuleId);

    /**
     * Obtener todas las notas de un alumno (en todos sus módulos)
     * @param studentId ID del alumno
     * @return Lista de notas
     */
    List<Grade> findByStudent(Integer studentId);

    /**
     * Obtener todas las notas de un módulo (todos los alumnos)
     * @param moduleId ID del módulo
     * @return Lista de notas
     */
    List<Grade> findByModule(Integer moduleId);

    /**
     * Calcular nota media de un alumno en un módulo
     * @param studentModuleId ID de la matrícula
     * @return Nota media (BigDecimal) o null si no hay notas
     */
    BigDecimal calculateAverageGrade(Integer studentModuleId);

    /**
     * Obtener la última nota registrada de una matrícula
     * @param studentModuleId ID de la matrícula
     * @return Última nota o null
     */
    Grade findLatestGrade(Integer studentModuleId);

    /**
     * Obtener la nota más alta de una matrícula
     * @param studentModuleId ID de la matrícula
     * @return Nota más alta o null
     */
    Grade findHighestGrade(Integer studentModuleId);

    /**
     * Obtener la nota más baja de una matrícula
     * @param studentModuleId ID de la matrícula
     * @return Nota más baja o null
     */
    Grade findLowestGrade(Integer studentModuleId);

    /**
     * Buscar notas por rango de calificación
     * @param minGrade Nota mínima
     * @param maxGrade Nota máxima
     * @return Lista de notas en ese rango
     */
    List<Grade> findByGradeRange(BigDecimal minGrade, BigDecimal maxGrade);

    /**
     * Buscar notas por rango de fechas
     * @param startDate Fecha inicial
     * @param endDate Fecha final
     * @return Lista de notas en ese rango
     */
    List<Grade> findByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Contar aprobados en un módulo (nota >= 5.0)
     * @param moduleId ID del módulo
     * @return Número de aprobados
     */
    long countPassedByModule(Integer moduleId);

    /**
     * Contar suspensos en un módulo (nota < 5.0)
     * @param moduleId ID del módulo
     * @return Número de suspensos
     */
    long countFailedByModule(Integer moduleId);

    /**
     * Verificar si un alumno ha aprobado un módulo
     * @param studentModuleId ID de la matrícula
     * @param passingGrade Nota mínima para aprobar (ej: 5.0)
     * @return true si la nota media es >= passingGrade
     */
    boolean hasPassed(Integer studentModuleId, BigDecimal passingGrade);

    /**
     * Obtener notas aprobadas de un módulo
     * @param moduleId ID del módulo
     * @return Lista de notas >= 5.0
     */
    List<Grade> findPassedGradesByModule(Integer moduleId);

    /**
     * Obtener notas suspensas de un módulo
     * @param moduleId ID del módulo
     * @return Lista de notas < 5.0
     */
    List<Grade> findFailedGradesByModule(Integer moduleId);

    /**
     * Calcular nota media general de un alumno (todos sus módulos)
     * @param studentId ID del alumno
     * @return Nota media general
     */
    BigDecimal calculateOverallAverageByStudent(Integer studentId);

    /**
     * Calcular nota media general de un módulo (todos los alumnos)
     * @param moduleId ID del módulo
     * @return Nota media general del módulo
     */
    BigDecimal calculateAverageGradeByModule(Integer moduleId);

    /**
     * Contar notas de una matrícula
     * @param studentModuleId ID de la matrícula
     * @return Número de notas registradas
     */
    long countByStudentModule(Integer studentModuleId);

    /**
     * Eliminar todas las notas de una matrícula
     * @param studentModuleId ID de la matrícula
     */
    void deleteByStudentModule(Integer studentModuleId);
}