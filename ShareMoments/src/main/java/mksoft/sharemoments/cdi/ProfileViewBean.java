package mksoft.sharemoments.cdi;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.ejb.PhotoPostDAO;
import mksoft.sharemoments.ejb.UserDAO;
import mksoft.sharemoments.entity.PhotoPost;
import org.primefaces.context.RequestContext;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "profileViewBean", eager = true)
@SessionScoped
public class ProfileViewBean implements Serializable {
    
    @EJB
    private PhotoPostDAO photoPostDAO;
    
    private String currViewUser;

    public String getCurrViewUser() {
        return currViewUser;
    }

    public void setCurrViewUser(String currViewUser) {
        this.currViewUser = currViewUser;
    }
    
    public String showUsername() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return (String)facesContext.getExternalContext().getSessionMap().get("username");
    }

    public String showId(String id) {
        return "post" + id;
    }
    
    int offsetX = 180, offsetY = 220;
    int vert = 0, hor = 0;
    int lastTop, lastLeft;
    
    public String sz() {
        lastTop = offsetY + vert * 160;
        lastLeft = offsetX + hor * 160;
        String res = String.format("top: %dpx; left: %dpx;", lastTop, lastLeft);
        if (++hor == 3) {
            hor = 0;
            ++vert;
        }
        return res;
    }
    
    public String shadowSz() {
        String res = String.format("top: %dpx; left: %dpx;", lastTop, lastLeft);
        return res;
    }
    
    public String footerTop() {
        if (hor > 0) ++vert;
        return String.format("top: %dpx;", offsetY + vert * 160);
    }
    
    public List<PhotoPost> getCurrentUserPhotoPosts() {
        vert = 0; hor = 0; offsetY = 220;
        return photoPostDAO.getCurrentUserPhotoPosts();
    }
    
    public List<PhotoPost> getAllPhotoPosts() {
        vert = 0; hor = 0; offsetY = 100;
        return photoPostDAO.getAllPhotoPosts();
    }

    public ProfileViewBean() {
    }   
    
}
