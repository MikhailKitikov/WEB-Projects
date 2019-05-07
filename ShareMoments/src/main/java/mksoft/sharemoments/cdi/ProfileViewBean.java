package mksoft.sharemoments.cdi;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.ejb.PhotoPostDAO;
import mksoft.sharemoments.ejb.UserDAO;
import mksoft.sharemoments.entity.PhotoPost;
import static mksoft.sharemoments.entity.PhotoPost_.id;
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
    
    int offsetX = 100, offsetY = 220;
    int vert = 0, hor = 0;
    int lastTop, lastLeft;
    
    public String sz() {
        lastTop = offsetY + vert * 220;
        lastLeft = offsetX + hor * 220;
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
        return String.format("top: %dpx;", offsetY + vert * 220);
    }
    
    private static String currSrc;

    public String getCurrSrc() {
        return currSrc;
    }

    public void setCurrSrc(String currSrc) {
        ProfileViewBean.currSrc = currSrc;
    }
    
    private static int currImageIndex = -1;

    public int getCurrImageIndex() {
        return currImageIndex;
    }

    public void setCurrImageIndex(int currImageIndex) {
        ProfileViewBean.currImageIndex = currImageIndex;
    }
    
    private List<PhotoPost> currentUserPhotoPosts;
    
    public List<PhotoPost> getCurrentUserPhotoPosts() {
        vert = 0; hor = 0; offsetY = 220;
        currentUserPhotoPosts = photoPostDAO.getCurrentUserPhotoPosts();
        return currentUserPhotoPosts;
    }
    
    public List<PhotoPost> getAllPhotoPosts() {
        vert = 0; hor = 0; offsetY = 100;
        return photoPostDAO.getAllPhotoPosts();
    }
    
    //
    
    public void setCurrentImage(Integer id) {
        
        currImageIndex = -1;
        for (int i = 0; i < currentUserPhotoPosts.size(); ++i) {
            if (currentUserPhotoPosts.get(i).getId().intValue() == id.intValue()) {
                currImageIndex = i;
                break;
            }
        }
        currSrc = currentUserPhotoPosts.get(currImageIndex).getSrc();
    }
    
    public void nextImage() {
        ++currImageIndex;
        currSrc = currentUserPhotoPosts.get(currImageIndex).getSrc();
    }
    
    public ProfileViewBean() {}
    
}
