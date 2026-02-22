package models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"user", "modules"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // relación @OneToOne con User

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "specialty", length = 100)
    private String specialty;

    // Relaciones

    // Relación 1:1 con user

    @OneToOne
    @JoinColumn(name = "id_user", nullable = false, unique = true)
    private User user;

    // Relación N:M con Module (tabla intermedia teacher_module)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teacher_module",
            joinColumns = @JoinColumn(name = "teacher_id"),
            inverseJoinColumns = @JoinColumn(name = "module_id")
    )
    private Set<Module> modules = new HashSet<>();

    public Teacher(User user, String department, String specialty) {
        this.user = user;
        this.department = department;
        this.specialty = specialty;
    }

    // Métodos para añadir o eliminar modulos

    public void addModule(Module module) {
        this.modules.add(module);
        module.getTeachers().add(this);
    }

    public void removeModule(Module module) {
        this.modules.remove(module);
        module.getTeachers().remove(this);
    }
}