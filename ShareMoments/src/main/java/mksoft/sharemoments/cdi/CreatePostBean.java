package mksoft.sharemoments.cdi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import mksoft.sharemoments.ejb.PhotoPostDAO;
import mksoft.sharemoments.ejb.UserDAO;
import mksoft.sharemoments.entity.User;
import org.eclipse.persistence.jpa.jpql.Assert;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "createPostBean", eager = true)
@SessionScoped
public class CreatePostBean implements Serializable {
    
    private String filename, tempFilename;
    private UploadedFile file;
    private String postDescription;
    private final String destination = "/home/mk99/web/data/images/",
            tempDestination = "/home/mk99/web/data/images/temp/",
            avatarDestination = "/home/mk99/web/data/images/avatars/";    
    
    @EJB
    private PhotoPostDAO photoPostDAO;
 
    public void upload(FileUploadEvent event) {     
        
        try {
            file = event.getFile();
            tempFilename = file.getFileName();
            System.out.println("temp filename: " + tempFilename);
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
            System.out.println("filename: " + filename);
            copyFile(destination + filename, file.getInputstream());
            photoPostDAO.createPost(filename, postDescription);
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
    private UserDAO userDAO;
    
    public void uploadAvatar(FileUploadEvent event) {     
        
        try {
            String username = ((User)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username")).getUsername();
            UploadedFile avatarFile = event.getFile();
            String avatarFilename = username + "_av" + avatarFile.getFileName().substring(avatarFile.getFileName().lastIndexOf('.'));
            copyFile(avatarDestination + avatarFilename, avatarFile.getInputstream());
            userDAO.changeAvatar(avatarFilename);
            RequestContext.getCurrentInstance().update(":avatar");
        }
        catch (IOException e) {
            e.getMessage();
        }
    }
    
}
