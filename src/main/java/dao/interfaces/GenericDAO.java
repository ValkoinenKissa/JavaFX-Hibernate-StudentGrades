package dao.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones CRUD básicas
 * @param <T> Tipo de entidad
 * @param <ID> Tipo del identificador (Integer, Long, etc.)
 */
public interface GenericDAO<T, ID> {

    /**
     * Guardar una nueva entidad
     * @param entity Entidad a guardar
     */
    void save(T entity);

    /**
     * Actualizar una entidad existente
     * @param entity Entidad a actualizar
     */
    void update(T entity);

    /**
     * Guardar o actualizar (merge)
     * @param entity Entidad a guardar o actualizar
     */
    void saveOrUpdate(T entity);

    /**
     * Eliminar una entidad
     * @param entity Entidad a eliminar
     */
    void delete(T entity);

    /**
     * Eliminar por ID
     * @param id ID de la entidad a eliminar
     */
    void deleteById(ID id);

    /**
     * Buscar por ID
     * @param id ID de la entidad
     * @return Entidad encontrada o null
     */
    T findById(ID id);

    /**
     * Buscar por ID (con Optional)
     * @param id ID de la entidad
     * @return Optional con la entidad si existe
     */
    Optional<T> findByIdOptional(ID id);

    /**
     * Obtener todas las entidades
     * @return Lista de todas las entidades
     */
    List<T> findAll();

    /**
     * Contar total de registros
     * @return Total de registros
     */
    long count();

    /**
     * Verificar si existe una entidad por ID
     * @param id ID a verificar
     * @return true si existe, false si no
     */
    boolean existsById(ID id);
}