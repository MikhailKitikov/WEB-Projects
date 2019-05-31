package mksoft.sharemoments.ejb;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mksoft.sharemoments.entity.Event;
import mksoft.sharemoments.entity.PhotoPost;
import mksoft.sharemoments.entity.User;
import mksoft.sharemoments.entity.UserData;

@Stateless
public class EventService {

    @PersistenceContext(unitName = "profiles_persist")
    private EntityManager entityManager;


    public void createEvent(String to, String event_type, Integer postID) {
        
        Event event = new Event();
        event.setAuthor((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username"));
        event.setUsername(new User(to));
        event.setEventType(event_type);
        event.setPostID(new PhotoPost(postID));
        entityManager.persist(event);
    }

    public List<Event> getCurrentUserEvents() {    
        
        FacesContext facesContext = FacesContext.getCurrentInstance();   
        Query query = entityManager.createNamedQuery("Event.findByUsername").setParameter("username", (User)facesContext.getExternalContext().getSessionMap().get("username"));
        return query.getResultList();
    }
}
    
