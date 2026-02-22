package models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = {"enrollments", "user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "course", length = 50)
    private String course;

    @Column(name = "grade_group", length = 10)
    private String gradeGroup;

    // Relación 1:1 con User
    @OneToOne
    @JoinColumn(name = "id_user", nullable = false, unique = true)
    private User user;

    // Relación 1:N con StudentModule (matrículas)
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<StudentModule> enrollments = new HashSet<>();

    public Student(User user, String course, String gradeGroup) {
        this.user = user;
        this.course = course;
        this.gradeGroup = gradeGroup;
    }

    public void addEnrollment(StudentModule enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setStudent(this);
    }

    public void removeEnrollment(StudentModule enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setStudent(null);
    }
}