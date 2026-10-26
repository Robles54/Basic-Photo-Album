package photoalbum.routes;
import static photoalbum.routes.Params.pathId;
import photoalbum.dto.NewCaption;
import java.util.List;
import photoalbum.dto.NewPhoto;
import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import photoalbum.domain.Photo;
import photoalbum.service.PhotoService;
/**
* The HTTP side of the photo endpoints.
*
* Each handler reads the request, calls the service, and writes the response.
* No SQL and no rules here; failures are exceptions that ErrorHandler turns
* into status codes.
*/
public class PhotoRoutes {
	private final PhotoService service;
	public PhotoRoutes(PhotoService service) {
		this.service = service;
	}
	
	// Called from inside Javalin.create(config -> { ... })
	public void register(JavalinConfig config) {
		config.routes.get("/photos/{id}", ctx -> getPhoto(ctx));
		// Adding new routes
		config.routes.get("/photos", ctx -> listPhotos(ctx));
		config.routes.post("/photos", ctx -> addPhoto(ctx));
		config.routes.put("/photos/{id}/caption", ctx -> setCaption(ctx));
		config.routes.delete("/photos/{id}", ctx -> deletePhoto(ctx));
		config.routes.get("/photos/{id}/keywords", ctx -> listKeywords(ctx));
	}
	
	private void listPhotos(Context ctx) {
		String username = CurrentUser.get(ctx);
		String keyword = ctx.queryParam("keyword");
		List<Photo> photos;
		
		if (keyword != null && !keyword.isBlank()) {
			photos = service.listPhotosByKeywords(keyword, username);
		} else {
			photos = service.listPhotos(username);
		}
		
		ctx.json(photos);
	}

	
	private void addPhoto(Context ctx) {
		String username = CurrentUser.get(ctx);
		NewPhoto newPhoto = ctx.bodyAsClass(NewPhoto.class);
		
		Photo createdPhoto = service.addPhoto(newPhoto, username);
		
		ctx.status(201);
		ctx.header("Location", "/photos/" + createdPhoto.id());
		ctx.json(createdPhoto);
	}
	
	private void setCaption(Context ctx) {
		int id = pathId (ctx);
		String username = CurrentUser.get(ctx);
		NewCaption newCaption = ctx.bodyAsClass(NewCaption.class);
		
		Photo updatedPhoto = service.setCaption(id, newCaption, username);
		ctx.json(updatedPhoto);
	}
	
	private void deletePhoto(Context ctx) {
		int id = pathId(ctx);
		String username = CurrentUser.get(ctx);
		
		service.deletePhoto(id, username);
		// 204 means no content, which means it was deleted
		ctx.status(204);
	}
	
	private void listKeywords(Context ctx) {
		int id = pathId(ctx);
		String username = CurrentUser.get(ctx);
		
		List<String> keywords = service.listKeywords(id, username);
		ctx.json(keywords);
	}
	
	// GET /photos/{id} -> 200 and the photo
	private void getPhoto(Context ctx) {
		int id = pathId(ctx);
		String username = CurrentUser.get(ctx);
		Photo photo = service.getPhoto(id, username);
		ctx.json(photo);
	}
	
	
}