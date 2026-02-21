package models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "student_module", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_student", "id_module"})
})
public class StudentModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Relación N:1 con Student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

    // Relación N:1 con Module
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_module", nullable = false)
    private Module module;

    // Relación 1:N con Grade (notas)
    @OneToMany(mappedBy = "studentModule", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Grade> grades = new HashSet<>();

    public StudentModule(Student student, Module module) {
        this.student = student;
        this.module = module;
    }

    // Metodos para añadir o eliminar un grade:

    public void addGrade(Grade grade) {
        this.grades.add(grade);
        grade.setStudentModule(this);
    }

    public void removeGrade(Grade grade) {
        this.grades.remove(grade);
        grade.setStudentModule(null);
    }
}