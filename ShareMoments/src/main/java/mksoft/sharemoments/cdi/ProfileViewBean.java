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
import mksoft.sharemoments.ejb.UserService;
import mksoft.sharemoments.entity.Comment;
import mksoft.sharemoments.entity.Event;
import mksoft.sharemoments.entity.Follower;
import mksoft.sharemoments.entity.PhotoPost;
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
    
    // main info
    
    public String showUsername() {
        return ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername();
    }
    
    public String showName() {
        UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUserData();
        if (userData == null)
            return "";
        return userData.getName();
    }
    
    public String showBio() {
        UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUserData();
        if (userData == null)
            return "";
        return userData.getBio();
    }
    
    public String showAvatar() {
        UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUserData();
        if (userData == null || userData.getAvatar() == null)
            return "demo.png";
        return userData.getAvatar();
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
    private PostLikeService postLikeDAO;
    
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
            if (currentPostLikes.contains(author)) {
                postLikeDAO.removeLike(author, postID);
            }
            else {
                postLikeDAO.addLike(author, postID);
                eventDAO.createEvent(currentUserPhotoPosts.get(currImageIndex).getUsername().getUsername(), "on_like", postID);
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
        if (currImageIndex < 0 || !renderComments || currentUserPhotoPosts == null || currImageIndex >= currentUserPhotoPosts.size()) return null;
        currentPostComments = commentDAO.getCurrentPostComments(currentUserPhotoPosts.get(currImageIndex).getId());
        renderComments1 = true;
        getCurrentPostLikes();
        RequestContext.getCurrentInstance().update("commentsFormID:like-comment-label");
        return currentPostComments;
    }    

    public String currentPostText() {
        if (currImageIndex < 0 || currImageIndex >= currentUserPhotoPosts.size()) return "";
        return currentUserPhotoPosts.get(currImageIndex).getText();
    }
    
    public String currentPostLocation() {
        if (currImageIndex < 0 || currImageIndex >= currentUserPhotoPosts.size() || currentUserPhotoPosts.get(currImageIndex).getLocation() == null) 
            return " ";
        return currentUserPhotoPosts.get(currImageIndex).getLocation();
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
        if (currentPostComments == null)
            return 0;
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
        String st = "position: fixed; right: 5%; height: 40px; width: 40px; font: 8pt grey; top: 0; bottom: 0; margin-top: auto; margin-bottom: auto; padding: 0;";
        return (currImageIndex == currentUserPhotoPosts.size() - 1 ? "display: none;" + st : "display: block;" + st);
    }
    
    public String prevImageAvailable() {
        String st = "position: fixed; left: 5%; height: 40px; width: 40px; font: 8pt grey; top: 0; bottom: 0; margin-top: auto; margin-bottom: auto; padding: 0;";
        return (currImageIndex == 0 ? "display: none;"+ st : "display: block;"+ st);
    }
    
    
    // followers
    
    @EJB
    private FollowerService followerDAO;
    
    public String followable() {
        return (FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username").equals(FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")) ? "display: none;" : "display: block;");
    }
    
    public int followingsCount() {
        String who = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername();
        return followerDAO.getUserFollowings(who).size();
    }
    
    public int followersCount() {
        String whom = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername();
        return followerDAO.getUserFollowers(whom).size();
    }
    
    public void followUnfollow() {
        String who = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
        String whom = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername();
        if (followerDAO.followUnfollow(who, whom))
            eventDAO.createEvent(((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername(), "on_follow", 72);
    }
    
    public String followState() {
        String who = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
        String whom = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername();
        return (followerDAO.isFollowing(who, whom) ? "do not follow" : "follow");
    }
    
    private List<Follower> currentUserFollowers;

    public List<Follower> getCurrentUserFollowers() {
        currentUserFollowers = followerDAO.getUserFollowers(((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername());
        return currentUserFollowers;
    }
    
    @EJB
    private UserService userDAO;
    
    private List<Follower> currentUserFollowings;

    public List<Follower> getCurrentUserFollowings() {
        currentUserFollowings = followerDAO.getUserFollowings(((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser")).getUsername());
        return currentUserFollowings;
    }
    
    public String getSomeAvatar(String username) {
        return userDAO.userObject(username).getUserData().getAvatar();
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
    
    public String forEvent(Event event) {
        if (event.getEventType().equals("on_follow")) {
            return "display: none;";
        }
        else {
            return "display: inline;";
        }
    }

}
