package mksoft.sharemoments.ejb;

import java.util.ArrayList;
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
import mksoft.sharemoments.entity.User;
import mksoft.sharemoments.entity.UserData;

@Stateless
public class UserService {

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

    public boolean createUser(String username, String password, String name, String bio) {
        
        User user = entityManager.find(User.class, username);
        if(user != null){
            return false;
        }
        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        entityManager.persist(user);
        
        UserData userData = new UserData(username);
        userData.setName(name);
        userData.setBio(bio);
        entityManager.persist(userData);
        
        return true;
    }
    
    public void changeAvatar(String avatarSrc) {
        
        User user = (User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        UserData userData = entityManager.find(UserData.class, user.getUsername());
        if (userData == null)
            return;
        Query query = entityManager.createNamedQuery("UserData.changeAvatar").setParameter("avatar", avatarSrc).setParameter("username", user.getUsername());
        query.executeUpdate();
    }
    
    public void changeInfo(String name, String bio) {
        
        User user = (User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        UserData userData = entityManager.find(UserData.class, user.getUsername());
        if (userData == null)
            return;
        Query query = entityManager.createNamedQuery("UserData.changeInfo");
        query.setParameter("username", user.getUsername());
        query.setParameter("name", name);
        query.setParameter("bio", bio);
        query.executeUpdate();
    }
    
    public void changePassword(String password) {
        
        User user = (User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        UserData userData = entityManager.find(UserData.class, user.getUsername());
        if (userData == null)
            return;
        Query query = entityManager.createNamedQuery("UserData.changePssword");
        query.setParameter("username", user.getUsername());
        query.setParameter("password", password);
        query.executeUpdate();
    }
    
    //

    public List<User> getAllUsers() {        
        Query query = entityManager.createNamedQuery("User.findAll");
        return query.getResultList();
    }
    
    public List<String> getAllUsernames() {        
        Query query = entityManager.createNamedQuery("User.findAllUsernames");
        return query.getResultList();
    }
    
    public List<String> searchUser(String name) {        
        Query query = entityManager.createNamedQuery("User.findLike").setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
    
    public User userObject(String username) {
        UserData ud = entityManager.find(UserData.class, username); 
        User user = entityManager.find(User.class, username);
        user.setUserData(ud);
        return user;      
    }
}
    
