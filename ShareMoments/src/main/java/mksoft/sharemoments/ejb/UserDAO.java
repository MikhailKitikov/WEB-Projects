package mksoft.sharemoments.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

    public List<User> getAllUsers() {        
        Query query = entityManager.createNamedQuery("User.findAll");
        return query.getResultList();
    }
    
    public List<String> getAllUsernames() {        
        Query query = entityManager.createNamedQuery("User.findAllUsernames");
        return query.getResultList();
    }
    
//    public List<User> searchUser(String name) {
//        
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        
//        CriteriaQuery<User> query = cb.createQuery(User.class);
//        //From clause
//        Root<User> personRoot = query.from(User.class);
//
//        //Where clause
//        query.where(
//            //Like predicate
//            cb.like(
//                //assuming 'lastName' is the property on the Person Java object that is mapped to the last_name column on the Person table.
//                personRoot.<String>get("username"),
//                //Add a named parameter called likeCondition
//                cb.parameter(String.class, "likeCondition")));
//
//        TypedQuery<User> tq = entityManager.createQuery(query);
//        String pattern = "%" + name + "%";
//        tq.setParameter("likeCondition", pattern);
//        List<User> people = tq.getResultList();
//        return people;
//    }
}
    
