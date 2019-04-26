package mksoft.sharemoments.cdi;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.ejb.PhotoPostDAO;
import mksoft.sharemoments.entity.PhotoPost;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "profileViewBean", eager = true)
@SessionScoped
public class ProfileViewBean implements Serializable {
    
    @EJB
    private PhotoPostDAO photoPostDAO;
    
    public String showUsername() {
        
//        FacesContext facesContext = FacesContext.getCurrentInstance();
//        HttpSession session = (HttpSession)facesContext.getExternalContext().getSession(true);
//        return (String)session.getAttribute("username");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (String)facesContext.getExternalContext().getSessionMap().get("username");
        //FacesContext context = FacesContext.getCurrentInstance();
        //LoginBean logBean = (LoginBean) context.getApplication().evaluateExpressionGet(context, "#{logBean}", LoginBean.class);
    }
    
    public List<PhotoPost> getPhotoPosts() {
        //return null;
        return photoPostDAO.getCurrentUserAllPosts();
    }

    public ProfileViewBean() {
    }   
    
}
