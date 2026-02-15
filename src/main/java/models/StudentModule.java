package models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "student_module")
public class StudentModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_student", nullable = false)
    private Integer idStudent;

    @Column(name = "id_module", nullable = false)
    private Integer idModule;

    public StudentModule(Integer idStudent, Integer idModule) {
        this.idStudent = idStudent;
        this.idModule = idModule;
    }
}