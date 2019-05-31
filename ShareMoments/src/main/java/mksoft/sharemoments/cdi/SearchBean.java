package mksoft.sharemoments.cdi;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.ejb.UserService;
import mksoft.sharemoments.entity.User;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "searchBean", eager = true)
@SessionScoped
public class SearchBean implements Serializable {
    
    @EJB
    private UserService userDAO;

    private String username; 

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
 
    public List<String> userList(String query) {
        List<String> resList = userDAO.searchUser(query);
        if (resList.contains("admin")) 
            resList.remove("admin");
        Collections.sort(resList);
        return resList;  
    }
    
    public void onItemSelect(SelectEvent event) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        User user = userDAO.userObject(username);
        context.getExternalContext().getSessionMap().put("currViewUser", user);
        FacesContext.getCurrentInstance().getExternalContext().redirect("profilePage.xhtml");
    }
    
    public void goHome() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("currViewUser", (User)context.getExternalContext().getSessionMap().get("username"));
        FacesContext.getCurrentInstance().getExternalContext().redirect("profilePage.xhtml");
    }

    public SearchBean() {
    }
    
}
