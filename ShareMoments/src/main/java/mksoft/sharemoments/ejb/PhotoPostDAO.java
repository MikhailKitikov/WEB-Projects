package mksoft.sharemoments.ejb;

import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.User;

/**
 *
 * @author mk99
 */
@Stateless
public class PhotoPostDAO {

   @PersistenceContext(unitName = "profiles_persist")
    private EntityManager entityManager;
   
    public void createPost(String path, String postDescription) {
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String username = (String)facesContext.getExternalContext().getSessionMap().get("username");
        PhotoPost photoPost = new PhotoPost();
        photoPost.setUsername(new User(username));
        photoPost.setText(postDescription);
        photoPost.setSrc(path);
        photoPost.setDate(new Timestamp(new Date().getTime()));
        entityManager.persist(photoPost);
    }

    public List<PhotoPost> getCurrentUserPhotoPosts() {    
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String username = (String)facesContext.getExternalContext().getSessionMap().get("currViewUser");        
        Query query = entityManager.createNamedQuery("PhotoPost.findByUsername").setParameter("username", new User(username));
        return query.getResultList();
    }
    
    public List<PhotoPost> getAllPhotoPosts() {    
        
        Query query = entityManager.createNamedQuery("PhotoPost.findAll");
        return query.getResultList();
    }
}

