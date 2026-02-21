package org.example.studentgrades;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentGrades extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StudentGrades.class.getResource("start-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 780, 430);
        stage.setTitle("Ventana de inicio");
        stage.setScene(scene);
        stage.setResizable(false); // Redimension en falso
        stage.show();
    }
}
