package logic;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author mk99
 */
@Named(value = "userBean")
@SessionScoped
public class UserBean implements Serializable {

    private String login;
    private String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    private Map<String, String> profiles;
    
    public UserBean() {
        profiles = new HashMap<>();
        profiles.put("admin", "admin");
    }
    
    public String display() {
        return "display";
    }
    
    public String showResult() {
        if (login == null || password == null) {
            return "index";
        }
        if (!profiles.containsKey(login)) {
            return "User not found :(";
        }
        if (!profiles.get(login).equals(password)) {
            return "Incorrect password :(";
        }
        else {
            return "You are welcome, " + login;
        }
    }
    
}
