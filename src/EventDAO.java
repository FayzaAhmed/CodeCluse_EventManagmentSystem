import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventDAO {
    private Connection connection;

    public EventDAO() {
        connection = DatabaseConnection.getConnection();
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.id, e.name, e.description, e.date, e.time, " +
                "u.username AS creator_username, GROUP_CONCAT(a.username) AS attendees, e.creator_id " +
                "FROM events e " +
                "JOIN users u ON e.creator_id = u.id " +
                "LEFT JOIN (SELECT a.event_id, u.username FROM attendees a JOIN users u ON a.user_id = u.id) a " +
                "ON e.id = a.event_id " +
                "GROUP BY e.id, u.username";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("date"));
                event.setEventTime(rs.getTime("time"));
                event.setCreatorUsername(rs.getString("creator_username"));
                event.setAttendees(rs.getString("attendees"));
                event.setCreatorId(rs.getInt("creator_id")); // Internal use only
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
    /*
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.*, u.username AS creator_username FROM events e JOIN users u ON e.creator_id = u.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("date"));
                event.setEventTime(rs.getTime("time"));
                event.setCreatorId(rs.getInt("creator_id"));
                event.setCreatorUsername(rs.getString("creator_username"));
                event.setAttendees(rs.getString("attendees"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }*/


    public List<Event> getEventsByUserId(int userId) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.id, e.name, e.description, e.date, e.time, " +
                "u.username AS creator_username, GROUP_CONCAT(a.username) AS attendees " +
                "FROM events e " +
                "JOIN users u ON e.creator_id = u.id " +
                "LEFT JOIN (SELECT a.event_id, u.username FROM attendees a JOIN users u ON a.user_id = u.id) a " +
                "ON e.id = a.event_id " +
                "WHERE e.creator_id = ? " +
                "GROUP BY e.id, u.username";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("date"));
                event.setEventTime(rs.getTime("time"));
                event.setCreatorUsername(rs.getString("creator_username"));
                event.setAttendees(rs.getString("attendees"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<Event> getAttendedEventsByUserId(int userId) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.id, e.name, e.description, e.date, e.time, " +
                "u.username AS creator_username, GROUP_CONCAT(a.username) AS attendees " +
                "FROM events e " +
                "JOIN users u ON e.creator_id = u.id " +
                "JOIN attendees at ON e.id = at.event_id " +
                "LEFT JOIN (SELECT a.event_id, u.username FROM attendees a JOIN users u ON a.user_id = u.id) a " +
                "ON e.id = a.event_id " +
                "WHERE at.user_id = ? " +
                "GROUP BY e.id, u.username";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setName(rs.getString("name"));
                event.setDescription(rs.getString("description"));
                event.setEventDate(rs.getDate("date"));
                event.setEventTime(rs.getTime("time"));
                event.setCreatorUsername(rs.getString("creator_username"));
                event.setAttendees(rs.getString("attendees"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public void createEvent(Event event) {
        String query = "INSERT INTO events (name, description, date, time, creator_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getEventDate());
            stmt.setTime(4, event.getEventTime());
            stmt.setInt(5, event.getCreatorId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEvent(Event event) {
        String query = "UPDATE events SET name = ?, description = ?, date = ?, time = ? WHERE id = ? AND creator_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getEventDate());
            stmt.setTime(4, event.getEventTime());
            stmt.setInt(5, event.getId());
            stmt.setInt(6, event.getCreatorId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteEvent(int eventId, int userId) {
        String query = "DELETE FROM events WHERE id = ? AND creator_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
