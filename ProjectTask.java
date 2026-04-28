package studentsuccessplanner;

/**
 * Represents a project task, extending SchoolTask.
 * Projects are typically longer-term assignments that benefit from breaking down into smaller steps.
 */
public class ProjectTask extends SchoolTask {

    /**
     * Constructs a ProjectTask with course name, task name, grade, and due date.
     *
     * @param courseName The name of the course
     * @param taskName The name or description of the project
     * @param grade The grade received on the project (0-100)
     * @param dueDate The due date of the project
     */
    public ProjectTask(String courseName, String taskName, double grade, String dueDate) {
        super(courseName, taskName, grade, dueDate);
    }

    /**
     * Gets the task type identifier.
     *
     * @return "Project"
     */
    @Override
    public String getTaskType() {
        return "Project";
    }

    /**
     * Gets a priority message specific to projects.
     *
     * @return A helpful reminder for project management
     */
    @Override
    public String getPriorityMessage() {
        return "Break into smaller steps";
    }
}
