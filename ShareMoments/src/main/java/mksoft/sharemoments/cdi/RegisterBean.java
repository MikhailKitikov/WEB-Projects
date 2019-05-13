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
import mksoft.sharemoments.ejb.UserDAO;
import mksoft.sharemoments.entity.User;
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
    private String name;
    private String location;
    private String bio;
    
    private boolean registerSuccess;  
    
    @EJB
    private UserDAO userDAO;
    
    public String register(){
        
        if (!validateInput()) {
            registerSuccess = false;
            return "registration";
        } 
        if (!userDAO.createUser(username, password, name, location, bio)) {
            registerSuccess = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "User already exists"));
            return "registration";
        } 
        
        registerSuccess = true;
        User user = userDAO.userObject(username);
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("username", user);
        context.getExternalContext().getSessionMap().put("currViewUser", user);
        return "profilePage?faces-redirect=true";
    }
    
    public boolean validateInput() {
        
//        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmPassword)) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Invalid input"));
//            return false;
//        }
        if (!password.equals(confirmPassword)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Passwords do not match"));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public RegisterBean() {
    }
    
    // locations
    
    private Map<String,String> locations;
     
    @PostConstruct
    public void init() {
        locations  = new HashMap<>();
        locations.put("Belarus, Minsk", "Belarus, Minsk");
        locations.put("Germany, Berlin", "Germany, Berlin");
        locations.put("USA, New York", "USA, New York");
    }

    public Map<String, String> getLocations() {
        return locations;
    }

    public void setLocations(Map<String, String> locations) {
        this.locations = locations;
    }

}
