package studentsuccessplanner;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.io.*;
import java.util.regex.Pattern;

/******************************************************************************
* Final Exam Project - Student Success Planner
* Author: Dario Brahimaj
* Project Purpose: Student Success Planner helps students organize school tasks,
* track grades, calculate course averages, and save/load task records.
* Input: Course name, task name, grade, due date, and task type.
* Desired Output: A GUI list showing school tasks, grades, due dates, task types,
* priority messages, and calculated grade average.
* Variables and Classes: SchoolTask, Assignment, Exam, ProjectTask,
* ArrayList<SchoolTask>, TextField, ComboBox, ListView, Button.
* Formulas: Average grade = total grades / number of tasks.
* Description of Main Algorithm: The user enters task information. The program
* creates an Assignment, Exam, or ProjectTask object, stores it in an ArrayList,
* displays it in the GUI, calculates the grade average, and saves/loads data
* using a text file.
* Date: April 2026
* Improvements: Enhanced validation, robust file loading, automatic average updates,
* clear all functionality, and professional GUI design.
********************************************************************************/

public class StudentSuccessPlanner extends Application {

    private final ArrayList<SchoolTask> taskList = new ArrayList<>();
    private final ObservableList<String> displayList = FXCollections.observableArrayList();

    private TextField courseField;
    private TextField taskField;
    private TextField gradeField;
    private TextField dueDateField;
    private ComboBox<String> taskTypeBox;
    private ListView<String> listView;
    private Label averageLabel;
    private Label countLabel;

    // Date validation pattern: MM/DD/YYYY
    private static final Pattern DATE_PATTERN = Pattern.compile("^(0[1-9]|1[0-2])/(0[1-9]|[12]\\d|3[01])/\\d{4}$");

    @Override
    public void start(Stage primaryStage) {

        Label titleLabel = new Label("Student Success Planner");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");

        Label subtitleLabel = new Label("Track assignments, exams, projects, due dates, and grades in one organized dashboard.");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #6b7280;");

        courseField = createTextField("Example: CIS 115");
        taskField = createTextField("Example: Final Project");
        gradeField = createTextField("Example: 95");
        dueDateField = createTextField("Example: 04/30/2026");

        taskTypeBox = new ComboBox<>();
        taskTypeBox.getItems().addAll("Assignment", "Exam", "Project");
        taskTypeBox.setValue("Assignment");
        taskTypeBox.setPrefWidth(220);
        taskTypeBox.setStyle("-fx-font-size: 13px; -fx-background-radius: 8;");

        Button addButton = createPrimaryButton("Add Task");
        Button averageButton = createSecondaryButton("Calculate Average");
        Button saveButton = createSecondaryButton("Save to File");
        Button loadButton = createSecondaryButton("Load from File");
        Button deleteButton = createDangerButton("Delete Selected");
        Button clearButton = createSecondaryButton("Clear Fields");
        Button clearAllButton = createDangerButton("Clear All Tasks");

        listView = new ListView<>();
        listView.setItems(displayList);
        listView.setPrefHeight(280);
        listView.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #d1d5db;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-font-size: 13px;"
        );

        averageLabel = new Label("Average Grade: N/A");
        averageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2563eb;");

        countLabel = new Label("Total Tasks: 0");
        countLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        addButton.setOnAction(e -> addTask());
        averageButton.setOnAction(e -> calculateAverage());
        saveButton.setOnAction(e -> saveToFile());
        loadButton.setOnAction(e -> loadFromFile());
        deleteButton.setOnAction(e -> deleteSelected());
        clearButton.setOnAction(e -> clearFields());
        clearAllButton.setOnAction(e -> clearAllTasks());

        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(20));
        inputGrid.setHgap(15);
        inputGrid.setVgap(14);
        inputGrid.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #e5e7eb;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        inputGrid.add(createInputLabel("Course Name:"), 0, 0);
        inputGrid.add(courseField, 1, 0);

        inputGrid.add(createInputLabel("Task Name:"), 0, 1);
        inputGrid.add(taskField, 1, 1);

        inputGrid.add(createInputLabel("Grade:"), 0, 2);
        inputGrid.add(gradeField, 1, 2);

        inputGrid.add(createInputLabel("Due Date:"), 0, 3);
        inputGrid.add(dueDateField, 1, 3);

        inputGrid.add(createInputLabel("Task Type:"), 0, 4);
        inputGrid.add(taskTypeBox, 1, 4);

        VBox leftPanel = new VBox(18);
        leftPanel.getChildren().addAll(inputGrid);

        HBox buttonBoxOne = new HBox(12);
        buttonBoxOne.getChildren().addAll(addButton, averageButton, saveButton);

        HBox buttonBoxTwo = new HBox(12);
        buttonBoxTwo.getChildren().addAll(loadButton, deleteButton, clearButton);

        HBox buttonBoxThree = new HBox(12);
        buttonBoxThree.getChildren().addAll(clearAllButton);

        HBox statsBox = new HBox(30);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle(
                "-fx-background-color: #eff6ff;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: #bfdbfe;" +
                "-fx-border-radius: 12;"
        );
        statsBox.getChildren().addAll(averageLabel, countLabel);

        Label listTitle = new Label("Saved School Tasks");
        listTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        VBox listPanel = new VBox(12);
        listPanel.getChildren().addAll(listTitle, listView);
        listPanel.setPadding(new Insets(18));
        listPanel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: #e5e7eb;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.08), 10, 0, 0, 3);"
        );

        VBox root = new VBox(18);
        root.setPadding(new Insets(25));
        root.setStyle("-fx-background-color: #f3f4f6;");
        root.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                leftPanel,
                buttonBoxOne,
                buttonBoxTwo,
                buttonBoxThree,
                statsBox,
                listPanel
        );

        Scene scene = new Scene(root, 900, 800);

        primaryStage.setTitle("Student Success Planner");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates a styled TextField with a placeholder prompt.
     *
     * @param prompt The placeholder text to display
     * @return A formatted TextField
     */
    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(220);
        field.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-background-radius: 8;" +
                "-fx-border-radius: 8;" +
                "-fx-border-color: #d1d5db;" +
                "-fx-padding: 8;"
        );
        return field;
    }

    /**
     * Creates a styled input label for form fields.
     *
     * @param text The label text
     * @return A formatted Label
     */
    private Label createInputLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        return label;
    }

    /**
     * Creates a primary button (blue color for main actions).
     *
     * @param text The button label
     * @return A formatted Button
     */
    private Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setStyle(
                "-fx-background-color: #2563eb;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9;"
        );
        return button;
    }

    /**
     * Creates a secondary button (gray color for utility actions).
     *
     * @param text The button label
     * @return A formatted Button
     */
    private Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setStyle(
                "-fx-background-color: #e5e7eb;" +
                "-fx-text-fill: #111827;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9;"
        );
        return button;
    }

    /**
     * Creates a danger button (red color for destructive actions).
     *
     * @param text The button label
     * @return A formatted Button
     */
    private Button createDangerButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setStyle(
                "-fx-background-color: #dc2626;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 9;"
        );
        return button;
    }

    /**
     * Validates the date format (MM/DD/YYYY).
     *
     * @param date The date string to validate
     * @return true if the date format is valid, false otherwise
     */
    private boolean isValidDate(String date) {
        return DATE_PATTERN.matcher(date).matches();
    }

    /**
     * Adds a new task to the task list after validating all inputs.
     * Performs validation on course name, task name, grade, and due date format.
     */
    private void addTask() {
        try {
            String course = courseField.getText().trim();
            String task = taskField.getText().trim();
            String dueDate = dueDateField.getText().trim();
            String type = taskTypeBox.getValue();

            // Validate empty fields
            if (course.isEmpty()) {
                showAlert("Validation Error", "Course name cannot be blank.");
                return;
            }
            if (task.isEmpty()) {
                showAlert("Validation Error", "Task name cannot be blank.");
                return;
            }
            if (dueDate.isEmpty()) {
                showAlert("Validation Error", "Due date cannot be blank.");
                return;
            }

            // Validate date format
            if (!isValidDate(dueDate)) {
                showAlert("Validation Error", "Due date must be in MM/DD/YYYY format (e.g., 04/30/2026).");
                return;
            }

            // Validate and parse grade
            double grade;
            try {
                grade = Double.parseDouble(gradeField.getText().trim());
            } catch (NumberFormatException ex) {
                showAlert("Validation Error", "Grade must be a valid number.");
                return;
            }

            if (grade < 0 || grade > 100) {
                showAlert("Validation Error", "Grade must be between 0 and 100.");
                return;
            }

            // Create appropriate task type using switch expression
            SchoolTask schoolTask = switch (type) {
                case "Exam" -> new Exam(course, task, grade, dueDate);
                case "Project" -> new ProjectTask(course, task, grade, dueDate);
                default -> new Assignment(course, task, grade, dueDate);
            };

            taskList.add(schoolTask);
            displayList.add(formatTask(schoolTask));

            updateTaskCount();
            calculateAverage(); // Auto-update average
            clearFields();
            showAlert("Success", "Task added successfully!");

        } catch (Exception ex) {
            showAlert("Error", "An unexpected error occurred: " + ex.getMessage());
        }
    }

    /**
     * Formats a SchoolTask for display in the ListView.
     *
     * @param task The SchoolTask to format
     * @return A formatted string representation of the task
     */
    private String formatTask(SchoolTask task) {
        return task.getCourseName() + "   |   "
                + task.getTaskName() + "   |   "
                + task.getTaskType() + "   |   Grade: "
                + String.format("%.2f", task.getGrade()) + "%   |   Due: "
                + task.getDueDate() + "   |   Note: "
                + task.getPriorityMessage();
    }

    /**
     * Calculates and displays the average grade of all tasks.
     * Updates the averageLabel with the calculated average or N/A if no tasks exist.
     */
    private void calculateAverage() {
        if (taskList.isEmpty()) {
            averageLabel.setText("Average Grade: N/A");
            return;
        }

        double total = 0;
        for (SchoolTask task : taskList) {
            total += task.getGrade();
        }

        double average = total / taskList.size();
        averageLabel.setText("Average Grade: " + String.format("%.2f", average) + "%");
    }

    /**
     * Saves all tasks in the task list to tasks.txt.
     * Displays success or error message upon completion.
     */
    private void saveToFile() {
        try {
            if (taskList.isEmpty()) {
                showAlert("Save Error", "No tasks to save.");
                return;
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter("tasks.txt"))) {
                for (SchoolTask task : taskList) {
                    writer.println(task.toString());
                }
            }
            showAlert("Success", "Tasks saved to tasks.txt.");

        } catch (IOException ex) {
            showAlert("File Error", "Could not save tasks: " + ex.getMessage());
        }
    }

    /**
     * Loads tasks from tasks.txt with robust error handling.
     * Skips malformed lines and continues loading valid tasks.
     * Automatically updates the average grade and task count.
     */
    private void loadFromFile() {
        try {
            File file = new File("tasks.txt");
            if (!file.exists()) {
                showAlert("File Error", "tasks.txt not found. Save tasks first.");
                return;
            }

            taskList.clear();
            displayList.clear();
            int successCount = 0;
            int failCount = 0;

            try (BufferedReader reader = new BufferedReader(new FileReader("tasks.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] parts = line.split(",");

                        // Validate that we have all 5 required parts
                        if (parts.length < 5) {
                            failCount++;
                            continue;
                        }

                        String course = parts[0].trim();
                        String task = parts[1].trim();
                        String dueDateStr = parts[3].trim();
                        String type = parts[4].trim();

                        // Validate and parse grade
                        double grade;
                        try {
                            grade = Double.parseDouble(parts[2].trim());
                        } catch (NumberFormatException ex) {
                            failCount++;
                            continue;
                        }

                        // Validate grade range
                        if (grade < 0 || grade > 100) {
                            failCount++;
                            continue;
                        }

                        // Create appropriate task type
                        SchoolTask schoolTask = switch (type) {
                            case "Exam" -> new Exam(course, task, grade, dueDateStr);
                            case "Project" -> new ProjectTask(course, task, grade, dueDateStr);
                            default -> new Assignment(course, task, grade, dueDateStr);
                        };

                        taskList.add(schoolTask);
                        displayList.add(formatTask(schoolTask));
                        successCount++;

                    } catch (Exception ex) {
                        failCount++;
                        // Continue to next line instead of crashing
                    }
                }
            }

            updateTaskCount();
            calculateAverage(); // Auto-update average

            String message = "Loaded " + successCount + " task(s)";
            if (failCount > 0) {
                message += " (" + failCount + " malformed line(s) skipped)";
            }
            showAlert("Success", message);

        } catch (IOException ex) {
            showAlert("File Error", "Could not load tasks: " + ex.getMessage());
        }
    }

    /**
     * Deletes the selected task from the list.
     * Automatically updates the average grade and task count.
     */
    private void deleteSelected() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();

        if (selectedIndex >= 0) {
            taskList.remove(selectedIndex);
            displayList.remove(selectedIndex);
            updateTaskCount();
            calculateAverage(); // Auto-update average
            showAlert("Success", "Task deleted successfully.");
        } else {
            showAlert("Selection Error", "Please select a task to delete.");
        }
    }

    /**
     * Clears all tasks from the task list after confirmation.
     * This is a destructive action and requires user confirmation.
     */
    private void clearAllTasks() {
        if (taskList.isEmpty()) {
            showAlert("Clear Error", "No tasks to clear.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Clear All");
        confirmAlert.setHeaderText("Are you sure?");
        confirmAlert.setContentText("This will delete ALL tasks. This action cannot be undone.");

        if (confirmAlert.showAndWait().orElse(Alert.ButtonType.CANCEL) == Alert.ButtonType.OK) {
            taskList.clear();
            displayList.clear();
            updateTaskCount();
            averageLabel.setText("Average Grade: N/A");
            showAlert("Success", "All tasks cleared.");
        }
    }

    /**
     * Clears all input fields and resets task type to default.
     */
    private void clearFields() {
        courseField.clear();
        taskField.clear();
        gradeField.clear();
        dueDateField.clear();
        taskTypeBox.setValue("Assignment");
    }

    /**
     * Updates the task count label with the current number of tasks.
     */
    private void updateTaskCount() {
        countLabel.setText("Total Tasks: " + taskList.size());
    }

    /**
     * Displays an alert dialog with the specified title and message.
     *
     * @param title The title of the alert
     * @param message The message content of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
