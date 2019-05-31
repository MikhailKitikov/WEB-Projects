package mksoft.sharemoments.cdi;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.entity.User;
import mksoft.sharemoments.ejb.UserService;
import mksoft.sharemoments.entity.UserData;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "logBean", eager = true)
@SessionScoped
public class LoginBean implements Serializable {
    
    private static final long serialVersionUID = 1L;        
    private String username;
    private String password;
    private boolean loginSuccess;

    @EJB
    private UserService userDAO;

    public String login() {        
        if (!validateInput()) {
            loginSuccess = false;
            return "authorization";
        } 
        int res = userDAO.checkPassword(username, password);          
        if (res != 0) {
            loginSuccess = false;
            String msg = (res == 1 ? "User not found" : "Incorrect password");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", msg));
            return "authorization";
        }
        loginSuccess = true;
        boolean isAdmin = ("admin".equals(username));
        User user = userDAO.userObject(username);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("username", user);
        context.getExternalContext().getSessionMap().put("currViewUser", user);
        context.getExternalContext().getSessionMap().put("admin", isAdmin);
        return (isAdmin ? "adminPage?faces-redirect=true" : "profilePage?faces-redirect=true");
    }
    
    public boolean validateInput() {        
        if (username == null || password == null || StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid input"));
            return false;
        }
        return true;
    }
    
    public void logout() throws IOException {

        if (!loginSuccess) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "!", "You are not logged in"));
        }
        loginSuccess = false;
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }
    
    
    //    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }
    
    public LoginBean (){
    }

}