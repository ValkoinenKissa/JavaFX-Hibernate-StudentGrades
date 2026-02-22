package models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"teachers", "enrollments"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // No incluir ningún campo en equals() ni en hashCode()
@NoArgsConstructor
@Entity
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "module_name", length = 150)
    private String moduleName;

    @Column(name = "course", length = 50)
    private String course;

    @Column(name = "semanal_hours")
    private Integer semanalHours;

    public Module(String moduleName, String course, Integer semanalHours) {
        this.moduleName = moduleName;
        this.course = course;
        this.semanalHours = semanalHours;
    }

    // Relación N:M con Teacher (lado inverso)
    @ManyToMany(mappedBy = "modules", fetch = FetchType.LAZY)
    private Set<Teacher> teachers = new HashSet<>();

    // Relación 1:N con StudentModule (matrículas)
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<StudentModule> enrollments = new HashSet<>();

    // Metodos para añadir o eliminar enrollments

    public void addEnrollment(StudentModule enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setModule(this);
    }

    public void removeEnrollment(StudentModule enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setModule(null);
    }
}