package mksoft.sharemoments.ejb;

import org.apache.commons.lang3.StringUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import mksoft.sharemoments.entity.User;

@Stateless
public class UserDAO {

    @PersistenceContext(unitName = "profiles_persist")
    private EntityManager entityManager;

    public int checkPassword(String username, String password) {

        List<Object> resultList = entityManager.createNamedQuery("User.findByUsername").setParameter("username", username).getResultList();
        if (resultList.isEmpty()) {
            return 1;
        }
        User currentUser = (User)resultList.get(0);
        if(currentUser == null){
            return 1;
        }
        if(!password.equals(currentUser.getPassword())){
            return 2;            
        }
        return 0;
    }

    public boolean createUser(String username, String password) {
        
        User user = entityManager.find(User.class, username);
        if(user != null){
            return false;
        }
        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        entityManager.persist(user);
        return true;
    }

    public List<User> getAllUsers(){
        
        Query query = entityManager.createNamedQuery("User.findAll");
        return query.getResultList();
    }
}
