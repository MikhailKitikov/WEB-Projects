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
    
    private String password;
    private String confirmPassword;
    private String name;
    private String bio;
    
    @EJB
    private UserService userDAO;
    
    //

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public AlterInfoBean() {
    }
    
    // altering
    
    public void preload() {
        FacesContext context = FacesContext.getCurrentInstance();
        User user = (User)context.getExternalContext().getSessionMap().get("username");
        if (user.getUserData() == null)
            return;
        name = user.getUserData().getName();
        bio = user.getUserData().getBio();
        
    }
    
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
    }
    
    boolean validatePassword() {
        FacesContext context = FacesContext.getCurrentInstance();
        User user = (User)context.getExternalContext().getSessionMap().get("username");
        if (oldPassword == null || password == null || confirmPassword == null)
            return false;
        if (!oldPassword.equals(user.getPassword()))
            return false;
        if (!password.equals(confirmPassword))
            return false;
        return true;
    }

    public void alterPassword() {
        
        if (!validatePassword()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords do not match"));
            return;
        }
        FacesContext context = FacesContext.getCurrentInstance();
        String username = ((User)context.getExternalContext().getSessionMap().get("username")).getUsername();
        userDAO.changePassword(password);
        User user = userDAO.userObject(username);
        context.getExternalContext().getSessionMap().put("username", user);
        context.getExternalContext().getSessionMap().put("currViewUser", user);
    }
}
