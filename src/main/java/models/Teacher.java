package models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "teachers")
public class Teacher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // relación @OneToOne con User

    @Column(name = "id_user", nullable = false, unique = true)
    private Integer idUser;

    @Column(name = "department", length = 100)
    private String department;

    @Column(name = "specialty", length = 100)
    private String specialty;

    public Teacher(Integer idUser, String department, String specialty) {
        this.idUser = idUser;
        this.department = department;
        this.specialty = specialty;
    }
}