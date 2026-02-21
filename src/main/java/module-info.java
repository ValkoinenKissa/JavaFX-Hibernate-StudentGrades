module org.example.studentgrades {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;

    opens models to org.hibernate.orm.core;

    opens org.example.studentgrades to javafx.fxml;
    exports org.example.studentgrades;
    exports org.example.studentgrades.controllers;
    opens org.example.studentgrades.controllers to javafx.fxml;
}