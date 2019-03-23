package mksoft.testjsf;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author mk99
 */
@Named(value = "testBean")
@SessionScoped
public class TestBean implements Serializable {

    private String login;
    private String password;
    private boolean status;

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
    
    public TestBean() {
        status = false;
    }
    
    public String display() {
        return "display";
    }
    
    public String showResult() throws ClassNotFoundException, SQLException 
    {      
        if (login == null || password == null) {
            return "index";
        }
        
        UserBean userBean = new UserBean();
        int res = userBean.findUser(login, password);
        System.out.print(res);
        
        switch (res) {
            case -1:
                status = false;
                return "User not found :(";
            case 0:
                status = false;
                return "Incorrect password :(";
            default:
                status = true;
                return "You are welcome, " + login + "!";
        }
    }
    
    public String viewUsers()
    {
        return (status ? "user" : "display");
    }
    
}