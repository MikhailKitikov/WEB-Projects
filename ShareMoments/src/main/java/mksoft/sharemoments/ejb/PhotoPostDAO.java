package mksoft.sharemoments.ejb;

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

//    public boolean createUser(String username, String password) {
//        
//        User user = entityManager.find(User.class, username);
//        if(user != null){
//            return false;
//        }
//        user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        entityManager.persist(user);
//        return true;
//    }

    public List<PhotoPost> getCurrentUserPhotoPosts(){        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String username = (String)facesContext.getExternalContext().getSessionMap().get("currViewUser");        
        Query query = entityManager.createNamedQuery("PhotoPost.findByUsername").setParameter("username", new User(username));
        return query.getResultList();
    }
    
    public List<PhotoPost> getAllPhotoPosts(){              
        Query query = entityManager.createNamedQuery("PhotoPost.findAll");
        return query.getResultList();
    }
}

