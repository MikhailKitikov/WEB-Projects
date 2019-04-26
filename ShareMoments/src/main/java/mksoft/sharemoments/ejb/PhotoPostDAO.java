package mksoft.sharemoments.ejb;

/**
 *
 * @author mk99
 */
import org.apache.commons.lang3.StringUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.User;

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

    public List<PhotoPost> getCurrentUserAllPosts(){
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String username = (String)facesContext.getExternalContext().getSessionMap().get("username");        
        Query query = entityManager.createNamedQuery("PhotoPost.findByUsername").setParameter("username", new User(username));
        return query.getResultList();
    }
}

