import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendeeDAO {
    private Connection connection;

    public AttendeeDAO() {
        connection = DatabaseConnection.getConnection();
    }

    public void addAttendee(Attendee attendee) {
        String query = "INSERT INTO attendees (event_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, attendee.getEventId());
            stmt.setInt(2, attendee.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserRegisteredForEvent(int userId, int eventId) {
        String query = "SELECT COUNT(*) FROM attendees WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public List<Attendee> getAttendeesByEventId(int eventId) {
        List<Attendee> attendees = new ArrayList<>();
        String query = "SELECT * FROM attendees WHERE event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Attendee attendee = new Attendee();
                attendee.setId(rs.getInt("id"));
                attendee.setEventId(rs.getInt("event_id"));
                attendee.setUserId(rs.getInt("user_id"));
                attendees.add(attendee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendees;
    }

    public void removeAttendee(int attendeeId) {
        String query = "DELETE FROM attendees WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, attendeeId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelRegistration(int userId, int eventId) throws SQLException {
        String query = "DELETE FROM attendees WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        }
    }

}
