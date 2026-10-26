package photoalbum.dto;

import photoalbum.errors.ValidationException;

public class NewPhoto {
	public String url;
	public String caption;
	
	public void validate() {
		if (url == null || url.isBlank()) {
			throw new ValidationException("URL is missing or blank");
		}
		url = url.trim();
		
		if (url.length() > 500) {
			throw new ValidationException("URL cannot be over 500 characters");
		}
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new ValidationException("URL must start with http:// or https://");
        }
		if (caption != null && caption.length() > 200) {
            throw new ValidationException("Caption cannot be over 200 characters");
        }
	}

}
