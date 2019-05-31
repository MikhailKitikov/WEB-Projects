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
import mksoft.sharemoments.entity.User;

/**
 *
 * @author mk99
 */
@Stateless
public class CommentService {

   @PersistenceContext(unitName = "profiles_persist")
    private EntityManager entityManager;
   
    public void createComment(String from, Integer targPost, String text) {
        Comment comment = new Comment(100);
        comment.setPostID(new PhotoPost(targPost));
        comment.setAuthorName(from);
        comment.setText(text);
        entityManager.persist(comment);
    }

    public List<Comment> getCurrentPostComments(Integer postID){              
        Query query = entityManager.createNamedQuery("Comment.findByPost").setParameter("postID", new PhotoPost(postID));
        return query.getResultList();
    }
   
}

