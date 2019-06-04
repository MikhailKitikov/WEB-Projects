package mksoft.sharemoments.cdi;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import mksoft.sharemoments.ejb.CommentService;
import mksoft.sharemoments.ejb.EventService;
import mksoft.sharemoments.ejb.FollowerService;
import mksoft.sharemoments.ejb.PhotoPostService;
import mksoft.sharemoments.ejb.PostLikeService;
import mksoft.sharemoments.entity.Comment;
import mksoft.sharemoments.entity.Event;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.User;
import mksoft.sharemoments.entity.UserData;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.primefaces.context.RequestContext;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "newsBean", eager = true)
@SessionScoped
public class NewsBean implements Serializable {    
        
    // main info
    
    public String showUsername() {
        return ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername();
    }

    public String showId(String id) {
        return "post" + id;
    }
    
    
    // photoposts
    
    @EJB
    private PhotoPostService photoPostDAO;
    
    private static String currSrc;

    public String getCurrSrc() {
        return currSrc;
    }

    public void setCurrSrc(String currSrc) {
        NewsBean.currSrc = currSrc;
    }
    
    private static int currImageIndex = -1;

    public int getCurrImageIndex() {
        return currImageIndex;
    }

    public void setCurrImageIndex(int currImageIndex) {
        NewsBean.currImageIndex = currImageIndex;
    }
    
    private List<PhotoPost> currentUserNews;
    
    public List<PhotoPost> getCurrentUserNews() {
        currentUserNews = photoPostDAO.getCurrentUserNews();
        return currentUserNews;
    }
    
    public void setCurrentImage(Integer id) {
        
        renderComments = true;
        currImageIndex = -1;
        for (int i = 0; i < currentUserNews.size(); ++i) {
            if (currentUserNews.get(i).getId().intValue() == id.intValue()) {
                currImageIndex = i;
                break;
            }
        }
        currSrc = currentUserNews.get(currImageIndex).getSrc();
        RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
    }
    
    
    public String currentPostDate() {
        if (currImageIndex < 0 || currImageIndex >= currentUserNews.size() || currentUserNews.get(currImageIndex).getLocation() == null) 
            return " ";
        return currentUserNews.get(currImageIndex).getDate().toString();
    }
    
    // likes
    
    @EJB
    private PostLikeService postLikeDAO;
    
    private static List<String> currentPostLikes;
    
    public List<String> getCurrentPostLikes() {
        if (currImageIndex < 0 || currImageIndex >= currentUserNews.size()) return null;
        currentPostLikes = postLikeDAO.getCurrentPostLikes(currentUserNews.get(currImageIndex).getId());
        return currentPostLikes;
    }    
   
    public void addLike() {        
        if (currentPostLikes == null) 
            return;        
        try {
            String author = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
            Integer postID = currentUserNews.get(currImageIndex).getId();        
            if (currentPostLikes.contains(author)) {
                postLikeDAO.removeLike(author, postID);
            }
            else {
                postLikeDAO.addLike(author, postID);
                eventDAO.createEvent(currentUserNews.get(currImageIndex).getUsername().getUsername(), "on_like", postID);
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
    
    public int likesCount() {
        return currentPostLikes.size();
    }
    
    public String likeState() {
        return (currentPostLikes.contains(((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername()) ? "dislike-button" : "like-button");
    }
    
    
    // comments
    
    @EJB
    private CommentService commentDAO;
    
    private static List<Comment> currentPostComments;
    
    public List<Comment> getCurrentPostComments() {
        if (currImageIndex < 0 || currImageIndex >= currentUserNews.size()) return null;
        currentPostComments = commentDAO.getCurrentPostComments(currentUserNews.get(currImageIndex).getId());
        renderComments1 = true;
        getCurrentPostLikes();
        RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
        return currentPostComments;
    }    

    public String currentPostText() {
        if (currImageIndex < 0 || currImageIndex >= currentUserNews.size()) return "";
        return currentUserNews.get(currImageIndex).getText();
    }
    
    public String currentPostLocation() {
        if (currImageIndex < 0 || currImageIndex >= currentUserNews.size() || currentUserNews.get(currImageIndex).getLocation() == null) 
            return " ";
        return currentUserNews.get(currImageIndex).getLocation();
    }
    
    private static boolean renderComments = false;

    public boolean isRenderComments() {
        return renderComments;
    }

    public void setRenderComments(boolean renderComments) {
        NewsBean.renderComments = renderComments;
    }
    
    private static boolean renderComments1 = false;

    public boolean isRenderComments1() {
        return renderComments1;
    }

    public void setRenderComments1(boolean renderComments1) {
        NewsBean.renderComments1 = renderComments1;
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
            Integer postID = currentUserNews.get(currImageIndex).getId();
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
        if (currentPostComments == null)
            return 0;
        return currentPostComments.size();
    }    
    
    
    // media
    
    public String forImage(String filename) {
        return (filename.contains(".mp4") ? "display: none;" : "display: block;");
    }
    
    public String forVideo(String filename) {
        return (filename.contains(".mp4") ? "display: block;" : "display: none;");
    }
    
    public String preview(String filename) {
        return (filename.contains(".mp4") ? "video_preview.jpeg" : filename);
    }
    
    
    // events
    
    @EJB
    private EventService eventDAO;
    
    private List<Event> eventList;

    public List<Event> getEventList() {
        eventList = eventDAO.getCurrentUserEvents();
        return eventList;
    } 
    
}
