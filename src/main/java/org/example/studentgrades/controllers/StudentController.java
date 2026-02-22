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
import javafx.collections.transformation.FilteredList;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
/**
 * Controlador para la ventana del estudiante
 */
public class StudentController {

    @FXML
    private Text studentNameText;

    @FXML
    private ComboBox<Module> moduleFilterCombo;

    @FXML
    private Label overallAvgLabel;

    @FXML
    private Button logoutButton;

    @FXML
    private TableView<ModuleSummaryRow> modulesTable;

    @FXML
    private TableColumn<ModuleSummaryRow, String> colModuleName;

    @FXML
    private TableColumn<ModuleSummaryRow, String> colLastGrade;

    @FXML
    private TableColumn<ModuleSummaryRow, String> colAvgGrade;

    @FXML
    private TableColumn<ModuleSummaryRow, String> colStatus;

    @FXML
    private ListView<String> gradesList;

    private final StudentModuleDAO studentModuleDAO;
    private final GradeDAO gradeDAO;
    private static final BigDecimal PASS_GRADE = new BigDecimal("5.0");
    private boolean loading = false;

    // Datos actuales
    private Student currentStudent;

    public StudentController() {
        // DAOs
        this.studentModuleDAO = new StudentModuleDAOImp();
        this.gradeDAO = new GradeDAOImp();
    }

    @FXML
    public void initialize() {
        // Configurar columnas de la tabla
        colModuleName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().moduleName()));
        colLastGrade.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().lastGrade()));
        colAvgGrade.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().avgGrade()));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status()));

        // Evento al seleccionar un módulo en la tabla


        filteredRows = new FilteredList<>(masterRows, _ -> true);
        modulesTable.setItems(filteredRows);

        modulesTable.getSelectionModel().selectedItemProperty().addListener((_,
                                                                             _, newVal) -> {
            if (newVal != null) {
                loadGradesForModule(newVal.enrollmentId());
            }
        });

        // Evento al filtrar por módulo
        moduleFilterCombo.valueProperty().addListener((_, _, val) -> {
            if (!loading)  applyFilter(val);
                });

        // Botón logout
        logoutButton.setOnAction(_ -> handleLogout());
    }


    private final ObservableList<ModuleSummaryRow> masterRows = FXCollections.observableArrayList();
    private FilteredList<ModuleSummaryRow> filteredRows;

    /**
     * Inicializa los datos del estudiante
     */
    public void initData(Student student) {
        this.currentStudent = student;
        loadStudentData();
    }

    /**
     * Carga los datos del estudiante
     */
    private void loadStudentData() {
        if (currentStudent == null) return;

        // Mostrar nombre
        studentNameText.setText("Alumno/a: " + currentStudent.getUser().getFirstName() + " " +
                currentStudent.getUser().getLastName() + " (" + currentStudent.getCourse() + " - " +
                currentStudent.getGradeGroup() + ")");

        // Cargar módulos
        loadStudentModules();

        // Calcular y mostrar media general
        calculateOverallAverage();
    }

    /**
     * Carga los módulos del estudiante
     */
    private void loadStudentModules() {
        try {
            List<StudentModule> enrollments = studentModuleDAO.findByStudent(currentStudent.getId());
            masterRows.clear();

            // También cargar combo de filtros
            ObservableList<Module> modules = FXCollections.observableArrayList();

            // Evitar duplicados
            java.util.Set<Integer> seen = new java.util.HashSet<>();

            for (StudentModule enrollment : enrollments) {
                Module module = enrollment.getModule();


                if (seen.add(module.getId())) {
                    modules.add(module);
                }

                Grade latest = gradeDAO.findLatestGrade(enrollment.getId());
                BigDecimal average = gradeDAO.calculateAverageGrade(enrollment.getId());
                if (average == null) average = BigDecimal.ZERO;


                String lastGrade = (latest == null) ? "-" : String.format("%.2f", latest.getGrade());
                String avgGrade = (average.compareTo(BigDecimal.ZERO) == 0) ? "-" : String.format("%.2f", average);

                String status;
                if (latest == null) {
                    status = "Sin notas";
                } else if (average.compareTo(PASS_GRADE) >= 0) {
                    status = "Aprobado";
                } else {
                    status = "Suspenso";
                }


                masterRows.add(new ModuleSummaryRow(
                        enrollment.getId(),
                        module.getId(),
                        module.getModuleName(),
                        lastGrade,
                        avgGrade,
                        status

                ));
            }


            loading = true;
            // Configurar combobox solo una vez
            moduleFilterCombo.setItems(modules);
            moduleFilterCombo.setCellFactory(_ -> new ListCell<>() {
                @Override

                protected void updateItem(Module module, boolean empty) {
                    super.updateItem(module, empty);
                    setText(empty || module == null ? null : module.getModuleName());
                }
            });
            moduleFilterCombo.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Module module, boolean empty) {
                    super.updateItem(module, empty);
                    setText(empty || module == null ? "Todos" : module.getModuleName());
                }
            });

            // Mostrar "Todos" al inicio (value null = todos)
            moduleFilterCombo.setValue(null);
            loading = false;

            // aplica el filtro una sola vez, ya con to-do cargado
            applyFilter(null);

            // Selecciona primera fila para cargar notas automáticamente
            if (!filteredRows.isEmpty()) {
                modulesTable.getSelectionModel().select(0);
            }

        } catch (Exception e) {
            showError("Error al cargar módulos: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /*
    Filtro para combobox
     */

    private void applyFilter(Module selectedModule) {
        if (selectedModule == null) {
            filteredRows.setPredicate(_ -> true); // todos
        } else {
            int id = selectedModule.getId();
            filteredRows.setPredicate(row -> row.moduleId() != null && row.moduleId().equals(id));
        }

        // Selección segura tras filtrar
        if (!filteredRows.isEmpty()) {
            modulesTable.getSelectionModel().select(0);
        } else {
            gradesList.setItems(FXCollections.observableArrayList("No hay módulos para mostrar"));
        }
    }

    /**
     * Carga las notas de un módulo específico
     */
    private void loadGradesForModule(Integer enrollmentId) {
        try {
            List<Grade> grades = gradeDAO.findByStudentModule(enrollmentId);
            ObservableList<String> gradeItems = FXCollections.observableArrayList();

            if (grades.isEmpty()) {
                gradeItems.add("No hay notas registradas");
            } else {
                for (Grade grade : grades) {
                    String item = String.format("%.2f", grade.getGrade());
                    if (grade.getNotes() != null && !grade.getNotes().isEmpty()) {
                        item += " - " + grade.getNotes();
                    }
                    gradeItems.add(item);
                }
            }

            gradesList.setItems(gradeItems);

        } catch (Exception e) {
            showError("Error al cargar notas: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());

        }
    }

    /**
     * Calcula y muestra la nota media general
     */
    private void calculateOverallAverage() {
        try {
            BigDecimal overallAvg = gradeDAO.calculateOverallAverageByStudent(currentStudent.getId());

            if (overallAvg.compareTo(BigDecimal.ZERO) == 0) {
                overallAvgLabel.setText("-");
            } else {
                overallAvgLabel.setText(String.format("%.2f", overallAvg));
            }

        } catch (Exception e) {
            overallAvgLabel.setText("-");
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
            stage.setTitle("Inicio de Sesión");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();

            // Cerrar ventana actual
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showError("Error al cerrar sesión: " + e.getMessage());
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
    public record ModuleSummaryRow(
            Integer enrollmentId,
            Integer moduleId,
            String moduleName,
            String lastGrade,
            String avgGrade,
            String status) {

    }
}