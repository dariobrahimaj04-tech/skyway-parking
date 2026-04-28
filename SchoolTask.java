package studentsuccessplanner;

/**
 * Abstract parent class representing a general school task.
 * This class serves as the base for specialized task types (Assignment, Exam, ProjectTask)
 * and demonstrates inheritance and polymorphism principles.
 */
public class SchoolTask {
    protected String courseName;  // Name of the course
    protected String taskName;    // Name/description of the task
    protected double grade;       // Grade received for the task (0-100)
    protected String dueDate;     // Due date of the task

    /**
     * Constructs a SchoolTask with course name, task name, grade, and due date.
     *
     * @param courseName The name of the course
     * @param taskName The name or description of the task
     * @param grade The grade received (0-100)
     * @param dueDate The due date of the task
     */
    public SchoolTask(String courseName, String taskName, double grade, String dueDate) {
        this.courseName = courseName;
        this.taskName = taskName;
        this.grade = grade;
        this.dueDate = dueDate;
    }

    /**
     * Gets the course name.
     *
     * @return The course name
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * Gets the task name.
     *
     * @return The task name
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Gets the grade.
     *
     * @return The grade (0-100)
     */
    public double getGrade() {
        return grade;
    }

    /**
     * Gets the due date.
     *
     * @return The due date
     */
    public String getDueDate() {
        return dueDate;
    }

    /**
     * Gets the task type. This method can be overridden by subclasses.
     *
     * @return The type of task
     */
    public String getTaskType() {
        return "School Task";
    }

    /**
     * Gets a priority message for the task. This method can be overridden by subclasses.
     *
     * @return A helpful message related to task priority
     */
    public String getPriorityMessage() {
        return "General school task";
    }

    /**
     * Returns a string representation of the task in CSV format.
     * Used for saving tasks to a file.
     *
     * @return A comma-separated string representation of the task
     */
    @Override
    public String toString() {
        return courseName + "," + taskName + "," + grade + "," + dueDate + "," + getTaskType();
    }
}
