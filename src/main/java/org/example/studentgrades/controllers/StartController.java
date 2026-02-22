package org.example.studentgrades.controllers;

import dao.impl.StudentDAOImp;
import dao.impl.TeacherDAOImp;
import dao.impl.UserDAOImp;
import dao.interfaces.StudentDAO;
import dao.interfaces.TeacherDAO;
import dao.interfaces.UserDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Student;
import models.Teacher;
import models.User;
import models.UserType;

import java.io.IOException;

/**
 * Controlador para la pantalla de inicio de sesión
 */
public class StartController {

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passField;

    @FXML
    private ComboBox<UserType> roleCombo;

    @FXML
    private Button loginButton;

    @FXML
    private Button cleanButton;

    @FXML
    private Button exitButton;

    // DAOs
    private final UserDAO userDAO;
    private final TeacherDAO teacherDAO;
    private final StudentDAO studentDAO;

    public StartController() {
        this.userDAO = new UserDAOImp();
        this.teacherDAO = new TeacherDAOImp();
        this.studentDAO = new StudentDAOImp();
    }

    @FXML
    public void initialize() {
        // Cargar tipos de usuario en el ComboBox
        roleCombo.setItems(FXCollections.observableArrayList(UserType.values()));

        // Configurar acciones de botones
        loginButton.setOnAction(_ -> handleLogin());
        cleanButton.setOnAction(_ -> handleClean());
        exitButton.setOnAction(_ -> handleExit());

        // Enter en password ejecuta login
        passField.setOnAction(_ -> handleLogin());
    }

    /**
     * Maneja el inicio de sesión
     */
    private void handleLogin() {
        String username = userField.getText().trim();
        String password = passField.getText();
        UserType selectedRole = roleCombo.getValue();

        // Validaciones básicas
        if (username.isEmpty()) {
            showError("El campo de usuario no puede estar vacío");
            userField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("El campo de contraseña no puede estar vacío");
            passField.requestFocus();
            return;
        }

        if (selectedRole == null) {
            showError("Debes seleccionar un cargo");
            roleCombo.requestFocus();
            return;
        }

        try {
            // Validar credenciales (NOTA: En producción, hashear la contraseña)
            User user = userDAO.validateLogin(username, password);

            if (user == null) {
                showError("Usuario o contraseña incorrectos");
                passField.clear();
                passField.requestFocus();
                return;
            }

            // Verificar que el tipo de usuario coincida con el seleccionado
            if (!user.getUserType().equals(selectedRole)) {
                showError("El cargo seleccionado no coincide con tu usuario");
                roleCombo.requestFocus();
                return;
            }

            // Login exitoso - Abrir ventana correspondiente
            if (user.getUserType() == UserType.PROFESOR) {
                openTeacherWindow(user);
            } else if (user.getUserType() == UserType.ESTUDIANTE) {
                openStudentWindow(user);
            }

        } catch (Exception e) {
            showError("Error al iniciar sesión: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /**
     * Abre la ventana del profesor
     */
    private void openTeacherWindow(User user) {
        try {
            // Obtener datos del profesor
            Teacher teacher = teacherDAO.findByUserId(user.getId());

            if (teacher == null) {
                showError("No se encontraron datos de profesor");
                return;
            }

            // Cargar FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/studentgrades/teacher-view.fxml"));
            Parent root = loader.load();

            // Obtener controller y pasar datos
            TeacherController controller = loader.getController();
            controller.initData(teacher);

            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Panel de Profesor - " + user.getFirstName() + " " + user.getLastName());
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

            // Cerrar ventana de login
            closeWindow();

        } catch (IOException e) {
            showError("Error al abrir ventana de profesor: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /**
     * Abre la ventana del estudiante
     */
    private void openStudentWindow(User user) {
        try {
            // Obtener datos del estudiante
            Student student = studentDAO.findByUserId(user.getId());

            if (student == null) {
                showError("No se encontraron datos de estudiante");
                return;
            }

            // Cargar FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/studentgrades/student-view.fxml"));
            Parent root = loader.load();

            // Obtener controller y pasar datos
            StudentController controller = loader.getController();
            controller.initData(student);

            // Crear nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Panel de Estudiante - " + user.getFirstName() + " " + user.getLastName());
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

            // Cerrar ventana de login
            closeWindow();

        } catch (IOException e) {
            showError("Error al abrir ventana de estudiante: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());

        }
    }

    /**
     * Limpia los campos
     */
    private void handleClean() {
        userField.clear();
        passField.clear();
        roleCombo.setValue(null);
        userField.requestFocus();
    }

    /**
     * Cierra la aplicación
     */
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas salir?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    /**
     * Cierra la ventana actual
     */
    private void closeWindow() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra un mensaje de error
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}