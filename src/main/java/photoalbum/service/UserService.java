package photoalbum.service;

import photoalbum.dao.UserDao;
import photoalbum.errors.UnauthorizedException;

public class UserService {
    private final UserDao dao;

    public UserService(UserDao dao) {
        this.dao = dao;
    }

    // Throws UnauthorizedException if the user doesn't exist
    public void requireUser(String username) {
        if (!dao.exists(username)) {
            throw new UnauthorizedException("User " + username + " does not exist");
        }
    }
}