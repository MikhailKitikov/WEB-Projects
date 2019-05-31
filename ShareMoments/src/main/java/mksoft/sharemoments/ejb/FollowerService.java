package mksoft.sharemoments.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import mksoft.sharemoments.entity.Follower;

/**
 *
 * @author mk99
 */
@Stateless
public class FollowerService {

   @PersistenceContext(unitName = "profiles_persist")
    private EntityManager entityManager;
   
    public boolean isFollowing(String who, String whom) {
        Query query = entityManager.createNamedQuery("Follower.find").setParameter("whoname", who).setParameter("whomname", whom);
        return query.getResultList().size() == 1;
    }
    
    public boolean followUnfollow(String who, String whom) {
        if (isFollowing(who, whom)) {
            Query query = entityManager.createNamedQuery("Follower.remove").setParameter("whoname", who).setParameter("whomname", whom);
            query.executeUpdate();
            return false;
        }
        Follower follower = new Follower();
        follower.setWhoName(who);
        follower.setWhomName(whom);
        entityManager.persist(follower);
        return true;
    }   

    public List<Follower> getUserFollowers(String username){              
        Query query = entityManager.createNamedQuery("Follower.findByWhomName").setParameter("whomName", username);
        return query.getResultList();
    }
    
    public List<Follower> getUserFollowings(String username){              
        Query query = entityManager.createNamedQuery("Follower.findByWhoName").setParameter("whoName", username);
        return query.getResultList();
    }
   
}

