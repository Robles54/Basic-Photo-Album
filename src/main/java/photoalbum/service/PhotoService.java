package photoalbum.service;
import java.util.List;
import java.util.Optional;
import photoalbum.dao.PhotoDao;
import photoalbum.domain.Photo;
import photoalbum.errors.NotFoundException;
import photoalbum.dto.NewPhoto;
import photoalbum.dto.NewCaption;
import photoalbum.dao.KeywordDao;
/**
* The rules for working with photos.
*
* Knows nothing about HTTP: it takes plain values and returns domain objects,
* and reports failures by throwing the application's exceptions.
*/
public class PhotoService {
	private final PhotoDao dao;
	private final KeywordDao keywordDao;
	public PhotoService(PhotoDao dao, KeywordDao keywordDao) {
		this.dao = dao;
		this.keywordDao = keywordDao;
	}
	// One of username's photos. NotFoundException if there's no such
	// photo or it belongs to another user.
	public Photo getPhoto(int id, String username) {
		Optional<Photo> photo = dao.findById(id, username);
		if (photo.isEmpty()) {
			throw new NotFoundException("No photo with id " + id);
		}
		return photo.get();
	}
	
	public List <Photo> listPhotos(String username){
		return dao.findAll(username);
	}
	
	public List <Photo> listPhotosByKeywords (String keyword, String username){
		return dao.findByKeyword(keyword, username);
	}
	
	public Photo addPhoto (NewPhoto newPhoto, String username) {
		newPhoto.validate();
		return dao.insert(newPhoto, username);
	}
	
	public void deletePhoto(int id, String username) {
	    boolean deleted = dao.delete(id, username);
	    if (!deleted) {
	        throw new NotFoundException("No photo with id " + id + " found to delete.");
	    }
	}
	
	public List<String> listKeywords (int id, String username){
		getPhoto(id, username);
		
		return keywordDao.findByPhoto(id);
	}
	
	public Photo setCaption(int id, NewCaption newCaption, String username) {
		newCaption.validate();
		Photo existingPhoto = getPhoto(id, username);
		
		dao.updateCaption(id, newCaption.caption, username);
		
		return new Photo(existingPhoto.id(), existingPhoto.url(), newCaption.caption);
	}
}