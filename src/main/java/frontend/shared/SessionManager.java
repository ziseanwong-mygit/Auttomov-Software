package main.java.frontend.shared;

import main.java.backend.model.StaffAccount;
import main.java.backend.model.UserAccount;
import main.java.enums.data.Role;

/**
 * Holds the currently logged-in user for the duration of the session.
 * All screens reference this instead of passing user objects through constructors.
 */
public class SessionManager {

    private static UserAccount currentUser;

    // Prevent instantiation
    private SessionManager() {}

    public static void setCurrentUser(UserAccount user) {
        currentUser = user;
    }

    public static UserAccount getCurrentUser() {
        return currentUser;
    }

    /**
     * Derives the current user's Role from their type.
     * Used because UserAccount has no public getUserRole() getter.
     */
    public static Role getCurrentRole() {
        return (currentUser instanceof StaffAccount) ? Role.STAFF : Role.CUSTOMER;
    }

    public static void clearSession() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}

