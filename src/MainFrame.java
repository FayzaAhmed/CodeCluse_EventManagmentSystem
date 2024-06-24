import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainFrame extends JFrame {
    private User currentUser;
    private EventDAO eventDAO;
    private AttendeeDAO attendeeDAO;
    private JTable eventTable;
    private EventTableModel eventTableModel;
    private JTable userEventsTable;
    private EventTableModel userEventTableModel;
    private JTable attendedEventsTable;
    private EventTableModel attendedEventTableModel;
    private JPanel allEventsPanel;
    private JPanel myEventsPanel;
    private JPanel attendedEventsPanel;
    private JLabel allEventsMessage;
    private JLabel myEventsMessage;
    private JLabel attendedEventsMessage;

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        this.eventDAO = new EventDAO();
        this.attendeeDAO = new AttendeeDAO();

        setTitle("Event Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        eventTableModel = new EventTableModel();
        eventTable = new JTable(eventTableModel);

        userEventTableModel = new EventTableModel();
        userEventsTable = new JTable(userEventTableModel);

        attendedEventTableModel = new EventTableModel();
        attendedEventsTable = new JTable(attendedEventTableModel);

        allEventsMessage = createMessageLabel("No events available.");
        myEventsMessage = createMessageLabel("You have not created any events.");
        attendedEventsMessage = createMessageLabel("You are not attending any events.");

        allEventsPanel = createCardPanel(eventTable, allEventsMessage);
        myEventsPanel = createCardPanel(userEventsTable, myEventsMessage);
        attendedEventsPanel = createCardPanel(attendedEventsTable, attendedEventsMessage);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("All Events", allEventsPanel);
        tabbedPane.addTab("My Events", myEventsPanel);
        tabbedPane.addTab("Attending Events", attendedEventsPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Event");
        JButton editButton = new JButton("Edit Event");
        JButton deleteButton = new JButton("Delete Event");
        JButton registerButton = new JButton("Register for Event");
        JButton cancelButton = new JButton("Cancel Registration");
        JButton logoutButton = new JButton("Logout");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addButton.addActionListener(e -> addEvent());

        editButton.addActionListener(e -> editEvent());

        deleteButton.addActionListener(e -> deleteEvent());

        registerButton.addActionListener(e -> registerForEvent());

        cancelButton.addActionListener(e -> cancelRegistration());

        logoutButton.addActionListener(e -> logout());

        loadEvents();
    }

    private JLabel createMessageLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 16));
        return label;
    }

    private JPanel createCardPanel(JTable table, JLabel messageLabel) {
        JPanel panel = new JPanel(new CardLayout()); // Set CardLayout here

        // Create a panel to hold the message label centered at the top
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.add(messageLabel);
        panel.add(messagePanel, "message"); // Add message label with a specific name

        panel.add(new JScrollPane(table), "table"); // Add table with a specific name
        return panel;
    }


    private void showCard(JPanel panel, String cardName) {
        CardLayout cardLayout = (CardLayout) panel.getLayout();
        cardLayout.show(panel, cardName);
    }


    private void loadEvents() {
        List<Event> events = eventDAO.getAllEvents();
        eventTableModel.setEvents(events);
        if (events.isEmpty()) {
            showCard(allEventsPanel, "message");
        } else {
            showCard(allEventsPanel, "table");
        }

        List<Event> userEvents = eventDAO.getEventsByUserId(currentUser.getId());
        userEventTableModel.setEvents(userEvents);
        if (userEvents.isEmpty()) {
            showCard(myEventsPanel, "message");
        } else {
            showCard(myEventsPanel, "table");
        }

        List<Event> attendedEvents = eventDAO.getAttendedEventsByUserId(currentUser.getId());
        attendedEventTableModel.setEvents(attendedEvents);
        if (attendedEvents.isEmpty()) {
            showCard(attendedEventsPanel, "message");
        } else {
            showCard(attendedEventsPanel, "table");
        }
    }

    private void addEvent() {
        JTextField nameField = new JTextField(10);
        JTextField descriptionField = new JTextField(20);
        JTextField dateField = new JTextField(10);
        JTextField timeField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM:SS):"));
        panel.add(timeField);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Please Enter Event Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Event event = new Event();
            event.setName(nameField.getText());
            event.setDescription(descriptionField.getText());
            event.setEventDate(Date.valueOf(dateField.getText()));
            event.setEventTime(Time.valueOf(timeField.getText()));
            event.setCreatorId(currentUser.getId());
            eventDAO.createEvent(event);
            loadEvents();
        }
    }

    private void editEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to edit.");
            return;
        }

        Event event = eventTableModel.getEventAt(selectedRow);
        if (event.getCreatorId() != currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "You can only edit events you created.");
            return;
        }

        JTextField nameField = new JTextField(event.getName(), 20);
        JTextField descriptionField = new JTextField(event.getDescription(), 20);
        JFormattedTextField dateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
        dateField.setText(event.getEventDate().toString());
        JFormattedTextField timeField = new JFormattedTextField(new SimpleDateFormat("HH:mm:ss"));
        timeField.setText(event.getEventTime().toString());

        JPanel panel = new JPanel();
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Time (HH:MM:SS):"));
        panel.add(timeField);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Edit Event Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            event.setName(nameField.getText());
            event.setDescription(descriptionField.getText());
            event.setEventDate(Date.valueOf(dateField.getText()));
            event.setEventTime(Time.valueOf(timeField.getText()));
            eventDAO.updateEvent(event);
            loadEvents();
        }
    }

    private void deleteEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to delete.");
            return;
        }

        Event event = eventTableModel.getEventAt(selectedRow);
        System.out.println(currentUser.getId());
        if (event.getCreatorId() != currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "You can only delete events you created.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this event?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            eventDAO.deleteEvent(event.getId(), currentUser.getId());
            loadEvents();
        }
    }

    private void registerForEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to register.");
            return;
        }

        Event event = eventTableModel.getEventAt(selectedRow);
        if (event.getCreatorId() == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "You cannot register for your own event.");
            return;
        }

        // Check if user is already registered
        if (attendeeDAO.isUserRegisteredForEvent(currentUser.getId(), event.getId())) {
            JOptionPane.showMessageDialog(this, "You are already registered for this event.");
            return;
        }

        Attendee attendee = new Attendee();
        attendee.setEventId(event.getId());
        attendee.setUserId(currentUser.getId());
        attendeeDAO.addAttendee(attendee);
        JOptionPane.showMessageDialog(this, "Successfully registered for the event.");
        loadEvents();
    }

    private void cancelRegistration() {
        int selectedRow = attendedEventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event to cancel registration.");
            return;
        }

        Event event = attendedEventTableModel.getEventAt(selectedRow);
        int eventId = event.getId();

        try {
            attendeeDAO.cancelRegistration(currentUser.getId(), eventId);
            JOptionPane.showMessageDialog(this, "Successfully canceled registration for the event.");
            loadEvents();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cancelling registration for the event.");
        }
    }


    private void logout() {
        this.dispose();
        new LoginFrame(user -> {
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);
        });
    }
}
