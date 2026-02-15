package models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_student_module", nullable = false)
    private Integer idStudentModule;

    @Column(name = "grade", precision = 4, scale = 2)
    private BigDecimal grade;

    @Column(name = "notes", length = 500)
    private String notes;

    public Grade(Integer idStudentModule, BigDecimal grade, String notes) {
        this.idStudentModule = idStudentModule;
        this.grade = grade;
        this.notes = notes;
    }
}