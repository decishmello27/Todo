
import javax.swing.SwingUtilities;

public class TodoApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TodoListFrame frame = new TodoListFrame();
            frame.setVisible(true);
        });
    }
}
