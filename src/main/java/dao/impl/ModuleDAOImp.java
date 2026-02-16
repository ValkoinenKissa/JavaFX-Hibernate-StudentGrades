package dao.impl;

import dao.interfaces.ModuleDAO;
import models.Module;
import models.Student;
import models.Teacher;

import java.util.List;

public class ModuleDAOImp extends GenericDAOImp<Module, Integer> implements ModuleDAO {
    public ModuleDAOImp() {
        super(Module.class);
    }

    @Override
    public List<Module> findByModuleName(String moduleName) {
        return List.of();
    }

    @Override
    public Module findByModuleNameExact(String moduleName) {
        return null;
    }

    @Override
    public List<Module> findByCourse(String course) {
        return List.of();
    }

    @Override
    public List<Teacher> getTeachersByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public List<Student> getStudentsByModule(Integer moduleId) {
        return List.of();
    }

    @Override
    public long countStudentsByModule(Integer moduleId) {
        return 0;
    }

    @Override
    public long countTeachersByModule(Integer moduleId) {
        return 0;
    }

    @Override
    public List<Module> findByTeacher(Integer teacherId) {
        return List.of();
    }

    @Override
    public List<Module> findByStudent(Integer studentId) {
        return List.of();
    }

    @Override
    public List<Module> findBySemanalHoursRange(Integer minHours, Integer maxHours) {
        return List.of();
    }

    @Override
    public List<String> findAllCourses() {
        return List.of();
    }

    @Override
    public boolean existsByModuleName(String moduleName) {
        return false;
    }
}
