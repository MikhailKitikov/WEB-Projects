package mksoft.sharemoments.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mk99
 */
@Entity
@Table(name = "Comment")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comment.findByPost", query = "SELECT c FROM Comment c WHERE c.postID = :postID")
    , @NamedQuery(name = "Comment.findById", query = "SELECT c FROM Comment c WHERE c.id = :id")})
public class Comment implements Serializable {

    @Size(max = 60)
    @Column(name = "author_name")
    private String authorName;

    @Size(max = 255)
    @Column(name = "text")
    private String text;

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "postID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PhotoPost postID;

    public Comment() {
    }

    public Comment(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PhotoPost getPostID() {
        return postID;
    }

    public void setPostID(PhotoPost postID) {
        this.postID = postID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mksoft.sharemoments.entity.Comment[ id=" + id + " ]";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
}
