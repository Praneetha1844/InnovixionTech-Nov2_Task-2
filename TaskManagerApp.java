import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class Task {
    private String name;
    private Date dueDate;
    private int priority;

    public Task(String name, Date dueDate, int priority) {
        this.name = name;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }
}

class TaskManager {
    private List<Task> tasks;

    public TaskManager() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }
}

public class TaskManagerApp extends JFrame {

    private TaskManager taskManager;
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;

    public TaskManagerApp() {
        taskManager = new TaskManager();
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);

        setupUI();
        setupTimer();
    }

    private void setupUI() {
        setTitle("Task Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddTaskDialog();
            }
        });

        mainPanel.add(addButton, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(taskList), BorderLayout.CENTER);

        add(mainPanel);

        refreshTaskList();
    }

    private void setupTimer() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkDueDates();
            }
        }, 0, 10000); // Check due dates every 10 seconds
    }

    private void showAddTaskDialog() {
        JTextField nameField = new JTextField();
        JTextField dueDateField = new JTextField();
        JTextField priorityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Task Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Due Date (yyyy-MM-dd HH:mm):"));
        panel.add(dueDateField);
        panel.add(new JLabel("Priority (1-5):"));
        panel.add(priorityField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add Task",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                Date dueDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dueDateField.getText());
                int priority = Integer.parseInt(priorityField.getText());

                Task task = new Task(name, dueDate, priority);
                taskManager.addTask(task);
                refreshTaskList();
            } catch (ParseException | NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid input. Please try again.");
            }
        }
    }

    private void checkDueDates() {
        List<Task> tasks = taskManager.getTasks();
        Date now = new Date();

        for (Task task : tasks) {
            if (task.getDueDate().before(now)) {
                showNotification("Task Overdue", "Task '" + task.getName() + "' is overdue!");
            }
        }
    }

    private void showNotification(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void refreshTaskList() {
        taskListModel.clear();
        List<Task> tasks = taskManager.getTasks();

        for (Task task : tasks) {
            taskListModel.addElement(task.getName() + " (Priority: " + task.getPriority() + ")");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TaskManagerApp().setVisible(true);
            }
        });
    }
}
