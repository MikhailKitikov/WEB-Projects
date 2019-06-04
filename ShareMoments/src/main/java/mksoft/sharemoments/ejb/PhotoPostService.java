package mksoft.sharemoments.ejb;

import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.entity.Follower;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.User;

/**
 *
 * @author mk99
 */
@Stateless
public class PhotoPostService {

   @PersistenceContext(unitName = "profiles_persist")
    private EntityManager entityManager;
   
    public void createPost(String path, String postDescription, String location) {
        
        PhotoPost photoPost = new PhotoPost();
        photoPost.setUsername((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username"));
        photoPost.setText(postDescription);
        photoPost.setSrc(path);
        photoPost.setLocation(location);
        photoPost.setDate(new Timestamp(new Date().getTime()));
        entityManager.persist(photoPost);
    }

    public List<PhotoPost> getCurrentUserPhotoPosts() {    
        
        FacesContext facesContext = FacesContext.getCurrentInstance();   
        Query query = entityManager.createNamedQuery("PhotoPost.findByUsername").setParameter("username", (User)facesContext.getExternalContext().getSessionMap().get("currViewUser"));
        List<PhotoPost> res = query.getResultList();
        Collections.sort(res);
        return res;
    }
    
    public List<PhotoPost> getCurrentUserNews() {    
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String username = ((User)facesContext.getExternalContext().getSessionMap().get("username")).getUsername();
        
        List<PhotoPost> res = new ArrayList<>();
        Query follower_query = entityManager.createNamedQuery("Follower.findByWhoName").setParameter("whoName", username);
        for (Object friend : follower_query.getResultList()) {
            Query post_query = entityManager.createNamedQuery("PhotoPost.findByUsername").setParameter("username", new User(((Follower)friend).getWhomName()));
            res.addAll(post_query.getResultList());
        }        
        Collections.sort(res);
        return res;
    }
    
    public List<PhotoPost> getAllPhotoPosts() {    
        
        Query query = entityManager.createNamedQuery("PhotoPost.findAll");
        return query.getResultList();
    }
}

