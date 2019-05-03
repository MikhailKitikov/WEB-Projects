package mksoft.sharemoments.cdi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import mksoft.sharemoments.ejb.PhotoPostDAO;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.persistence.jpa.jpql.Assert;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "createPostBean", eager = true)
@ViewScoped
public class CreatePostBean implements Serializable{
    
    private String filename;
    private UploadedFile file;
    private String destination = "/tmp/images/";
    
    @EJB
    private PhotoPostDAO photoPostDAO;
 
    public void upload(FileUploadEvent event) {     
        
        try {
            file = event.getFile();
            filename = file.getFileName();
            copyFile(filename, event.getFile().getInputstream());
            photoPostDAO.createPost("res.png");
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
 
    public boolean copyFile(String fileName, InputStream in) {
        
        try {
            OutputStream out = new FileOutputStream(new File("/tmp/images/res.png")); 
            int read = 0;
            byte[] bytes = new byte[1024];
 
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
 
            in.close();
            out.flush();
            out.close();
            
            FacesContext.getCurrentInstance().addMessage("Completed", new FacesMessage(null, "PhotoPost created!"));
            return true;
        } 
        catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
//    public String src() {
//        if (filename == null) return "resources/images/logo.png";
//        return destination + "res.png";
//    }
    
    //
    
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }   

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
}
