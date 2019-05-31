package mksoft.sharemoments.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import mksoft.sharemoments.ejb.UserService;
import mksoft.sharemoments.entity.User;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "alterInfoBean", eager = true)
@SessionScoped
public class AlterInfoBean implements Serializable {
    
    private static String password;
    private static String confirmPassword;
    private static String name;
    private static String bio;
    
    @EJB
    private UserService userDAO;
    
    //

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        AlterInfoBean.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        AlterInfoBean.confirmPassword = confirmPassword;
    }

    public String getName() {
        FacesContext context = FacesContext.getCurrentInstance();
        User user = (User)context.getExternalContext().getSessionMap().get("username");
        name = user.getUserData().getName();
        return name;
    }

    public void setName(String name) {
        AlterInfoBean.name = name;
    }

    public String getBio() {
        FacesContext context = FacesContext.getCurrentInstance();
        User user = (User)context.getExternalContext().getSessionMap().get("username");
        bio = user.getUserData().getBio();
        return bio;
    }

    public void setBio(String bio) {
        AlterInfoBean.bio = bio;
    }
    
    public AlterInfoBean() {
    }
    
    // altering
    
    private String oldPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }
    
    public void alterInfo() {
        FacesContext context = FacesContext.getCurrentInstance();
        String uname = ((User)context.getExternalContext().getSessionMap().get("username")).getUsername();
        userDAO.changeInfo(name, bio);
        User user = userDAO.userObject(uname);
        context.getExternalContext().getSessionMap().put("username", user);
        context.getExternalContext().getSessionMap().put("currViewUser", user);
        context.addMessage("alter-info-form", new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Data changed successfully!"));
    }
    
    boolean validatePassword() {
        FacesContext context = FacesContext.getCurrentInstance();
        User user = (User)context.getExternalContext().getSessionMap().get("username");
        if (oldPassword == null || password == null || confirmPassword == null || 
                oldPassword.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Empty password"));
            return false;
        }            
        if (!oldPassword.equals(user.getPassword())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Incorrect password"));
            return false;
        }        
        if (!password.equals(confirmPassword)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords do not match"));
            return false;
        }        
        return true;
    }

    public void alterPassword() {
        
        if (!validatePassword()) {
            return;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        String username = ((User)context.getExternalContext().getSessionMap().get("username")).getUsername();
        userDAO.changePassword(password);
        User user = userDAO.userObject(username);
        context.getExternalContext().getSessionMap().put("username", user);
        context.getExternalContext().getSessionMap().put("currViewUser", user);
        context.addMessage("alter-pswrd-form", new FacesMessage(FacesMessage.SEVERITY_INFO, "OK", "Password changed successfully!"));
    }
}
