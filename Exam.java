package studentsuccessplanner;

/**
 * Represents an exam task, extending SchoolTask.
 * Exams require advance studying and are high-priority assessment tasks.
 */
public class Exam extends SchoolTask {

    /**
     * Constructs an Exam with course name, task name, grade, and due date.
     *
     * @param courseName The name of the course
     * @param taskName The name or description of the exam
     * @param grade The grade received on the exam (0-100)
     * @param dueDate The date of the exam
     */
    public Exam(String courseName, String taskName, double grade, String dueDate) {
        super(courseName, taskName, grade, dueDate);
    }

    /**
     * Gets the task type identifier.
     *
     * @return "Exam"
     */
    @Override
    public String getTaskType() {
        return "Exam";
    }

    /**
     * Gets a priority message specific to exams.
     *
     * @return A helpful reminder for exam preparation
     */
    @Override
    public String getPriorityMessage() {
        return "Study early and review notes";
    }
}
