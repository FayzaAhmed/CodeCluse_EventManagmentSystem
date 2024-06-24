import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class EventTableModel extends AbstractTableModel {
    private String[] columnNames = {"Name", "Description", "Date", "Time", "Creator", "Attendees"};
    private List<Event> events;

    public EventTableModel() {
        events = new ArrayList<>();
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        fireTableDataChanged();
    }

    public Event getEventAt(int rowIndex) {
        return events.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return events.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Event event = events.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return event.getName();
            case 1:
                return event.getDescription();
            case 2:
                return event.getEventDate();
            case 3:
                return event.getEventTime();
            case 4:
                return event.getCreatorUsername();
            case 5:
                return event.getAttendees();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
