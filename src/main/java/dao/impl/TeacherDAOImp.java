package dao.impl;

import dao.interfaces.TeacherDAO;
import models.Module;
import models.Teacher;

import java.util.List;
import java.util.Optional;

public class TeacherDAOImp extends GenericDAOImp<Teacher, Integer> implements TeacherDAO {

    public TeacherDAOImp() {
        super(Teacher.class);
    }

    @Override
    public Teacher findByUserId(Integer userId) {
        return null;
    }

    @Override
    public Optional<Teacher> findByUserIdOptional(Integer userId) {
        return Optional.empty();
    }

    @Override
    public List<Teacher> findByDepartment(String department) {
        return List.of();
    }

    @Override
    public List<Teacher> findBySpecialty(String specialty) {
        return List.of();
    }

    @Override
    public List<Module> getModulesByTeacher(Integer teacherId) {
        return List.of();
    }

    @Override
    public List<Teacher> findByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public boolean teachesModule(Integer teacherId, Integer moduleId) {
        return false;
    }

    @Override
    public void assignModule(Integer teacherId, Integer moduleId) {

    }

    @Override
    public void unassignModule(Integer teacherId, Integer moduleId) {

    }

    @Override
    public long countModulesByTeacher(Integer teacherId) {
        return 0;
    }
}
