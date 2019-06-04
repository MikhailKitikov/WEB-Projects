package mksoft.sharemoments.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name = "PostLike")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "PostLike.findAll", query = "SELECT p FROM PostLike p")
    , @NamedQuery(name = "PostLike.removeLike", query = "DELETE FROM PostLike p WHERE p.authorName = :authorName AND p.postID = :postID")
    , @NamedQuery(name = "PostLike.findByPost", query = "SELECT p.authorName.username FROM PostLike p WHERE p.postID = :postID")
    , @NamedQuery(name = "PostLike.findById", query = "SELECT p FROM PostLike p WHERE p.id = :id")
    , @NamedQuery(name = "PostLike.findByAuthorName", query = "SELECT p FROM PostLike p WHERE p.authorName = :authorName")})
public class PostLike implements Serializable {

    @JoinColumn(name = "author_name", referencedColumnName = "username")
    @ManyToOne(optional = false)
    private User authorName;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "postID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private PhotoPost postID;

    public PostLike() {
    }

    public PostLike(Integer id) {
        this.id = id;
    }

    public PostLike(Integer id, User authorName) {
        this.id = id;
        this.authorName = authorName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getAuthorName() {
        return authorName;
    }

    public void setAuthorName(User authorName) {
        this.authorName = authorName;
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
        if (!(object instanceof PostLike)) {
            return false;
        }
        PostLike other = (PostLike) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "mksoft.sharemoments.entity.PostLike[ id=" + id + " ]";
    }
    
}
