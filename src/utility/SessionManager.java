package utility;
import java.util.Date;

import domain.User;


public class SessionManager {
    private static volatile SessionManager instance;
    private User currentUser;
    private Date loginTime;

    private SessionManager() {}


    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }


    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) {
    this.currentUser = user;
    this.loginTime = new Date();
    }

    public void clearSession() {
    currentUser = null;
    loginTime = null;
    }
    public boolean isLoggedIn() { return currentUser != null; }
    public Date getLoginTime() { return loginTime; }
}

