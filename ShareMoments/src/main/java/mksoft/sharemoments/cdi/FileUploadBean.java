package mksoft.sharemoments.cdi;

import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author mk99
 */
@ManagedBean(name = "fileUploadBean", eager = true)
@SessionScoped
public class FileUploadBean {
     
    private UploadedFile file = null;
 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
     
    public void upload() {
        if (file == null || file.getFileName().length() == 0) 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Failed", "No file uploaded"));
        else 
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Succesful", file.getFileName() + " is uploaded."));
    }
}
