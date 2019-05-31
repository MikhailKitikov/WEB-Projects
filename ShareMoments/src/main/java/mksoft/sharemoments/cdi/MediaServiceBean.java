package mksoft.sharemoments.cdi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import mksoft.sharemoments.ejb.PhotoPostService;
import mksoft.sharemoments.ejb.UserService;
import mksoft.sharemoments.entity.User;
import mksoft.sharemoments.entity.UserData;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;


/**
 *
 * @author mk99
 */
@ManagedBean(name = "mediaServiceBean", eager = true)
@SessionScoped
public class MediaServiceBean implements Serializable {
    
    private String filename, tempFilename;
    private UploadedFile file;
    private String postDescription;
    private final String destination = "/home/mk99/web/data/images/",
            tempDestination = "/home/mk99/web/data/images/temp/",
            avatarDestination = "/home/mk99/web/data/images/avatars/";    
    
    @EJB
    private PhotoPostService photoPostDAO;
 
    public void upload(FileUploadEvent event) {     
        
        try {
            file = event.getFile();
            tempFilename = file.getFileName();
            copyFile(tempDestination + tempFilename, file.getInputstream());
        }
        catch (IOException e) {
            e.getMessage();
        }
    }
    
    public void submit() {     
        
        try {
            String username = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
            filename = username + "_" + String.valueOf(new Date().getTime()) + tempFilename.substring(tempFilename.lastIndexOf('.'));
            copyFile(destination + filename, file.getInputstream());
            photoPostDAO.createPost(filename, postDescription, location);
            
            File old_file = new File(tempDestination + tempFilename); 
            old_file.delete();
        }
        catch (IOException e) {
            e.getMessage();
        }
        catch (EJBException e) {
            @SuppressWarnings("ThrowableResultIgnored")
            Exception cause = e.getCausedByException();
            if (cause instanceof ConstraintViolationException) {
                @SuppressWarnings("ThrowableResultIgnored")
                ConstraintViolationException cve = (ConstraintViolationException) e.getCausedByException();
                for (Iterator<ConstraintViolation<?>> it = cve.getConstraintViolations().iterator(); it.hasNext();) {
                    ConstraintViolation<? extends Object> v = it.next();
                    System.err.println(v);
                    System.err.println("==>>"+v.getMessage());
                }
            }
            Assert.fail("ejb exception");
        }
    }
 
    public boolean copyFile(String path, InputStream in) {
        
        try {
            OutputStream out = new FileOutputStream(new File(path)); 
            int read = 0;
            byte[] bytes = new byte[1024]; 
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            } 
            in.close();
            out.flush();
            out.close();
            return true;
        } 
        catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    //
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }   

    public String getTempFilename() {
        return tempFilename;
    }

    public void setTempFilename(String tempFilename) {
        this.tempFilename = tempFilename;
    }
    
    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }
    
    // avatar
    
    @EJB
    private UserService userDAO;
    
    public boolean toUpdate = false;

    public boolean isToUpdate() {
        if (toUpdate == true) {
            toUpdate = false;
            return true;
        }
        return toUpdate;
    }

    public void setToUpdate(boolean toUpdate) {
        this.toUpdate = toUpdate;
    }
    
    public String toReload() {
        if (isToUpdate())
            return "location.reload();";
        else
            return "";
    }
    
    public void uploadAvatar(FileUploadEvent event) {     
        
        try {
            String username = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
            UserData userData = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUserData();
            UploadedFile avatarFile = event.getFile();
            if (userData == null)
                return;
            File old_file = new File(avatarDestination + userData.getAvatar()); 
            old_file.delete();
            
            String avatarFilename = username + "_av" + avatarFile.getFileName().substring(avatarFile.getFileName().lastIndexOf('.'));
            copyFile(avatarDestination + avatarFilename, avatarFile.getInputstream());
            userDAO.changeAvatar(avatarFilename);
            User user = userDAO.userObject(username);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("username");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("currViewUser");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", user);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("currViewUser", user);
            RequestContext.getCurrentInstance().update(":avatar");
            toUpdate = true;
        }
        catch (IOException e) {
            e.getMessage();
        }
    }
    
    // location for post
    
    private String location;
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    private List<String> locations;

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
    
    @PostConstruct
    public void init() {
        locations  = new ArrayList<>();
        locations.add("Belarus, Minsk");
        locations.add("Germany, Berlin");
        locations.add("USA, New York");
    }
    
}
