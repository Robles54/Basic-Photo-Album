package photoalbum.routes;

import io.javalin.config.JavalinConfig;
import io.javalin.http.Context;
import photoalbum.errors.UnauthorizedException;
import photoalbum.service.UserService;

public final class CurrentUser {
    private static final String KEY = "currentUser";
    private final UserService userService;

    // Make constructor public and inject the service
    public CurrentUser(UserService userService) {
        this.userService = userService;
    }

    // Remove static modifier so it becomes an instance method
    public void register(JavalinConfig config) {
        config.routes.beforeMatched(ctx -> {
            String username = ctx.header("X-User");
            if (username == null || username.isBlank()) {
                throw new UnauthorizedException("X-User header is required");
            }
            username = username.trim();
            
            // Check if the user exists in the database
            userService.requireUser(username);
            
            ctx.attribute(KEY, username);
        });
    }

    // get() stays static because it just reads from the request
    public static String get(Context ctx) {
        return ctx.attribute(KEY);
    }
}