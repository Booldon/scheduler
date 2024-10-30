package com.example.scheduler.config.context;

import org.springframework.security.core.userdetails.UserDetails;

public class ResourceContext {

    private static final ThreadLocal<UserDetails> userContext = new ThreadLocal<>();

    public static void setUserContext(UserDetails userDetails) {
        userContext.set(userDetails);
    }

    public static UserDetails getUserContext() {
        return userContext.get();
    }

    public static void clear() {
        userContext.remove();
    }
}
