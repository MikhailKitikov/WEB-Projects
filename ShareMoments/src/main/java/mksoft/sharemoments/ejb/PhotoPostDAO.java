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
        
        PhotoPost photoPost = new PhotoPost();
        photoPost.setUsername((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username"));
        photoPost.setText(postDescription);
        photoPost.setSrc(path);
        photoPost.setDate(new Timestamp(new Date().getTime()));
        entityManager.persist(photoPost);
    }

    public List<PhotoPost> getCurrentUserPhotoPosts() {    
        
        FacesContext facesContext = FacesContext.getCurrentInstance();   
        Query query = entityManager.createNamedQuery("PhotoPost.findByUsername").setParameter("username", (User)facesContext.getExternalContext().getSessionMap().get("currViewUser"));
        return query.getResultList();
    }
    
    public List<PhotoPost> getAllPhotoPosts() {    
        
        Query query = entityManager.createNamedQuery("PhotoPost.findAll");
        return query.getResultList();
    }
}

