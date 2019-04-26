package mksoft.sharemoments.cdi;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import mksoft.sharemoments.ejb.UserDAO;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "regBean", eager = true)
@SessionScoped
public class RegisterBean implements Serializable {
    
    private String username;
    private String password;
    private String confirmPassword;
    
    private boolean registerSuccess;  
    
    @EJB
    private UserDAO userDAO;
    
    public String register(){
        
        if (!validateInput()) {
            registerSuccess = false;
            return "registration";
        } 
        if (!userDAO.createUser(username, password)) {
            registerSuccess = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User already exists"));
            return "registration";
        } 
        registerSuccess = true;
        return "display?faces-redirect=true";
    }
    
    public boolean validateInput() {
        
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid input"));
            return false;
        }
        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords does not match"));
            return false;
        }
        return true;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public boolean isRegisterSuccess() {
        return registerSuccess;
    }

    public void setRegisterSuccess(boolean registerSuccess) {
        this.registerSuccess = registerSuccess;
    }
    
    public RegisterBean() {
    }
}
