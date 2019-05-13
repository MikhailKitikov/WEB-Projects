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
import mksoft.sharemoments.entity.Comment;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.PostLike;
import mksoft.sharemoments.entity.User;

/**
 *
 * @author mk99
 */
@Stateless
public class PostLikeDAO {

   @PersistenceContext(unitName = "profiles_persist")
    private EntityManager entityManager;
   
    public boolean addLike(String from, Integer targPostID) {
        if (getCurrentPostLikes(targPostID).contains(from)) {
            return false;
        }
        PostLike like = new PostLike(100);
        like.setPostID(new PhotoPost(targPostID));
        like.setAuthorName(from);
        entityManager.persist(like);
        return true;
    }
    
    public boolean removeLike(String from, Integer targPostID) {
        if (!getCurrentPostLikes(targPostID).contains(from)) {
            return false;
        }
        Query query = entityManager.createNamedQuery("PostLike.removeLike");
        query.setParameter("authorName", from);
        query.setParameter("postID", new PhotoPost(targPostID));
        query.executeUpdate();
        return true;
    }

    public List<String> getCurrentPostLikes(Integer postID){              
        Query query = entityManager.createNamedQuery("PostLike.findByPost").setParameter("postID", new PhotoPost(postID));
        return query.getResultList();
    }
   
}

