import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.*;

public class TodoListFrame extends JFrame {
    private final DefaultListModel<Task> taskListModel;
    private final JList<Task> taskList;
    private final JButton addButton, removeButton, saveButton, loadButton;
    private final JCheckBox showCompletedCheckBox;
    private final ArrayList<Task> allTasks;

    public TodoListFrame() {
        setTitle("To-Do List");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        allTasks = new ArrayList<>();
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        JScrollPane scrollPane = new JScrollPane(taskList);

        addButton = new JButton("Přidat úkol");
        removeButton = new JButton("Odebrat úkol");
        saveButton = new JButton("Uložit");
        loadButton = new JButton("Načíst");
        showCompletedCheckBox = new JCheckBox("Zobrazit splněné úkoly");

        addButton.addActionListener(e -> addTask());
        removeButton.addActionListener(e -> removeTask());
        saveButton.addActionListener(e -> saveTasks());
        loadButton.addActionListener(e -> loadTasks());
        showCompletedCheckBox.addActionListener(e -> refreshTaskListDisplay());

        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index != -1) {
                    Rectangle rect = taskList.getCellBounds(index, index);
                    if (rect != null && e.getX() < rect.x + 20) {
                        Task task = taskListModel.getElementAt(index);
                        task.setDone(!task.isDone());
                        refreshTaskListDisplay();
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(buttonPanel, BorderLayout.CENTER);
        controlPanel.add(showCompletedCheckBox, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        loadTasks(); 
    }

    private void addTask() {
        JTextField descriptionField = new JTextField();
        JTextField deadlineField = new JTextField(LocalDate.now().plusWeeks(1).toString());
        String[] priorities = {"NIZKA", "STREDNI", "VYSOKA"};
        JComboBox<String> priorityBox = new JComboBox<>(priorities);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Popis:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Deadline (YYYY-MM-DD):"));
        panel.add(deadlineField);
        panel.add(new JLabel("Priorita:"));
        panel.add(priorityBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Nový úkol", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String description = descriptionField.getText().trim();
                LocalDate deadline = LocalDate.parse(deadlineField.getText().trim());
                Task.Priority priority = Task.Priority.valueOf(priorityBox.getSelectedItem().toString());

                if (!description.isEmpty()) {
                    Task newTask = new Task(description, deadline, priority);
                    allTasks.add(newTask);
                    sortAndRefreshTasks();
                }
            } catch (DateTimeParseException | IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Neplatné zadání. Zkus to znovu.");
            }
        }
    }

    private void removeTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task taskToRemove = taskListModel.getElementAt(selectedIndex);
            allTasks.remove(taskToRemove);
            refreshTaskListDisplay();
        }
    }

    private void sortAndRefreshTasks() {
        Collections.sort(allTasks);
        refreshTaskListDisplay();
    }

    private void saveTasks() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            oos.writeObject(allTasks);
            JOptionPane.showMessageDialog(this, "Úkoly byly uloženy!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Chyba při ukládání: " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadTasks() {
        ArrayList<Task> loadedTasks = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tasks.dat"))) {
            loadedTasks = (ArrayList<Task>) ois.readObject();
        } catch (FileNotFoundException ex) {
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Chyba při načítání souboru úkolů: " + ex.getMessage());
        }
        

        for (Task loadedTask : loadedTasks) {
            if (!allTasks.contains(loadedTask)) {
                allTasks.add(loadedTask);
            }
        }
        sortAndRefreshTasks();
    }
    
    private void refreshTaskListDisplay() {
        taskListModel.clear();
        boolean showCompleted = showCompletedCheckBox.isSelected();

        for (Task task : allTasks) {
            if (showCompleted || !task.isDone()) {
                taskListModel.addElement(task);
            }
        }
    }
    
    private static class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {
        private final JCheckBox checkBox;
        private final JLabel label;

        public TaskCellRenderer() {
            setLayout(new BorderLayout(5, 0));
            checkBox = new JCheckBox();
            label = new JLabel();
            add(checkBox, BorderLayout.WEST);
            add(label, BorderLayout.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean cellHasFocus) {
            checkBox.setSelected(task.isDone());
            checkBox.setBackground(list.getBackground());
            checkBox.setOpaque(true);

            label.setText(task.toString());
            label.setBackground(list.getBackground());
            label.setOpaque(true);

            Color textColor = Color.BLACK;
            if (task.isDone()) {
                textColor = Color.GRAY;
                label.setFont(label.getFont().deriveFont(Font.ITALIC));
            } else {
                switch (task.getPriority()) {
                    case VYSOKA -> textColor = Color.RED;
                    case STREDNI -> textColor = Color.ORANGE;
                    case NIZKA -> textColor = Color.GREEN.darker();
                }
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
            }
            label.setForeground(textColor);

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                checkBox.setBackground(list.getSelectionBackground());
                label.setBackground(list.getSelectionBackground());
            } else {
                setBackground(list.getBackground());
                checkBox.setBackground(list.getBackground());
                label.setBackground(list.getBackground());
            }
            
            return this;
        }
    }
}
