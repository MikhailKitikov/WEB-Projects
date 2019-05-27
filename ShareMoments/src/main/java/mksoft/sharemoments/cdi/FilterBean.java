package mksoft.sharemoments.cdi;

import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import mksoft.sharemoments.entity.User;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "filter", eager = true)
@SessionScoped
public class FilterBean implements Serializable {
    
    public String f() {
        User user = (User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        User viewUser = (User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("currViewUser");
        if (user.equals(viewUser))
            return "";
        else
            return "return false;";
    }
}
