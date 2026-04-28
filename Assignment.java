package studentsuccessplanner;

/**
 * Represents an assignment task, extending SchoolTask.
 * Assignments are regular coursework tasks with specific priority guidance.
 */
public class Assignment extends SchoolTask {

    /**
     * Constructs an Assignment with course name, task name, grade, and due date.
     *
     * @param courseName The name of the course
     * @param taskName The name or description of the assignment
     * @param grade The grade received for the assignment (0-100)
     * @param dueDate The due date of the assignment
     */
    public Assignment(String courseName, String taskName, double grade, String dueDate) {
        super(courseName, taskName, grade, dueDate);
    }

    /**
     * Gets the task type identifier.
     *
     * @return "Assignment"
     */
    @Override
    public String getTaskType() {
        return "Assignment";
    }

    /**
     * Gets a priority message specific to assignments.
     *
     * @return A helpful reminder for assignment completion
     */
    @Override
    public String getPriorityMessage() {
        return "Complete before the due date";
    }
}
