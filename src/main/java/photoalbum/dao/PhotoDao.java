package photoalbum.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import photoalbum.db.DB;
import photoalbum.domain.Photo;
import photoalbum.dto.NewPhoto;
/**
* All of the SQL for the ALBUM table.
*
* Every lookup includes the owner, so a photo that belongs to someone else is
* indistinguishable from one that doesn't exist.
*/
public class PhotoDao {
	
	public static void main(String[] args) {
		System.out.println(new PhotoDao().findById(1, "arthur"));
	}
// The photo with this id in this user's album, or empty if there
// is none.
public Optional<Photo> findById(int id, String username) {
	String sql = "SELECT id, url, caption FROM ALBUM WHERE id = ? " +
			"AND username = ?";
	try (Connection conn = DB.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
		stmt.setInt(1, id);
		stmt.setString(2, username);
		try (ResultSet rs = stmt.executeQuery()) {
			if (rs.next()) {
				return Optional.of(toPhoto(rs));
			}
			return Optional.empty();
		}
	} catch (SQLException e) {
		throw SqlErrors.translate(e);
	}
}

public List<Photo> findAll (String username){
	String sql = "SELECT id, url, caption FROM ALBUM WHERE username = ? ORDER BY id";
	List<Photo> photos = new ArrayList<>();
	
	try (Connection conn = DB.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)) {
		stmt.setString(1, username);
		try (ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				photos.add(toPhoto(rs));
			}
		}
	} catch (SQLException e) {
		throw SqlErrors.translate(e);
	}
	return photos;
}

public List<Photo> findByKeyword (String keyword, String username){
	String sql = "SELECT ALBUM.id, url, caption FROM ALBUM " +
	        "JOIN KEYWORD ON ALBUM.id = KEYWORD.id" +
	        "WHERE username = ? AND keyword = ? ORDER BY ALBUM.id";
	List<Photo> photos = new ArrayList<>();
	
	try (Connection conn = DB.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql)){
		
		stmt.setString(1, username);
		stmt.setString(2, keyword);
		try (ResultSet rs = stmt.executeQuery()){
			while (rs.next()) {
				photos.add(toPhoto(rs));
			}
		}
	} catch (SQLException e) {
		throw SqlErrors.translate(e);
	}
	return photos;
}

public Photo insert (NewPhoto newPhoto, String username) {
	String sql = "INSERT INTO ALBUM (username, url, caption) VALUES (?, ?, ?)";
	
	try (Connection conn = DB.getConnection();
			PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
		
		stmt.setString(1, username);
		stmt.setString(2, newPhoto.url);
		stmt.setString(3, newPhoto.caption);
		stmt.executeUpdate();
		
		try (ResultSet rs = stmt.getGeneratedKeys()){
			if (rs.next()) {
				int id = rs.getInt(1);
				return new Photo(id, newPhoto.url, newPhoto.caption);
			}
			throw new SQLException("Failed to grab ID");
		}
	} catch (SQLException e) {
		throw SqlErrors.translate(e);
	}
}

public void updateCaption(int id, String caption, String username) {
    String sql = "UPDATE ALBUM SET caption = ? WHERE id = ? AND username = ?";
    
    try (Connection conn = DB.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, caption);
        stmt.setInt(2, id);
        stmt.setString(3, username);
        
        stmt.executeUpdate();
    } catch (SQLException e) {
        throw SqlErrors.translate(e);
    }
}

public boolean delete(int id, String username) {
    String sql = "DELETE FROM ALBUM WHERE id = ? AND username = ?";
    
    try (Connection conn = DB.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        stmt.setString(2, username);
        
        // executeUpdate() returns the number of rows affected. 
        // If it's > 0, the photo existed and was deleted.
        return stmt.executeUpdate() > 0;
        
    } catch (SQLException e) {
        throw SqlErrors.translate(e);
    }
}

// The current row of rs as a Photo. getString returns null for a
// NULL caption.
private Photo toPhoto(ResultSet rs) throws SQLException {
	return new Photo(rs.getInt("id"), rs.getString("url"), rs.getString("caption"));
	}
}