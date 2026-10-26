package photoalbum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import photoalbum.db.DB;

public class UserDao {
    // Returns true if the username exists in the USER table.
    public boolean exists(String username) {
        String sql = "SELECT 1 FROM USER WHERE username = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // True if a row is found
            }
        } catch (SQLException e) {
            throw SqlErrors.translate(e);
        }
    }
}