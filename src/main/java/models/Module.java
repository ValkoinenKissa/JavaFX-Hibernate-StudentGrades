package models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
}