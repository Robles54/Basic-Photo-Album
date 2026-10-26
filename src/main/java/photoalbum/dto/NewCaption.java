package photoalbum.dto;

import photoalbum.errors.ValidationException;

public class NewCaption {
    public String caption;

    public void validate() {
        if (caption != null && caption.length() > 200) {
            throw new ValidationException("Caption cannot be over 200 characters");
        }
        
        // If the caption is explicitly an empty string, set it to null 
        // so the database registers it as a cleared caption.
        if (caption != null && caption.isEmpty()) {
            caption = null;
        }
    }
}