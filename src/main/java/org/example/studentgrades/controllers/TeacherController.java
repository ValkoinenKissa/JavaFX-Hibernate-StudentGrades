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


    @FXML
    private TableView<GradeRow> gradesTable;

    @FXML
    private TableColumn<GradeRow, String> colGradeValue;

    @FXML
    private TableColumn<GradeRow, String> colGradeNotes;

    @FXML
    private Button deleteGradeButton;

    @FXML
    private Button deleteAllGradesButton;

    @FXML private Button updateGradeButton;


    // DAOs
    private final TeacherDAO teacherDAO;
    private final StudentModuleDAO studentModuleDAO;
    private final GradeDAO gradeDAO;

    // Datos actuales
    private Teacher currentTeacher;
    private StudentModuleRow selectedRow;
    private Integer editingGradeId = null;   // id de la nota que se está editando

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


        // Configurar columnas del historial
        colGradeValue.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().gradeValue()));
        colGradeNotes.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().notes()));

        // Selección única
        gradesTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // Deshabilitar botones al inicio
        deleteGradeButton.setDisable(true);
        deleteAllGradesButton.setDisable(true);
        updateGradeButton.setDisable(true);

        // Cuando el usuario selecciona una nota en el historial, habilitar "Eliminar nota"
        gradesTable.getSelectionModel().selectedItemProperty().addListener((_, _, sel) ->
                deleteGradeButton.setDisable(sel == null));

        // Botones de borrado
        deleteGradeButton.setOnAction(_ -> handleDeleteSelectedGrade());
        deleteAllGradesButton.setOnAction(_ -> handleDeleteAllGrades());


        // Evento al seleccionar un estudiante
        studentsTable.getSelectionModel().selectedItemProperty().addListener((_, _, newVal) -> {
            if (newVal != null) {
                editingGradeId = null;
                updateGradeButton.setDisable(true);
                gradesTable.getSelectionModel().clearSelection();
                selectedRow = newVal;
                selectedStudentLabel.setText(newVal.studentName());
                statusLabel.setText("");


                // Cargar historial de notas del alumno en el módulo
                loadGradesHistory(selectedRow.enrollmentId());

                // Ya se puede borrar todas las notas (si existen o no, da igual)
                deleteAllGradesButton.setDisable(false);

                // Eliminar nota solo cuando selecciones una del historial
                deleteGradeButton.setDisable(true);

            }
            else {

                editingGradeId = null;
                updateGradeButton.setDisable(true);
                selectedRow = null;
                selectedStudentLabel.setText("-");
                gradesTable.setItems(FXCollections.observableArrayList());
                deleteAllGradesButton.setDisable(true);
                deleteGradeButton.setDisable(true);
            }

        });

           /*
    Listener para botón actualizar
     */


        gradesTable.getSelectionModel().selectedItemProperty().addListener((_, _, sel) -> {
            if (sel != null) {
                editingGradeId = sel.gradeId();   // guardamos el id de la nota
                gradeField.setText(sel.gradeValue().replace(",", ".")); // por si acaso
                notesField.setText(sel.notes());
                updateGradeButton.setDisable(false);
                statusLabel.setText("Editando nota ID: " + editingGradeId);
            } else {
                editingGradeId = null;
                updateGradeButton.setDisable(true);
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
        updateGradeButton.setOnAction(_ -> handleUpdateGrade());
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
            statusLabel.setText("Debes seleccionar un alumno primero");
            return;
        }

        String gradeText = gradeField.getText().trim();
        if (gradeText.isEmpty()) {
            statusLabel.setText("Debes introducir una nota");
            gradeField.requestFocus();
            return;
        }

        try {
            // Validar nota
            BigDecimal gradeValue = new BigDecimal(gradeText);
            if (gradeValue.compareTo(BigDecimal.ZERO) < 0 ||
                    gradeValue.compareTo(new BigDecimal("10")) > 0) {
                statusLabel.setText("La nota debe estar entre 0 y 10");
                gradeField.requestFocus();
                return;
            }

            Integer enrollmentId = selectedRow.enrollmentId();

            // Crear y guardar nota
            Grade newGrade = new Grade();
            StudentModule enrollment = studentModuleDAO.findById(enrollmentId);
            newGrade.setStudentModule(enrollment);
            newGrade.setGrade(gradeValue.setScale(2, RoundingMode.HALF_UP));
            newGrade.setNotes(notesField.getText().trim());

            gradeDAO.save(newGrade);

            // Actualizar tabla y historial
            loadStudentsForModule();

            // Cargar historial usando enrollmentId
            loadGradesHistory(enrollmentId);

            // Limpiar campos
            gradeField.clear();
            notesField.clear();
            statusLabel.setText("Nota guardada exitosamente");

        } catch (NumberFormatException e) {
            statusLabel.setText("La nota debe ser un número válido");
            gradeField.requestFocus();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /*
    Cargar el historial de notas
     */

    private void loadGradesHistory(Integer enrollmentId) {
        try {
            List<Grade> grades = gradeDAO.findByStudentModule(enrollmentId);
            ObservableList<GradeRow> rows = FXCollections.observableArrayList();

            for (Grade g : grades) {
                String notes = (g.getNotes() == null) ? "" : g.getNotes();
                rows.add(new GradeRow(
                        g.getId(),
                        String.format("%.2f", g.getGrade()),
                        notes
                ));
            }

            gradesTable.setItems(rows);

            // Si no hay notas, se apaga el botón Eliminar nota
            deleteGradeButton.setDisable(true);

        } catch (Exception e) {
            statusLabel.setText("Error cargando historial: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /*
    Borrado de notas
     */

    private void handleDeleteSelectedGrade() {
        if (selectedRow == null) {
            statusLabel.setText("Selecciona un alumno primero");
            return;
        }

        GradeRow selectedGrade = gradesTable.getSelectionModel().getSelectedItem();
        if (selectedGrade == null) {
            statusLabel.setText("Selecciona una nota del historial para eliminar");
            return;
        }

        if (cancelled("Eliminar nota", "¿Seguro que quieres eliminar la nota seleccionada?")) {
            statusLabel.setText("Acción cancelada");
            return;
        }

        try {
            gradeDAO.deleteById(selectedGrade.gradeId());
            statusLabel.setText("Nota eliminada");

            // Refrescar historial y tabla de alumnos
            loadGradesHistory(selectedRow.enrollmentId());
            loadStudentsForModule();

        } catch (Exception e) {
            statusLabel.setText("Error al eliminar: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /*
    Borrado masivo de notas
     */

    private void handleDeleteAllGrades() {
        if (selectedRow == null) {
            statusLabel.setText("Selecciona un alumno primero");
            return;
        }

        if (cancelled("Borrar todas", "¿Seguro que quieres borrar TODAS las notas de este alumno en el módulo?")) {
            statusLabel.setText("Acción cancelada");
            return;
        }

        try {
            gradeDAO.deleteByStudentModule(selectedRow.enrollmentId());
            statusLabel.setText("Todas las notas borradas");

            // Refrescar historial y tabla
            loadGradesHistory(selectedRow.enrollmentId());
            loadStudentsForModule();

        } catch (Exception e) {
            statusLabel.setText("Error al borrar: " + e.getMessage());
            System.out.println("(Depuración)" + e.getMessage());
        }
    }

    /*
    Helper de confirmación
     */

    private boolean cancelled(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().filter(b -> b == ButtonType.OK).isEmpty();
    }

    /*
    Metodo para gestionar la actualizacion
     */

    private void handleUpdateGrade() {
        if (selectedRow == null) {
            statusLabel.setText("Selecciona un alumno primero");
            return;
        }
        if (editingGradeId == null) {
            statusLabel.setText("Selecciona una nota del historial para editar");
            return;
        }

        String gradeText = gradeField.getText().trim().replace(",", ".");
        if (gradeText.isEmpty()) {
            statusLabel.setText("Debes introducir una nota");
            gradeField.requestFocus();
            return;
        }

        try {
            BigDecimal gradeValue = new BigDecimal(gradeText);
            if (gradeValue.compareTo(BigDecimal.ZERO) < 0 ||
                    gradeValue.compareTo(new BigDecimal("10")) > 0) {
                statusLabel.setText("Error: La nota debe estar entre 0 y 10");
                gradeField.requestFocus();
                return;
            }

            // Guardamos el enrollmentId antes de refrescar tablas (por seguridad)
            Integer enrollmentId = selectedRow.enrollmentId();

            // 1) Traer la nota por ID
            Grade grade = gradeDAO.findById(editingGradeId);
            if (grade == null) {
                statusLabel.setText("No se encontró la nota a editar");
                return;
            }

            // Modificar campos
            grade.setGrade(gradeValue.setScale(2, RoundingMode.HALF_UP));
            grade.setNotes(notesField.getText().trim());

            // UPDATE
            gradeDAO.update(grade);

            // Refrescar UI
            loadGradesHistory(enrollmentId);
            loadStudentsForModule();

            // Limpiar estado edición
            gradesTable.getSelectionModel().clearSelection();
            editingGradeId = null;
            updateGradeButton.setDisable(true);

            gradeField.clear();
            notesField.clear();

            statusLabel.setText("Nota actualizada correctamente");

        } catch (NumberFormatException e) {
            statusLabel.setText("La nota debe ser un número válido");
            gradeField.requestFocus();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
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

    public record GradeRow(Integer gradeId, String gradeValue, String notes) {

    }
}