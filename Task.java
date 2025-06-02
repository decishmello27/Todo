import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;


public class Task implements Serializable, Comparable<Task> {
    public enum Priority { NIZKA, STREDNI, VYSOKA }

    private final String description;
    private final LocalDate deadline;
    private final Priority priority;
    private boolean isDone; // Nová proměnná pro stav splnění úkolu

    public Task (String description, LocalDate deadline, Priority priority) {
        this.description = description;
        this.deadline = deadline;
        this.priority = priority;
        this.isDone = false; // Úkol je při vytvoření nesplněný
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

    // Nové metody pro stav splnění úkolu
    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    @Override
    public String toString() {
        // Upraveno pro zobrazení stavu splnění
        return (isDone ? "[SPLNĚNO] " : "") + description + " (do " + deadline.toString() + ", " + priority.toString() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Task)) return false;
        Task task = (Task) obj;
        // Upraveno pro zohlednění isDone při porovnávání
        return Objects.equals(this.description, task.description) && Objects.equals(this.deadline, task.deadline) 
        && this.priority == task.priority && this.isDone == task.isDone; 
    }

    @Override
    public int hashCode() {
        // Upraveno pro zohlednění isDone při výpočtu hash kódu
        return Objects.hash(description, deadline, priority, isDone);
    }

    @Override
    public int compareTo(Task other) {
        // Nejprve porovnáme deadliny
        int result = this.deadline.compareTo(other.deadline);

        // Pokud jsou deadliny stejné, porovnáme priority
        if (result == 0) {
            // Nižší ordinal znamená vyšší prioritu (NIZKA = 0, STREDNI = 1, VYSOKA = 2)
            // Chceme, aby VYSOKA byla první (menší hodnota), proto odečítáme
            return this.priority.ordinal() - other.priority.ordinal(); 
        }
        return result;
    }
}