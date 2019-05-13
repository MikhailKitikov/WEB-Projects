package mksoft.sharemoments.cdi;

import java.io.IOException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import mksoft.sharemoments.ejb.CommentDAO;
import mksoft.sharemoments.ejb.PhotoPostDAO;
import mksoft.sharemoments.ejb.PostLikeDAO;
import mksoft.sharemoments.ejb.UserDAO;
import mksoft.sharemoments.entity.Comment;
import mksoft.sharemoments.entity.PhotoPost;
import static mksoft.sharemoments.entity.PhotoPost_.id;
import mksoft.sharemoments.entity.PostLike;
import mksoft.sharemoments.entity.User;
import mksoft.sharemoments.entity.UserData;
import org.eclipse.persistence.jpa.jpql.Assert;
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
        return ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
    }
    
    public String showName() {
        UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUserData();
        if (userData == null)
            return "";
        return userData.getName();
    }
    
    public String showLocation() {
        UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUserData();
        if (userData == null)
            return "";
        return userData.getLocation();
    }
    
    public String showBio() {
        UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUserData();
        if (userData == null)
            return "";
        return userData.getBio();
    }
    
    public String showAvatar() {
        UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUserData();
        if (userData == null)
            return "";
        return userData.getAvatar();
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
    
    // likes
    
    @EJB
    private PostLikeDAO postLikeDAO;
    
    private static List<String> currentPostLikes;
    
    public List<String> getCurrentPostLikes() {
        if (currImageIndex < 0) return null;
        currentPostLikes = postLikeDAO.getCurrentPostLikes(currentUserPhotoPosts.get(currImageIndex).getId());
        return currentPostLikes;
    }    
   
    public void addLike() {
        
        if (currentPostLikes == null) 
            return;
        
        try {
            String author = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
            Integer postID = currentUserPhotoPosts.get(currImageIndex).getId();        
            if (currentPostLikes.contains(showUsername())) {
                postLikeDAO.removeLike(author, postID);
            }
            else {
                postLikeDAO.addLike(author, postID);
            }            
            RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
        }
        catch (EJBException e) {
            @SuppressWarnings("ThrowableResultIgnored")
            Exception cause = e.getCausedByException();
            if (cause instanceof ConstraintViolationException) {
                @SuppressWarnings("ThrowableResultIgnored")
                ConstraintViolationException cve = (ConstraintViolationException) e.getCausedByException();
                for (Iterator<ConstraintViolation<?>> it = cve.getConstraintViolations().iterator(); it.hasNext();) {
                    ConstraintViolation<? extends Object> v = it.next();
                    System.err.println(v);
                    System.err.println("==>>"+v.getMessage());
                }
            }
            Assert.fail("ejb exception");
        }    
    }
    
    // ///// for comments
    
    @EJB
    private CommentDAO commentDAO;
    
    private static List<Comment> currentPostComments;
    
    public List<Comment> getCurrentPostComments() {
        if (currImageIndex < 0) return null;
        currentPostComments = commentDAO.getCurrentPostComments(currentUserPhotoPosts.get(currImageIndex).getId());
        renderComments1 = true;
        getCurrentPostLikes();
        RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
        return currentPostComments;
    }
    
     
    public int likesCount() {
        return currentPostLikes.size();
    }
    
    public String likeState() {
        return (currentPostLikes.contains(((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername()) ? "dislike" : "like");
    }
    

    public String currentPostText() {
        return currentUserPhotoPosts.get(currImageIndex).getText();
    }
    
    private static boolean renderComments = false;

    public boolean isRenderComments() {
        return renderComments;
    }

    public void setRenderComments(boolean renderComments) {
        ProfileViewBean.renderComments = renderComments;
    }
    
    private static boolean renderComments1 = false;

    public boolean isRenderComments1() {
        return renderComments1;
    }

    public void setRenderComments1(boolean renderComments1) {
        ProfileViewBean.renderComments1 = renderComments1;
    }
    
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public void createComment() {
        
        try {
            String author = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
            Integer postID = currentUserPhotoPosts.get(currImageIndex).getId();
            commentDAO.createComment(author, postID, text);
            List<Comment> f = getCurrentPostComments();
            List<String> g = getCurrentPostLikes();
            RequestContext.getCurrentInstance().update("commentsFormID:tablePG");
        }
        catch (EJBException e) {
            @SuppressWarnings("ThrowableResultIgnored")
            Exception cause = e.getCausedByException();
            if (cause instanceof ConstraintViolationException) {
                @SuppressWarnings("ThrowableResultIgnored")
                ConstraintViolationException cve = (ConstraintViolationException) e.getCausedByException();
                for (Iterator<ConstraintViolation<?>> it = cve.getConstraintViolations().iterator(); it.hasNext();) {
                    ConstraintViolation<? extends Object> v = it.next();
                    System.err.println(v);
                    System.err.println("==>>"+v.getMessage());
                }
            }
            Assert.fail("ejb exception");
        }       
    }    
    
    public int commentsCount() {
        return currentPostComments.size();
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
        RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
    }
    
    public void nextImage() {
        ++currImageIndex;
        currSrc = currentUserPhotoPosts.get(currImageIndex).getSrc();
        RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
    }
    
    public void prevImage() {
        --currImageIndex;
        currSrc = currentUserPhotoPosts.get(currImageIndex).getSrc();
        RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
    }
    
    public String nextImageAvailable() {
        String st = "position: fixed; right: 380px; height: 10px; width: 10px; font: 10pt; top: 0; bottom: 0; margin-top: auto; margin-bottom: auto;";
        return (currImageIndex == currentUserPhotoPosts.size() - 1 ? "display: none;" + st : "display: block;" + st);
    }
    
    public String prevImageAvailable() {
        String st = "position: fixed; left: 80px; height: 10px; width: 10px; font: 10pt; top: 0; bottom: 0; margin-top: auto; margin-bottom: auto;";
        return (currImageIndex == 0 ? "display: none;"+ st : "display: block;"+ st);
    }
    
}
