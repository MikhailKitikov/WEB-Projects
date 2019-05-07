package mksoft.sharemoments.cdi;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.ejb.CommentDAO;
import mksoft.sharemoments.ejb.PhotoPostDAO;
import mksoft.sharemoments.ejb.UserDAO;
import mksoft.sharemoments.entity.Comment;
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
        currentUserPhotoPosts = photoPostDAO.getCurrentUserPhotoPosts();
        return currentUserPhotoPosts;
    }
    
    public List<PhotoPost> getAllPhotoPosts() {
        return photoPostDAO.getAllPhotoPosts();
    }
    
    // ///// for comments
    
    @EJB
    private CommentDAO commentDAO;
    
    private List<Comment> currentPostComments;
    
    public List<Comment> getCurrentPostComments() {
        currentPostComments = commentDAO.getCurrentPostComments(currentUserPhotoPosts.get(currImageIndex).getId());
        return currentPostComments;
    }    
    
    private boolean renderComments = false;

    public boolean isRenderComments() {
        return renderComments;
    }

    public void setRenderComments(boolean renderComments) {
        this.renderComments = renderComments;
    }
    
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public void createComment() {
        String author = (String)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser");
        Integer postID = currentUserPhotoPosts.get(currImageIndex).getId();
        commentDAO.createComment(author, postID, text);
    }    
    
    //
    
    public void setCurrentImage(Integer id) {
        
        renderComments = true;
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
    
    public void prevImage() {
        --currImageIndex;
        currSrc = currentUserPhotoPosts.get(currImageIndex).getSrc();
    }
    
    public String nextImageAvailable() {
        return (currImageIndex >= currentUserPhotoPosts.size() ? "display: none;" : "display: block;");
    }
    
    public String prevImageAvailable() {
        return (currImageIndex < 0 ? "display: none;" : "display: block;");
    }
    
}
