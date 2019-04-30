/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import mksoft.sharemoments.ejb.UserDAO;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "searchBean", eager = true)
@javax.faces.bean.SessionScoped
public class SearchBean implements Serializable {
    
    @EJB
    private UserDAO userDAO;
    
//    public String searchUser(String name)
//    {
//        return userDAO.searchUser(name);
//    }
    
    private String username; 

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
 
    // Method To Display List On The JSF Page
    public List<String> userList() {         
        List<String> resList = userDAO.getAllUsernames();
        Collections.sort(resList);
        return resList;  
    }
    
    public void onItemSelect(SelectEvent event) throws IOException {
//        String a = (String)event.getObject();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().put("currViewUser", username);
        FacesContext.getCurrentInstance().getExternalContext().redirect("profilePage.xhtml");
    }

    public SearchBean() {
    }
    
}
