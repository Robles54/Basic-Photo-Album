package photoalbum.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import photoalbum.db.DB;

public class KeywordDao {
    public List<String> findByPhoto(int id) {
        String sql = "SELECT keyword FROM KEYWORD WHERE id = ? ORDER BY keyword";
        List<String> keywords = new ArrayList<>();
        
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    keywords.add(rs.getString("keyword"));
                }
            }
        } catch (SQLException e) {
            throw SqlErrors.translate(e);
        }
        return keywords;
    }
}