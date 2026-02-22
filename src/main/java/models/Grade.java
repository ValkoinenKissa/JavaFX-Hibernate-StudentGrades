package models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(exclude = "studentModule")
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // No incluir ningún campo en equals() ni en hashCode()
@NoArgsConstructor
@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "grade", precision = 4, scale = 2)
    private BigDecimal grade;

    @Column(name = "notes", length = 500)
    private String notes;

    // Relación N:1 con StudentModule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_student_module", nullable = false)
    private StudentModule studentModule;

    // Constructores

    public Grade(StudentModule studentModule, BigDecimal grade) {
        this.studentModule = studentModule;
        this.grade = grade;
    }

    public Grade(StudentModule studentModule, BigDecimal grade, String notes) {
        this.studentModule = studentModule;
        this.grade = grade;
        this.notes = notes;
    }

}