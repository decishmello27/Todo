import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;


public class Task implements Serializable, Comparable<Task> {
    public enum Priority { NIZKA, STREDNI, VYSOKA }

    private final String description;
    private final LocalDate deadline;
    private final Priority priority;
    private boolean isDone;

    public Task (String description, LocalDate deadline, Priority priority) {
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Priority getPriority() {
        return priority;
    }
    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        return (isDone ? "[SPLNĚNO] " : "") + description + " (do " + deadline.toString() + ", " + priority.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Task)) return false;
        Task task = (Task) obj;
        return Objects.equals(this.description, task.description) && Objects.equals(this.deadline, task.deadline) 
        && this.priority == task.priority && this.isDone == task.isDone; 
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, deadline, priority, isDone);
    }

    @Override
    public int compareTo(Task other) {
        int result = this.deadline.compareTo(other.deadline);

        if (result == 0) {
            return this.priority.ordinal() - other.priority.ordinal(); 
        }
        return result;
    }
}
