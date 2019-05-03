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
   
    public void createPost(String path) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String username = (String)facesContext.getExternalContext().getSessionMap().get("currViewUser");
        PhotoPost photoPost = new PhotoPost();
        photoPost.setUsername(new User(username));
        photoPost.setText("hello");
        photoPost.setSrc(path);
//        try {
//            DateFormat formatter;
//            formatter = new SimpleDateFormat("dd/MM/yyyy");
//             // you can change format of date
//            Date date = formatter.parse("2019-04-28 13:57:31.000");
//            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());
//            
//            photoPost.setDate(date);
//          } catch (ParseException e) {
//            System.out.println("Exception :" + e);
//            return ;
//          }
        photoPost.setDate(new Date(100, 1, 1, 1, 1, 1));
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" + photoPost.getDate());
        entityManager.persist(photoPost);
    }

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

