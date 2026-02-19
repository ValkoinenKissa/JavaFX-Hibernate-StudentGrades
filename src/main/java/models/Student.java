package models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "id_user", nullable = false, unique = true)
    private Integer idUser;

    @Column(name = "course", length = 50)
    private String course;

    @Column(name = "grade_group", length = 10)
    private String gradeGroup;

    public Student(Integer idUser, String course, String gradeGroup) {
        this.idUser = idUser;
        this.course = course;
        this.gradeGroup = gradeGroup;
    }
}