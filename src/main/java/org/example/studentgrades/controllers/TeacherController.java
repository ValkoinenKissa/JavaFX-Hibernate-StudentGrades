package org.example.studentgrades.controllers;

import dao.impl.*;
import dao.interfaces.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.*;
import models.Module;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Controlador para la ventana del profesor
 */
public class TeacherController {

    @FXML
    private Text teacherNameText;

    @FXML
    private ComboBox<Module> moduleCombo;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<StudentModuleRow> studentsTable;

    @FXML
    private TableColumn<StudentModuleRow, String> colStudent;

    @FXML
    private TableColumn<StudentModuleRow, String> colCourse;

    @FXML
    private TableColumn<StudentModuleRow, String> colGroup;

    @FXML
    private TableColumn<StudentModuleRow, String> colLastGrade;

    @FXML
    private TableColumn<StudentModuleRow, String> colAvgGrade;

    @FXML
    private TableColumn<StudentModuleRow, String> colCount;

    @FXML
    private Button refreshButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label selectedStudentLabel;

    @FXML
    private TextField gradeField;

    @FXML
    private TextField notesField;

    @FXML
    private Button saveGradeButton;

    @FXML
    private Label statusLabel;

    // DAOs
    private final TeacherDAO teacherDAO;
    private final StudentModuleDAO studentModuleDAO;
    private final GradeDAO gradeDAO;

    // Datos actuales
    private Teacher currentTeacher;
    private StudentModuleRow selectedRow;

    public TeacherController() {
        this.teacherDAO = new TeacherDAOImp();
        this.studentModuleDAO = new StudentModuleDAOImp();
        this.gradeDAO = new GradeDAOImp();
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colStudent.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().studentName()));
        colCourse.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().course()));
        colGroup.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().group()));
        colLastGrade.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().lastGrade()));
        colAvgGrade.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().avgGrade()));
        colCount.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().gradeCount()));

        // Evento al seleccionar un estudiante
        studentsTable.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                selectedRow = newVal;
                selectedStudentLabel.setText(newVal.studentName());
                statusLabel.setText("");
            }
        });

        // Evento al cambiar módulo
        moduleCombo.setOnAction(_ -> loadStudentsForModule());

        // Búsqueda en tiempo real
        searchField.textProperty().addListener((_, _, newVal) -> filterStudents(newVal));

        // Botones
        refreshButton.setOnAction(_ -> loadStudentsForModule());
        saveGradeButton.setOnAction(_ -> handleSaveGrade());
        logoutButton.setOnAction(_ -> handleLogout());
    }

    /**
     * Inicializa los datos del profesor
     */
    public void initData(Teacher teacher) {
        this.currentTeacher = teacher;
        loadTeacherData();
    }

    /**
     * Carga los datos del profesor
     */
    private void loadTeacherData() {
        if (currentTeacher == null) return;

        // Mostrar nombre
        teacherNameText.setText("Profesor/a: " + currentTeacher.getUser().getFirstName() + " " +
                currentTeacher.getUser().getLastName());

        // Cargar módulos
        loadTeacherModules();
    }

    /**
     * Carga los módulos del profesor
     */
    private void loadTeacherModules() {

        try {
            List<Module> modules = teacherDAO.getModulesByTeacher(currentTeacher.getId());
            ObservableList<Module> modulesList = FXCollections.observableArrayList(modules);
            moduleCombo.setItems(modulesList);

            // Se configura como se enseña cada modulo
            moduleCombo.setCellFactory(_ -> new ListCell<>() {
                @Override
                protected void updateItem(Module module, boolean empty) {
                    super.updateItem(module, empty);
                    setText(empty || module == null ? null : module.getModuleName());
                }
            });

            moduleCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Module module, boolean empty) {
                    super.updateItem(module, empty);
                    setText(empty || module == null ? null : module.getModuleName());
                }
            });

            // Seleccionar el primer módulo
            if (!modules.isEmpty()) {
                moduleCombo.getSelectionModel().selectFirst();
                loadStudentsForModule();
            }

        } catch (Exception e) {
            showError("Error al cargar módulos: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /**
     * Carga los estudiantes del módulo seleccionado
     */
    private void loadStudentsForModule() {
        Module selectedModule = moduleCombo.getValue();
        if (selectedModule == null) return;

        try {
            List<StudentModule> enrollments = studentModuleDAO.findByModule(selectedModule.getId());
            ObservableList<StudentModuleRow> rows = FXCollections.observableArrayList();

            for (StudentModule enrollment : enrollments) {
                Student student = enrollment.getStudent();
                String studentName = student.getUser().getFirstName() + " " + student.getUser().getLastName();

                // Obtener notas
                List<Grade> grades = gradeDAO.findByStudentModule(enrollment.getId());
                String lastGrade = "-";
                String avgGrade = "-";
                String gradeCount = String.valueOf(grades.size());

                if (!grades.isEmpty()) {
                    // Última nota
                    Grade latest = grades.getFirst(); // Ya está ordenado por ID DESC
                    lastGrade = String.format("%.2f", latest.getGrade());

                    // Nota media
                    BigDecimal average = gradeDAO.calculateAverageGrade(enrollment.getId());
                    avgGrade = String.format("%.2f", average);
                }

                StudentModuleRow row = new StudentModuleRow(
                        enrollment.getId(),
                        studentName,
                        student.getCourse(),
                        student.getGradeGroup(),
                        lastGrade,
                        avgGrade,
                        gradeCount
                );
                rows.add(row);
            }

            studentsTable.setItems(rows);

        } catch (Exception e) {
            showError("Error al cargar estudiantes: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /**
     * Filtra estudiantes por nombre
     */
    private void filterStudents(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            loadStudentsForModule();
            return;
        }

        ObservableList<StudentModuleRow> allRows = studentsTable.getItems();
        ObservableList<StudentModuleRow> filtered = FXCollections.observableArrayList();

        String lowerSearch = searchText.toLowerCase();
        for (StudentModuleRow row : allRows) {
            if (row.studentName().toLowerCase().contains(lowerSearch)) {
                filtered.add(row);
            }
        }

        studentsTable.setItems(filtered);
    }

    /**
     * Guarda una nueva nota
     */
    private void handleSaveGrade() {
        if (selectedRow == null) {
            statusLabel.setText("⚠ Debes seleccionar un alumno primero");
            return;
        }

        String gradeText = gradeField.getText().trim();
        if (gradeText.isEmpty()) {
            statusLabel.setText("⚠ Debes introducir una nota");
            gradeField.requestFocus();
            return;
        }

        try {
            // Validar nota
            BigDecimal gradeValue = new BigDecimal(gradeText);
            if (gradeValue.compareTo(BigDecimal.ZERO) < 0 ||
                    gradeValue.compareTo(new BigDecimal("10")) > 0) {
                statusLabel.setText("⚠ La nota debe estar entre 0 y 10");
                gradeField.requestFocus();
                return;
            }

            // Crear y guardar nota
            Grade newGrade = new Grade();
            StudentModule enrollment = studentModuleDAO.findById(selectedRow.enrollmentId());
            newGrade.setStudentModule(enrollment);
            newGrade.setGrade(gradeValue.setScale(2, RoundingMode.HALF_UP));
            newGrade.setNotes(notesField.getText().trim());

            gradeDAO.save(newGrade);

            // Actualizar tabla
            loadStudentsForModule();

            // Limpiar campos
            gradeField.clear();
            notesField.clear();
            statusLabel.setText("✓ Nota guardada exitosamente");

        } catch (NumberFormatException e) {
            statusLabel.setText("⚠ La nota debe ser un número válido");
            gradeField.requestFocus();
        } catch (Exception e) {
            statusLabel.setText("✗ Error: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /**
     * Cierra sesión
     */
    private void handleLogout() {
        try {
            // Abrir ventana de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/studentgrades/start-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ventana de inicio");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

            // Cerrar ventana actual
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("Error al cerrar la sesión: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
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

    /**
         * Clase auxiliar para representar una fila en la tabla
         */
        public record StudentModuleRow(Integer enrollmentId, String studentName, String course, String group,
                                       String lastGrade, String avgGrade, String gradeCount) {

    }
}